package org.example.smartshop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartshop.entities.enums.UserRole;
import org.example.smartshop.entities.models.User;
import org.example.smartshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminUsername = "admin";

        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password("admin123")
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("✅ Admin user created successfully");
            log.info("   Username: admin");
            log.info("   Password: admin123");
        } else {
            log.info("✅ Admin user already exists");
        }
    }
}