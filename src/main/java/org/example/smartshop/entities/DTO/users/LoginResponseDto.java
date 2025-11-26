package org.example.smartshop.entities.DTO.users;

import org.example.smartshop.entities.enums.UserRole;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        Long id,
        String username,
        UserRole role,
        Long clientId,
        String message
) {
}
