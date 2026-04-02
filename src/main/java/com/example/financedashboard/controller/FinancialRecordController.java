package com.example.financedashboard.controller;

import com.example.financedashboard.dto.request.FinancialRecordRequest;
import com.example.financedashboard.dto.response.FinancialRecordResponse;
import com.example.financedashboard.model.enums.TransactionType;
import com.example.financedashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<FinancialRecordResponse> createForCurrentUser(@Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecordForCurrentUser(request));
    }

    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecordResponse> createForUser(@PathVariable UUID userId,
                                                                 @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecordForUser(userId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecordResponse> update(@PathVariable UUID id,
                                                          @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        recordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<Page<FinancialRecordResponse>> getMyRecords(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getMyRecords(category, type, pageable));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FinancialRecordResponse>> getUserRecords(
            @PathVariable UUID userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getUserRecords(userId, category, type, pageable));
    }

    @GetMapping("/me/by-date")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<Page<FinancialRecordResponse>> getMyRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getMyRecordsByDateRange(start, end, pageable));
    }

    @GetMapping("/users/{userId}/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FinancialRecordResponse>> getUserRecordsByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getUserRecordsByDateRange(userId, start, end, pageable));
    }
}
