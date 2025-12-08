package org.example.smartshop.services.impl;

import jakarta.servlet.http.HttpSession;
import org.example.smartshop.entities.DTO.users.LoginRequestDto;
import org.example.smartshop.entities.DTO.users.LoginResponseDto;
import org.example.smartshop.entities.enums.UserRole;
import org.example.smartshop.entities.models.User;
import org.example.smartshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private AuthService authService;

    //Login
    @Test
    void shouldLoginSuccessfully() {
        LoginRequestDto request = new LoginRequestDto("zakaria", "1234");

        User user = User.builder()
                .id(1L)
                .username("zakaria")
                .password("1234")
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findByUsername("zakaria"))
                .thenReturn(Optional.of(user));

        LoginResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("zakaria", response.username());
        assertEquals(UserRole.ADMIN, response.role());
        assertEquals("Login successful", response.message());

        verify(httpSession).setAttribute("userId", 1L);
        verify(httpSession).setAttribute("userRole", "ADMIN");
    }

    // Logout
    @Test
    void shouldLogoutSuccessfully() {
        when(httpSession.getAttribute("userId"))
                .thenReturn(1L);

        authService.logout();

        verify(httpSession).invalidate();
    }

    //isAdmin
    @Test
    void shouldReturnTrueIfAdmin() {
        when(httpSession.getAttribute("userRole"))
                .thenReturn("ADMIN");

        assertTrue(authService.isAdmin());
    }
}
