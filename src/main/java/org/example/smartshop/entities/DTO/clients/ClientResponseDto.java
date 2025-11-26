package org.example.smartshop.entities.DTO.clients;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.smartshop.entities.enums.CustomerTier;

public record ClientResponseDto(
        Long id,
        String nom,
        String email,
        CustomerTier tier,
        int totalOrders,
        BigDecimal totalSpent,
        LocalDateTime firstOrderDate,
        LocalDateTime lastOrderDate,
        Long userId
) { }
