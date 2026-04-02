package com.example.financedashboard.service.impl;

import com.example.financedashboard.dto.response.CategoryTotalResponse;
import com.example.financedashboard.dto.response.DashboardSummaryResponse;
import com.example.financedashboard.dto.response.FinancialRecordResponse;
import com.example.financedashboard.dto.response.MonthlyTrendResponse;
import com.example.financedashboard.exception.ResourceNotFoundException;
import com.example.financedashboard.mapper.FinancialMapper;
import com.example.financedashboard.model.entity.User;
import com.example.financedashboard.model.enums.Role;
import com.example.financedashboard.model.enums.TransactionType;
import com.example.financedashboard.repository.FinancialRecordRepository;
import com.example.financedashboard.repository.UserRepository;
import com.example.financedashboard.service.AuthService;
import com.example.financedashboard.service.DashboardService;
import com.example.financedashboard.service.MonthlyTrendRawProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int TREND_MONTH_COUNT = 6;

    private final FinancialRecordRepository recordRepository;
    private final FinancialMapper financialMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public DashboardSummaryResponse getMyDashboard() {
        return buildDashboardSummary(authService.getCurrentUser().getUsername());
    }

    @Override
    public DashboardSummaryResponse getDashboardByUserId(UUID userId) {
        User targetUser = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));

        if (authService.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can access another user's dashboard");
        }

        return buildDashboardSummary(targetUser.getUsername());
    }

    private DashboardSummaryResponse buildDashboardSummary(String username) {
        BigDecimal totalIncome = zeroIfNull(recordRepository.getTotalAmountByUsernameAndType(username, TransactionType.INCOME));
        BigDecimal totalExpense = zeroIfNull(recordRepository.getTotalAmountByUsernameAndType(username, TransactionType.EXPENSE));
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        List<CategoryTotalResponse> categoryTotals = defaultIfNull(recordRepository.getExpenseCategoryTotalsByUsername(username));
        List<FinancialRecordResponse> recentTransactions = recordRepository
                .findTop5ByUserUsernameAndIsDeletedFalseOrderByDateDesc(username)
                .stream()
                .map(financialMapper::toResponse)
                .toList();

        List<MonthlyTrendResponse> monthlyTrends = normalizeMonthlyTrends(
                recordRepository.getMonthlyTrends(username),
                TREND_MONTH_COUNT
        );

        return new DashboardSummaryResponse(totalIncome, totalExpense, netBalance, categoryTotals, recentTransactions, monthlyTrends);
    }

    private List<MonthlyTrendResponse> normalizeMonthlyTrends(List<MonthlyTrendRawProjection> rawTrends, int monthCount) {
        Map<YearMonth, MonthlyTrendRawProjection> trendMap = rawTrends.stream()
                .collect(Collectors.toMap(
                        trend -> YearMonth.of(trend.getYear(), trend.getMonth()),
                        trend -> trend,
                        (existing, replacement) -> existing
                ));

        List<MonthlyTrendResponse> result = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = monthCount - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            MonthlyTrendRawProjection trend = trendMap.get(month);

            result.add(new MonthlyTrendResponse(
                    month.toString(),
                    trend != null && trend.getIncome() != null ? trend.getIncome() : BigDecimal.ZERO,
                    trend != null && trend.getExpense() != null ? trend.getExpense() : BigDecimal.ZERO
            ));
        }

        return result;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private <T> List<T> defaultIfNull(List<T> values) {
        return values == null ? List.of() : values;
    }

}
