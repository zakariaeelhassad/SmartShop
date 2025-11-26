package org.example.smartshop.mappers;

import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.models.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDto toDto(Client client);
}