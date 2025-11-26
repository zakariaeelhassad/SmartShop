package org.example.smartshop.mappers;

import org.example.smartshop.entities.DTO.users.UserResponseDto;
import org.example.smartshop.entities.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);
}