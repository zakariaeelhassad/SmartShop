package org.example.smartshop.services;


import org.example.smartshop.entities.DTO.clients.ClientCreateDto;
import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.DTO.clients.ClientUpdateDto;

import java.util.List;

public interface IClientService {

    ClientResponseDto createClient(ClientCreateDto dto);
    ClientResponseDto updateClient(Long id, ClientUpdateDto dto);
    void deleteClient(Long id);
    ClientResponseDto getClientById(Long id);
    List<ClientResponseDto> getAllClients();
    ClientResponseDto getCurrentClientInfo();
}
