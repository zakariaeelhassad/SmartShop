package org.example.smartshop.services;

import org.example.smartshop.entities.DTO.commandes.CommandeCreateDto;
import org.example.smartshop.entities.DTO.commandes.CommandeResponseDto;
import org.example.smartshop.entities.DTO.commandes.CommandeUpdateDto;

import java.util.List;

public interface ICommandeService {

    CommandeResponseDto createCommande(CommandeCreateDto createDto);

    CommandeResponseDto getCommandeById(Long id);

    List<CommandeResponseDto> getAllCommandes();

    List<CommandeResponseDto> getCommandesByClient(Long clientId);

    CommandeResponseDto updateCommandeStatus(Long id, CommandeUpdateDto updateDto);

    CommandeResponseDto confirmCommande(Long id);

    CommandeResponseDto cancelCommande(Long id);
}