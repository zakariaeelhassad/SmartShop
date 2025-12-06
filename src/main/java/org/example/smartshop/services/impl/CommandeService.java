package org.example.smartshop.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.commandes.CommandeCreateDto;
import org.example.smartshop.entities.DTO.commandes.CommandeResponseDto;
import org.example.smartshop.entities.DTO.commandes.CommandeUpdateDto;
import org.example.smartshop.entities.DTO.orderItem.OrderItemCreateDto;
import org.example.smartshop.entities.enums.CustomerTier;
import org.example.smartshop.entities.enums.OrderStatus;
import org.example.smartshop.entities.models.Client;
import org.example.smartshop.entities.models.Commande;
import org.example.smartshop.entities.models.OrderItem;
import org.example.smartshop.entities.models.Product;
import org.example.smartshop.exceptions.BusinessException;
import org.example.smartshop.exceptions.ResourceNotFoundException;
import org.example.smartshop.mappers.CommandeMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.repository.CommandeRepository;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.services.ICommandeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService implements ICommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final CommandeMapper commandeMapper;
    private final AuthService authService;

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");

    @Override
    @Transactional
    public CommandeResponseDto createCommande(CommandeCreateDto createDto) {
        authService.requireAdminRole();
        Client client = clientRepository.findById(createDto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemCreateDto itemDto : createDto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé: " + itemDto.productId()));

            if (product.getStock() < itemDto.quantity()) {
                throw new BusinessException("Stock insuffisant pour le produit: " + product.getName()
                        + ". Disponible: " + product.getStock() + ", Demandé: " + itemDto.quantity());
            }

            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(new BigDecimal(itemDto.quantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.quantity())
                    .unitPrice(product.getUnitPrice())
                    .totalLine(lineTotal)
                    .build();

            orderItems.add(orderItem);
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal discountAmount = calculateDiscount(client, subtotal, createDto.promoCode());
        BigDecimal amountAfterDiscount = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = amountAfterDiscount.multiply(TVA_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amountAfterDiscount.add(tax).setScale(2, RoundingMode.HALF_UP);

        Commande commande = Commande.builder()
                .client(client)
                .dateCreated(LocalDateTime.now())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .discountAmount(discountAmount)
                .tax(tax)
                .total(total)
                .promoCode(createDto.promoCode())
                .status(OrderStatus.PENDING)
                .remainingAmount(total)
                .items(orderItems)
                .build();

        orderItems.forEach(item -> item.setCommande(commande));

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toDto(savedCommande);
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeResponseDto getCommandeById(Long id) {
        authService.requireAdminRole();
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        return commandeMapper.toDto(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeResponseDto> getAllCommandes() {
        authService.requireAdminRole();
        return commandeRepository.findAll().stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeResponseDto> getCommandesByClient(Long clientId) {
        authService.requireAdminRole();
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client non trouvé");
        }
        return commandeRepository.findByClientId(clientId).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommandeResponseDto updateCommandeStatus(Long id, CommandeUpdateDto updateDto) {
        authService.requireAdminRole();
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Impossible de modifier une commande avec le statut: " + commande.getStatus());
        }

        if (updateDto.status() == OrderStatus.CONFIRMED) {
            return confirmCommande(id);
        } else if (updateDto.status() == OrderStatus.CANCELED) {
            return cancelCommande(id);
        } else {
            throw new BusinessException("Transition de statut non autorisée: " + updateDto.status());
        }
    }

    @Override
    @Transactional
    public CommandeResponseDto confirmCommande(Long id) {
        authService.requireAdminRole();
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes en statut PENDING peuvent être confirmées");
        }

        if (commande.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("La commande doit être totalement payée avant validation. Montant restant: "
                    + commande.getRemainingAmount() + " DH");
        }

        commande.setStatus(OrderStatus.CONFIRMED);
        Commande savedCommande = commandeRepository.save(commande);

        updateClientStatistics(commande.getClient().getId());

        return commandeMapper.toDto(savedCommande);
    }

    @Override
    @Transactional
    public CommandeResponseDto cancelCommande(Long id) {
        authService.requireAdminRole();
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes en statut PENDING peuvent être annulées");
        }

        for (OrderItem item : commande.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        commande.setStatus(OrderStatus.CANCELED);
        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toDto(savedCommande);
    }

    private BigDecimal calculateDiscount(Client client, BigDecimal subtotal, String promoCode) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client.getTier(), subtotal);

        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (promoCode != null && !promoCode.isEmpty()) {
            if (commandeRepository.existsByPromoCode(promoCode)) {
                throw new BusinessException("Ce code promo a déjà été utilisé");
            }
            promoDiscount = subtotal.multiply(new BigDecimal("0.05"));
        }

        discountAmount = loyaltyDiscount.add(promoDiscount).setScale(2, RoundingMode.HALF_UP);

        return discountAmount;
    }

    private BigDecimal calculateLoyaltyDiscount(CustomerTier tier, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (tier) {
            case SILVER:
                if (subtotal.compareTo(new BigDecimal("500")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.05"));
                }
                break;
            case GOLD:
                if (subtotal.compareTo(new BigDecimal("800")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.10"));
                }
                break;
            case PLATINUM:
                if (subtotal.compareTo(new BigDecimal("1200")) >= 0) {
                    discount = subtotal.multiply(new BigDecimal("0.15"));
                }
                break;
            case BASIC:
            default:
                discount = BigDecimal.ZERO;
                break;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private void updateClientStatistics(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        Long totalOrders = commandeRepository.countConfirmedOrdersByClientId(clientId);
        BigDecimal totalSpent = commandeRepository.sumTotalSpentByClientId(clientId);

        client.setTotalOrders(totalOrders.intValue());
        client.setTotalSpent(totalSpent);

        CustomerTier newTier = calculateCustomerTier(totalOrders.intValue(), totalSpent);
        client.setTier(newTier);

        clientRepository.save(client);
    }

    private CustomerTier calculateCustomerTier(int totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        } else {
            return CustomerTier.BASIC;
        }
    }
}
