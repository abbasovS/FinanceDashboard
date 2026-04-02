package com.example.financedashboard.dto.response;

import java.math.BigDecimal;

public record MonthlyTrendResponse(
        String month,
        BigDecimal income,
        BigDecimal expense
) {}
