package com.example.financedashboard.dto.response;


import java.math.BigDecimal;

public record CategoryTotalResponse(
        String category,
        BigDecimal totalAmount
) {}