package org.example.smartshop.services;

import org.example.smartshop.entities.DTO.users.LoginRequestDto;
import org.example.smartshop.entities.DTO.users.LoginResponseDto;

public interface IAuthService {
    LoginResponseDto login(LoginRequestDto loginRequest);
    void logout();
    boolean isAuthenticated();
    Long getCurrentUserId();
    String getCurrentUserRole();
}
