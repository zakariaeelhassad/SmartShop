package org.example.smartshop.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.paiments.PaiementCreateDto;
import org.example.smartshop.entities.DTO.paiments.PaiementResponseDto;
import org.example.smartshop.entities.enums.OrderStatus;
import org.example.smartshop.entities.enums.PaymentStatus;
import org.example.smartshop.entities.enums.PaymentType;
import org.example.smartshop.entities.models.Commande;
import org.example.smartshop.entities.models.Paiement;
import org.example.smartshop.exceptions.BusinessException;
import org.example.smartshop.exceptions.ResourceNotFoundException;
import org.example.smartshop.mappers.PaiementMapper;
import org.example.smartshop.repository.CommandeRepository;
import org.example.smartshop.repository.PaiementRepository;
import org.example.smartshop.services.IPaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementService implements IPaiementService {

    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementMapper paiementMapper;
    private final AuthService authService;

    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000");

    @Override
    @Transactional
    public PaiementResponseDto createPaiement(PaiementCreateDto createDto) {
        authService.requireAdminRole();

        Commande commande = commandeRepository.findById(createDto.commandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Impossible d'ajouter un paiement à une commande avec le statut: "
                    + commande.getStatus());
        }

        if (createDto.montant().compareTo(commande.getRemainingAmount()) > 0) {
            throw new BusinessException("Le montant du paiement (" + createDto.montant()
                    + " DH) dépasse le montant restant (" + commande.getRemainingAmount() + " DH)");
        }

        PaymentType paymentType = PaymentType.valueOf(createDto.typePaiement());
        validatePaymentType(paymentType, createDto);

        Integer nextNumero = paiementRepository.findMaxNumeroPaiementByCommandeId(createDto.commandeId()) + 1;
        PaymentStatus initialStatus = determineInitialStatus(paymentType);

        Paiement paiement = Paiement.builder()
                .commande(commande)
                .numeroPaiement(nextNumero)
                .montant(createDto.montant())
                .typePaiement(paymentType)
                .datePaiement(LocalDateTime.now())
                .statut(initialStatus)
                .reference(createDto.reference())
                .banque(createDto.banque())
                .dateEcheance(createDto.dateEcheance())
                .build();

        if (paymentType == PaymentType.ESPECES) {
            paiement.setDateEncaissement(LocalDateTime.now());
        }

        Paiement savedPaiement = paiementRepository.save(paiement);
        updateCommandeRemainingAmount(commande);

        return paiementMapper.toDto(savedPaiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponseDto> getPaiementsByCommande(Long commandeId) {
        authService.requireAdminRole();

        if (!commandeRepository.existsById(commandeId)) {
            throw new ResourceNotFoundException("Commande non trouvée");
        }

        return paiementRepository.findByCommandeId(commandeId).stream()
                .map(paiementMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponseDto getPaiementById(Long id) {
        authService.requireAdminRole();

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        return paiementMapper.toDto(paiement);
    }

    @Override
    @Transactional
    public PaiementResponseDto encaisserPaiement(Long paiementId) {
        authService.requireAdminRole();

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (paiement.getStatut() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Seuls les paiements en attente peuvent être encaissés. Statut actuel: "
                    + paiement.getStatut());
        }

        if (paiement.getCommande().getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("La commande associée n'est plus en statut PENDING");
        }

        paiement.setStatut(PaymentStatus.ENCAISSE);
        paiement.setDateEncaissement(LocalDateTime.now());

        Paiement savedPaiement = paiementRepository.save(paiement);
        updateCommandeRemainingAmount(paiement.getCommande());

        return paiementMapper.toDto(savedPaiement);
    }

    @Override
    @Transactional
    public PaiementResponseDto rejeterPaiement(Long paiementId) {
        authService.requireAdminRole();

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (paiement.getStatut() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Seuls les paiements en attente peuvent être rejetés. Statut actuel: "
                    + paiement.getStatut());
        }

        if (paiement.getCommande().getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("La commande associée n'est plus en statut PENDING");
        }

        paiement.setStatut(PaymentStatus.REJETE);

        Paiement savedPaiement = paiementRepository.save(paiement);
        updateCommandeRemainingAmount(paiement.getCommande());

        return paiementMapper.toDto(savedPaiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponseDto> getAllPaiements() {
        authService.requireAdminRole();

        return paiementRepository.findAll().stream()
                .map(paiementMapper::toDto)
                .toList();
    }

    private void validatePaymentType(PaymentType paymentType, PaiementCreateDto createDto) {
        switch (paymentType) {
            case ESPECES:
                if (createDto.montant().compareTo(ESPECES_LIMIT) > 0) {
                    throw new BusinessException("Le paiement en espèces ne peut pas dépasser "
                            + ESPECES_LIMIT + " DH (Art. 193 CGI). Montant demandé: " + createDto.montant() + " DH");
                }
                break;

            case CHEQUE:
                if (createDto.reference() == null || createDto.reference().isEmpty()) {
                    throw new BusinessException("Le numéro de chèque est obligatoire");
                }
                if (createDto.banque() == null || createDto.banque().isEmpty()) {
                    throw new BusinessException("La banque est obligatoire pour un paiement par chèque");
                }
                if (createDto.dateEcheance() == null) {
                    throw new BusinessException("La date d'échéance est obligatoire pour un paiement par chèque");
                }
                break;

            case VIREMENT:
                if (createDto.reference() == null || createDto.reference().isEmpty()) {
                    throw new BusinessException("La référence du virement est obligatoire");
                }
                if (createDto.banque() == null || createDto.banque().isEmpty()) {
                    throw new BusinessException("La banque est obligatoire pour un paiement par virement");
                }
                break;
        }
    }

    private PaymentStatus determineInitialStatus(PaymentType paymentType) {
        return paymentType == PaymentType.ESPECES ? PaymentStatus.ENCAISSE : PaymentStatus.EN_ATTENTE;
    }

    private void updateCommandeRemainingAmount(Commande commande) {
        BigDecimal totalEncaisse = paiementRepository.sumEncaissedAmountByCommandeId(commande.getId());
        BigDecimal remainingAmount = commande.getTotal().subtract(totalEncaisse);
        commande.setRemainingAmount(remainingAmount);
        commandeRepository.save(commande);
    }
}