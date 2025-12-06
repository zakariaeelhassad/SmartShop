package org.example.smartshop.entities.DTO.product;

import java.math.BigDecimal;

public record ProductCreateDto(
        String name,
        BigDecimal unitPrice,
        int stock
) {}