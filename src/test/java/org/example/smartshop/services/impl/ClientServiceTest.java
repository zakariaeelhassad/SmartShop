package org.example.smartshop.services.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ClientService clientService;

    // create client
    @Test
    void shouldCreateClientSuccessfully() {
        ClientCreateDto dto = new ClientCreateDto(
                "Zakaria",
                "zakaria@gmail.com",
                "1234"
        );

        User user = User.builder()
                .id(1L)
                .username(dto.email())
                .password(dto.password())
                .role(UserRole.CLIENT)
                .build();

        Client client = Client.builder()
                .id(10L)
                .nom("Zakaria")
                .email("zakaria@gmail.com")
                .tier(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .firstOrderDate(null)
                .lastOrderDate(null)
                .user(user)
                .build();

        ClientResponseDto responseDto = new ClientResponseDto(
                10L,
                "Zakaria",
                "zakaria@gmail.com",
                CustomerTier.BASIC,
                0,
                BigDecimal.ZERO,
                null,
                null,
                1L
        );

        doNothing().when(authService).requireAdminRole();
        when(clientMapper.toEntity(dto)).thenReturn(client);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(responseDto);

        ClientResponseDto result = clientService.createClient(dto);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Zakaria", result.nom());
        verify(userRepository).save(any(User.class));
        verify(clientRepository).save(any(Client.class));
    }

    //get all clients
    @Test
    void shouldGetAllClients() {
        doNothing().when(authService).requireAdminRole();

        when(clientRepository.findAll())
                .thenReturn(List.of(new Client(), new Client()));

        when(clientMapper.toDto(any(Client.class)))
                .thenReturn(new ClientResponseDto(
                        1L, "zak", "email",
                        CustomerTier.BASIC,
                        0,
                        BigDecimal.ZERO,
                        null,
                        null,
                        1L
                ));

        List<ClientResponseDto> result = clientService.getAllClients();

        assertEquals(2, result.size());
    }

    // update client
    @Test
    void shouldUpdateClient() {
        doNothing().when(authService).requireAdminRole();

        ClientUpdateDto dto = new ClientUpdateDto("New Name", "new@mail.com");

        Client client = Client.builder().id(7L).build();

        when(clientRepository.findById(7L))
                .thenReturn(Optional.of(client));

        doNothing().when(clientMapper).updateEntity(dto, client);

        when(clientRepository.save(client)).thenReturn(client);

        when(clientMapper.toDto(client))
                .thenReturn(new ClientResponseDto(
                        7L,
                        "New Name",
                        "new@mail.com",
                        CustomerTier.BASIC,
                        0,
                        BigDecimal.ZERO,
                        null,
                        null,
                        1L
                ));

        ClientResponseDto result = clientService.updateClient(7L, dto);

        assertEquals(7L, result.id());
        assertEquals("New Name", result.nom());
    }

    // delete client
    @Test
    void shouldDeleteClientSuccessfully() {
        doNothing().when(authService).requireAdminRole();

        Client client = Client.builder().id(4L).commandes(List.of()).build();

        when(clientRepository.findById(4L))
                .thenReturn(Optional.of(client));

        doNothing().when(clientRepository).delete(client);

        assertDoesNotThrow(() -> clientService.deleteClient(4L));
    }
}
