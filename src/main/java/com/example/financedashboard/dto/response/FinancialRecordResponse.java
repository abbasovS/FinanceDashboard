package com.example.financedashboard.dto.response;

import com.example.financedashboard.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialRecordResponse(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date,
        String notes,
        UUID userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
