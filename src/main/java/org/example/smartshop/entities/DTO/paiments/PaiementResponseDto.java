package org.example.smartshop.entities.DTO.paiments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponseDto(
        Long id,
        Long commandeId,
        Integer numeroPaiement,
        BigDecimal montant,
        String typePaiement,
        LocalDateTime datePaiement,
        LocalDateTime dateEncaissement,
        String statut,
        String reference,
        String banque,
        LocalDateTime dateEcheance
) {
}


