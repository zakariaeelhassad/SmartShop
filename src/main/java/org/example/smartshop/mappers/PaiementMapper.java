package org.example.smartshop.mappers;

import org.example.smartshop.entities.DTO.paiments.PaiementResponseDto;
import org.example.smartshop.entities.models.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    @Mapping(source = "commande.id", target = "commandeId")
    @Mapping(source = "typePaiement", target = "typePaiement")
    @Mapping(source = "statut", target = "statut")
    PaiementResponseDto toDto(Paiement paiement);
}
