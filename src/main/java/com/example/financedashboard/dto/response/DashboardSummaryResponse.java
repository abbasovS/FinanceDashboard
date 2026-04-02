package com.example.financedashboard.dto.response;


import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance,
        List<CategoryTotalResponse> categoryWiseExpense,
        List<FinancialRecordResponse> recentActivity,
        List<MonthlyTrendResponse> monthlyTrends
) {}
