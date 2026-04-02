package com.example.financedashboard.service;

import com.example.financedashboard.dto.request.FinancialRecordRequest;
import com.example.financedashboard.dto.response.FinancialRecordResponse;
import com.example.financedashboard.model.enums.TransactionType;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FinancialRecordService {
    FinancialRecordResponse createRecordForCurrentUser(FinancialRecordRequest request);
    FinancialRecordResponse createRecordForUser(UUID userId, FinancialRecordRequest request);
    FinancialRecordResponse updateRecord(UUID recordId, FinancialRecordRequest request);
    void softDeleteRecord(UUID recordId);

    Page<FinancialRecordResponse> getMyRecords(String category, TransactionType type, Pageable pageable);
    Page<FinancialRecordResponse> getUserRecords(UUID userId, String category, TransactionType type, Pageable pageable);
    Page<FinancialRecordResponse> getMyRecordsByDateRange(LocalDate start, LocalDate end, Pageable pageable);
    Page<FinancialRecordResponse> getUserRecordsByDateRange(UUID userId, LocalDate start, LocalDate end, Pageable pageable);
}