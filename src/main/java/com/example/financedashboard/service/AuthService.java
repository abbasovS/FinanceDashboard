package com.example.financedashboard.service;

import com.example.financedashboard.dto.request.LoginRequest;
import com.example.financedashboard.dto.response.AuthResponse;
import com.example.financedashboard.model.entity.User;

import java.util.UUID;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    User getCurrentUser();

    UUID getCurrentUserId();
}
