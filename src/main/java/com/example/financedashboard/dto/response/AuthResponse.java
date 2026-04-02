package com.example.financedashboard.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
