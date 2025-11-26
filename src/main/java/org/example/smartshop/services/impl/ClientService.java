package org.example.smartshop.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.enums.UserRole;
import org.example.smartshop.entities.models.Client;
import org.example.smartshop.mappers.ClientMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.services.IClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public ClientResponseDto getClientById(Long id) {
        checkAuthorization(id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDto> getAllClients() {
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: Admin privileges required");
        }

        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponseDto getCurrentClientInfo() {
        if (isAdmin()) {
            throw new RuntimeException("Admin users don't have client information");
        }

        Long userId = authService.getCurrentUserId();
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found for current user"));

        return clientMapper.toDto(client);
    }

    private void checkAuthorization(Long clientId) {
        String role = authService.getCurrentUserRole();

        if (UserRole.ADMIN.name().equals(role)) {
            return;
        }

        Long userId = authService.getCurrentUserId();
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (!client.getId().equals(clientId)) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
    }

    private boolean isAdmin() {
        return UserRole.ADMIN.name().equals(authService.getCurrentUserRole());
    }
}
