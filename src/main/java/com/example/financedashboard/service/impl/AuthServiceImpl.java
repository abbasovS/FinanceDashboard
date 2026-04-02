package com.example.financedashboard.service.impl;

import com.example.financedashboard.dto.request.LoginRequest;
import com.example.financedashboard.dto.response.AuthResponse;
import com.example.financedashboard.dto.response.UserResponse;
import com.example.financedashboard.exception.ResourceNotFoundException;
import com.example.financedashboard.mapper.UserMapper;
import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.repository.UserRepository;
import com.example.financedashboard.service.AuthService;
import com.example.financedashboard.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (DisabledException ex) {
            throw new BadCredentialsException("User is inactive");
        }

        User user = userRepository.findByUsernameAndIsActiveTrue(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toResponse(user);
        return new AuthResponse(token, "Bearer", userResponse);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            Object principal = authentication != null ? authentication.getPrincipal() : null;
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                return userRepository.findByUsernameAndIsActiveTrue(userDetails.getUsername())
                        .orElseThrow(() -> new ResourceNotFoundException("Authenticated active user not found"));
            }
            throw new BadCredentialsException("Authenticated user not found");
        }
        return user;
    }

    @Override
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
