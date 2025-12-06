package org.example.smartshop.mappers;

import org.example.smartshop.entities.DTO.clients.ClientCreateDto;
import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.DTO.clients.ClientUpdateDto;
import org.example.smartshop.entities.models.Client;
import org.mapstruct.Mapper;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientCreateDto dto);

    @Mapping(target = "userId", source = "user.id")
    ClientResponseDto toDto(Client client);

    void updateEntity(ClientUpdateDto dto, @MappingTarget Client client);
}
