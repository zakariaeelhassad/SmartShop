package org.example.smartshop.entities.DTO.paiments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementDto(
        Long id,
        Integer paymentNumber,
        BigDecimal amount,
        String paymentType,
        LocalDateTime paymentDate,
        LocalDateTime encashmentDate,
        String status,
        String reference,
        String bank,
        LocalDateTime dueDate
) {}