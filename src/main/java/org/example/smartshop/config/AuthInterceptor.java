package org.example.smartshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/auth/login") ||
                requestURI.startsWith("/api/auth/status")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            log.warn("Unauthorized access attempt to: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized. Please login first.\"}");
            return false;
        }

        return true;
    }
}
