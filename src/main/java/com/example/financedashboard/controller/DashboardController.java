package com.example.financedashboard.controller;

import com.example.financedashboard.dto.response.DashboardSummaryResponse;
import com.example.financedashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getMyDashboard() {
        return ResponseEntity.ok(dashboardService.getMyDashboard());
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getUserDashboard(@PathVariable UUID userId) {
        return ResponseEntity.ok(dashboardService.getDashboardByUserId(userId));
    }
}
