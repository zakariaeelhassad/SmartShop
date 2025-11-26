package org.example.smartshop.entities.DTO.users;

import org.example.smartshop.entities.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String username,
        UserRole role,
        Long clientId,
        LocalDateTime createdAt,
        LocalDateTime lastLogin
) {
}
