package org.example.smartshop.entities.DTO.product;

import java.math.BigDecimal;

public record ProductUpdateDto(
        Long id ,
        String name,
        BigDecimal unitPrice,
        int stock,
        boolean deleted
) {}
