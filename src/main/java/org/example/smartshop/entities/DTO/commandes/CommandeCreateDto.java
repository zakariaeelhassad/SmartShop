package org.example.smartshop.entities.DTO.commandes;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.smartshop.entities.DTO.orderItem.OrderItemCreateDto;

import java.util.List;

public record CommandeCreateDto(
        @NotNull(message = "Client ID est obligatoire")
        Long clientId,

        @NotEmpty(message = "La commande doit contenir au moins un article")
        List<OrderItemCreateDto> items,

        @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Le code promo doit être au format PROMO-XXXX")
        String promoCode
) {}