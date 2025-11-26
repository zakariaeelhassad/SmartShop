package org.example.smartshop.services;


import org.example.smartshop.entities.DTO.clients.ClientResponseDto;

import java.util.List;

public interface IClientService {

    ClientResponseDto getClientById(Long id);
    List<ClientResponseDto> getAllClients();
    ClientResponseDto getCurrentClientInfo();
}
