package com.example.financedashboard.repository;

import com.example.financedashboard.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    Optional<User> findByIdAndIsActiveTrue(UUID id);
    boolean existsByUsername(String username);

    long countByRoleAndIsActiveTrue(com.example.financedashboard.model.enums.Role role);
}
