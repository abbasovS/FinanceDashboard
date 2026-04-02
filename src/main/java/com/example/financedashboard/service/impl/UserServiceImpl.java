package com.example.financedashboard.service.impl;

import com.example.financedashboard.dto.request.UserCreateRequest;
import com.example.financedashboard.dto.response.UserResponse;
import com.example.financedashboard.exception.InvalidOperationException;
import com.example.financedashboard.exception.ResourceNotFoundException;
import com.example.financedashboard.mapper.UserMapper;
import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.model.enums.Role;
import com.example.financedashboard.repository.UserRepository;
import com.example.financedashboard.service.AuthService;
import com.example.financedashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AuthService authService;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        ensureAdmin();
        if (userRepository.existsByUsername(request.username())) {
            throw new InvalidOperationException("Username is already taken");
        }

        User user = mapper.toEntity(request, passwordEncoder.encode(request.password()), request.role());
        user.setActive(true);
        return mapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        ensureAdmin();
        return userRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return mapper.toResponse(authService.getCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(UUID userId, Role newRole) {
        ensureAdmin();
        User currentUser = authService.getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new InvalidOperationException("Inactive user's role cannot be changed");
        }

        if (currentUser.getId().equals(userId) && newRole != Role.ADMIN) {
            throw new InvalidOperationException("You cannot remove your own admin role");
        }

        user.setRole(newRole);
        return mapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void toggleUserStatus(UUID userId) {
        ensureAdmin();
        UUID currentUserId = authService.getCurrentUserId();
        if (currentUserId.equals(userId)) {
            throw new InvalidOperationException("You cannot deactivate yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN && user.isActive()) {
            long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
            if (activeAdminCount <= 1) {
                throw new InvalidOperationException("The last active admin cannot be deactivated");
            }
        }

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    private void ensureAdmin() {
        if (authService.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin access required");
        }
    }
}
