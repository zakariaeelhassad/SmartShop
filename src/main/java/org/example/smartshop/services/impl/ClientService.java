package org.example.smartshop.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.clients.ClientCreateDto;
import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.DTO.clients.ClientUpdateDto;
import org.example.smartshop.entities.enums.CustomerTier;
import org.example.smartshop.entities.enums.UserRole;
import org.example.smartshop.entities.models.Client;
import org.example.smartshop.entities.models.User;
import org.example.smartshop.mappers.ClientMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.repository.UserRepository;
import org.example.smartshop.services.IClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final AuthService authService;

    @Override
    @Transactional
    public ClientResponseDto createClient(ClientCreateDto dto) {
        authService.requireAdminRole();

        User user = User.builder()
                .username(dto.email())
                .password(dto.password())
                .role(UserRole.CLIENT)
                .build();

        userRepository.save(user);

        Client client = clientMapper.toEntity(dto);
        client.setTier(CustomerTier.BASIC);
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);
        client.setUser(user);

        Client saved = clientRepository.save(client);

        return clientMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto getClientById(Long id) {
        checkAuthorization(id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDto> getAllClients() {
        authService.requireAdminRole();
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto getCurrentClientInfo() {
        authService.requireAdminRole();

        Long userId = authService.getCurrentUserId();

        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found for current user"));

        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientResponseDto updateClient(Long id, ClientUpdateDto dto) {
        authService.requireAdminRole();

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));

        clientMapper.updateEntity(dto, client);

        Client updated = clientRepository.save(client);
        return clientMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        authService.requireAdminRole();

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));

        if (client.getCommandes() != null && !client.getCommandes().isEmpty()) {
            throw new RuntimeException("Cannot delete client with existing orders.");
        }

        clientRepository.delete(client);
    }

    // ------------------- SECURITY HELPERS -------------------

    private void checkAuthorization(Long clientId) {
        String role = authService.getCurrentUserRole();

        if (UserRole.ADMIN.name().equals(role)) return;

        Long userId = authService.getCurrentUserId();

        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (!client.getId().equals(clientId)) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
    }

}
