package com.example.financedashboard.mapper;

import com.example.financedashboard.dto.request.UserCreateRequest;
import com.example.financedashboard.dto.response.UserResponse;
import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.model.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserCreateRequest request, String encodedPassword, Role finalRole) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(encodedPassword);
        user.setRole(finalRole);
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.isActive());
    }
}