package org.example.smartshop.services;

import org.example.smartshop.entities.DTO.paiments.PaiementCreateDto;
import org.example.smartshop.entities.DTO.paiments.PaiementResponseDto;

import java.util.List;

public interface IPaiementService {

    /**
     * Enregistrer un nouveau paiement pour une commande
     */
    PaiementResponseDto createPaiement(PaiementCreateDto createDto);

    /**
     * Récupérer tous les paiements d'une commande
     */
    List<PaiementResponseDto> getPaiementsByCommande(Long commandeId);

    /**
     * Récupérer un paiement par son ID
     */
    PaiementResponseDto getPaiementById(Long id);

    /**
     * Encaisser un paiement (changer son statut à ENCAISSÉ)
     */
    PaiementResponseDto encaisserPaiement(Long paiementId);

    /**
     * Rejeter un paiement (changer son statut à REJETÉ)
     */
    PaiementResponseDto rejeterPaiement(Long paiementId);

    /**
     * Récupérer tous les paiements
     */
    List<PaiementResponseDto> getAllPaiements();
}