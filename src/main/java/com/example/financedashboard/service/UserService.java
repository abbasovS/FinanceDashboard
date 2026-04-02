package com.example.financedashboard.service;

import com.example.financedashboard.dto.request.UserCreateRequest;
import com.example.financedashboard.dto.response.UserResponse;
import com.example.financedashboard.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getCurrentUser();
    UserResponse updateUserRole(UUID userId, Role newRole);
    void toggleUserStatus(UUID userId);
}
