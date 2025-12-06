package org.example.smartshop.entities.DTO.commandes;

import org.example.smartshop.entities.DTO.orderItem.OrderItemResponseDto;
import org.example.smartshop.entities.DTO.paiments.PaiementDto;
import org.example.smartshop.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponseDto(
        Long id,
        Long clientId,
        String clientName,
        LocalDateTime dateCreated,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal tax,
        BigDecimal total,
        String promoCode,
        OrderStatus status,
        BigDecimal remainingAmount,
        List<OrderItemResponseDto> items,
        List<PaiementDto> paiements
) {}