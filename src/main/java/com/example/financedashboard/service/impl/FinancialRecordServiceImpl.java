package com.example.financedashboard.service.impl;

import com.example.financedashboard.dto.request.FinancialRecordRequest;
import com.example.financedashboard.dto.response.FinancialRecordResponse;
import com.example.financedashboard.exception.InvalidRequestException;
import com.example.financedashboard.exception.ResourceNotFoundException;
import com.example.financedashboard.mapper.FinancialMapper;
import com.example.financedashboard.model.entity.FinancialRecord;
import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.model.enums.TransactionType;
import com.example.financedashboard.repository.FinancialRecordRepository;
import com.example.financedashboard.repository.UserRepository;
import com.example.financedashboard.service.AuthService;
import com.example.financedashboard.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final FinancialMapper mapper;
    private final AuthService authService;

    @Override
    @Transactional
    public FinancialRecordResponse createRecordForCurrentUser(FinancialRecordRequest request) {
        User currentUser = authService.getCurrentUser();
        FinancialRecord record = mapper.toEntity(request, currentUser);
        return mapper.toResponse(recordRepository.save(record));
    }

    @Override
    @Transactional
    public FinancialRecordResponse createRecordForUser(UUID userId, FinancialRecordRequest request) {
        ensureAdmin();
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));

        FinancialRecord record = mapper.toEntity(request, user);
        return mapper.toResponse(recordRepository.save(record));
    }

    @Override
    @Transactional
    public FinancialRecordResponse updateRecord(UUID recordId, FinancialRecordRequest request) {
        ensureAdmin();
        FinancialRecord record = recordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category());
        record.setDate(request.date());
        record.setNotes(request.notes());

        return mapper.toResponse(recordRepository.save(record));
    }

    @Override
    @Transactional
    public void softDeleteRecord(UUID recordId) {
        ensureAdmin();
        FinancialRecord record = recordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setDeleted(true);
        recordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getMyRecords(String category, TransactionType type, Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        return recordRepository.findByUsernameAndFilters(currentUser.getUsername(), category, type, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getUserRecords(UUID userId, String category, TransactionType type, Pageable pageable) {
        ensureAdmin();
        ensureActiveUser(userId);
        return recordRepository.findByUserIdAndFilters(userId, category, type, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getMyRecordsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        validateDateRange(start, end);
        String username = authService.getCurrentUser().getUsername();
        return recordRepository.findAllByUsernameAndDateBetween(username, start, end, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getUserRecordsByDateRange(UUID userId, LocalDate start, LocalDate end, Pageable pageable) {
        ensureAdmin();
        validateDateRange(start, end);
        ensureActiveUser(userId);
        return recordRepository.findAllByUserIdAndDateBetween(userId, start, end, pageable)
                .map(mapper::toResponse);
    }

    private void ensureAdmin() {
        if (authService.getCurrentUser().getRole() != com.example.financedashboard.model.enums.Role.ADMIN) {
            throw new AccessDeniedException("Admin access required");
        }
    }

    private void ensureActiveUser(UUID userId) {
        userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new InvalidRequestException("Start and end dates are required");
        }
        if (start.isAfter(end)) {
            throw new InvalidRequestException("Start date cannot be after end date");
        }
    }
}
