package org.example.smartshop.entities.DTO.paiments;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementCreateDto(
        @NotNull(message = "L'identifiant de la commande est obligatoire")
        Long commandeId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
        BigDecimal montant,

        @NotBlank(message = "Le type de paiement est obligatoire")
        @Pattern(regexp = "ESPECES|CHEQUE|VIREMENT", message = "Type de paiement invalide")
        String typePaiement,

        String reference,

        String banque,

        LocalDateTime dateEcheance
) {
}