package org.example.smartshop.controlles;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.users.LoginResponseDto;
import org.example.smartshop.entities.DTO.users.LoginRequestDto;
import org.example.smartshop.services.IAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean authenticated = authService.isAuthenticated();

        if (authenticated) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "userId", authService.getCurrentUserId(),
                    "role", authService.getCurrentUserRole()
            ));
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }
}
