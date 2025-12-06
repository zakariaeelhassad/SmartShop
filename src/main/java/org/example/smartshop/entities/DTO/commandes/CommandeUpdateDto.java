package org.example.smartshop.entities.DTO.commandes;

import jakarta.validation.constraints.NotNull;
import org.example.smartshop.entities.enums.OrderStatus;

public record CommandeUpdateDto(
        @NotNull(message = "Le statut est obligatoire")
        OrderStatus status
) {
}
