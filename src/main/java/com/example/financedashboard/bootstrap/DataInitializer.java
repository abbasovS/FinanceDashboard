package com.example.financedashboard.bootstrap;

import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.model.enums.Role;
import com.example.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        userRepository.findByUsername(adminUsername).orElseGet(() -> {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            return userRepository.save(admin);
        });
    }
}
