package com.example.financedashboard.service;

import com.example.financedashboard.dto.response.DashboardSummaryResponse;

import java.util.UUID;

public interface DashboardService {

    DashboardSummaryResponse getMyDashboard();

    DashboardSummaryResponse getDashboardByUserId(UUID userId);
}
