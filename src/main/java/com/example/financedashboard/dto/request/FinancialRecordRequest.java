package com.example.financedashboard.dto.request;

import com.example.financedashboard.model.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialRecordRequest(

        @NotNull(message = "The amount cannot be empty")
        @Positive(message = "The amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "The transaction type (INCOME/EXPENSE) must be selected")
        TransactionType type,

        @NotBlank(message = "Category cannot be empty")
        String category,

        @NotNull(message = "The date must be recorded")
        LocalDate date,

        String notes
) {}
