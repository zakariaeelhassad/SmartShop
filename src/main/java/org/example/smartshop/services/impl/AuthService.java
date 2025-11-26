package org.example.smartshop.services.impl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartshop.entities.DTO.users.LoginResponseDto;
import org.example.smartshop.entities.DTO.users.LoginRequestDto;
import org.example.smartshop.entities.models.User;
import org.example.smartshop.repository.UserRepository;
import org.example.smartshop.services.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new RuntimeException("Invalid username or password");
        }

        httpSession.setAttribute("userId", user.getId());
        httpSession.setAttribute("userRole", user.getRole().name());

        log.info("User {} logged in successfully", user.getUsername());

        return LoginResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .clientId(user.getClient() != null ? user.getClient().getId() : null)
                .message("Login successful")
                .build();
    }

    public void logout() {
        Long userId = (Long) httpSession.getAttribute("userId");

        if (userId != null) {
            log.info("User {} logged out", userId);
        }

        httpSession.invalidate();
    }

    public boolean isAuthenticated() {
        return httpSession.getAttribute("userId") != null;
    }

    public Long getCurrentUserId() {
        return (Long) httpSession.getAttribute("userId");
    }

    public String getCurrentUserRole() {
        return (String) httpSession.getAttribute("userRole");
    }
}