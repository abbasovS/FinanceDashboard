package com.example.financedashboard.dto.response;


import com.example.financedashboard.model.enums.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        Role role,
        boolean isActive
) {}
