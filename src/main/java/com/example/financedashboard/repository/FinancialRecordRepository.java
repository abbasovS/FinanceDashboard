package com.example.financedashboard.repository;

import com.example.financedashboard.dto.response.CategoryTotalResponse;
import com.example.financedashboard.model.entity.FinancialRecord;
import com.example.financedashboard.model.enums.TransactionType;
import com.example.financedashboard.service.MonthlyTrendRawProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {


    @Query("""
            select coalesce(sum(f.amount), 0)
            from FinancialRecord f
            where f.user.username = :username
              and f.type = :type
              and f.isDeleted = false
            """)
    BigDecimal getTotalAmountByUsernameAndType(@Param("username") String username,
                                               @Param("type") TransactionType type);

    @Query("""
        select new com.example.financedashboard.dto.response.CategoryTotalResponse(
            f.category,
            sum(f.amount)
        )
        from FinancialRecord f
        where f.user.username = :username
          and f.type = com.example.financedashboard.model.enums.TransactionType.EXPENSE
          and f.isDeleted = false
        group by f.category
        order by sum(f.amount) desc
        """)
    List<CategoryTotalResponse> getExpenseCategoryTotalsByUsername(@Param("username") String username);

    @Query("""
        select
            year(f.date) as year,
            month(f.date) as month,
            sum(case when f.type = com.example.financedashboard.model.enums.TransactionType.INCOME then f.amount else null end) as income,
            sum(case when f.type = com.example.financedashboard.model.enums.TransactionType.EXPENSE then f.amount else null end) as expense
        from FinancialRecord f
        where f.user.username = :username
          and f.isDeleted = false
          and f.date >= :startDate
        group by year(f.date), month(f.date)
        order by year(f.date), month(f.date)
        """)
    List<MonthlyTrendRawProjection> getMonthlyTrendsFromDate(@Param("username") String username,
                                                             @Param("startDate") LocalDate startDate);
    List<FinancialRecord> findTop5ByUserUsernameAndIsDeletedFalseOrderByDateDesc(String username);

    Optional<FinancialRecord> findByIdAndIsDeletedFalse(UUID id);
    @Query("""
        select f
        from FinancialRecord f
        where f.user.username = :username
          and (:category is null or f.category = :category)
          and (:type is null or f.type = :type)
          and f.isDeleted = false
        """)
    Page<FinancialRecord> findByUsernameAndFilters(@Param("username") String username,
                                                   @Param("category") String category,
                                                   @Param("type") TransactionType type,
                                                   Pageable pageable);

    @Query("""
        select f
        from FinancialRecord f
        where f.user.id = :userId
          and (:category is null or f.category = :category)
          and (:type is null or f.type = :type)
          and f.isDeleted = false
        """)
    Page<FinancialRecord> findByUserIdAndFilters(@Param("userId") UUID userId,
                                                 @Param("category") String category,
                                                 @Param("type") TransactionType type,
                                                 Pageable pageable);

    @Query("""
        select f
        from FinancialRecord f
        where f.user.username = :username
          and f.date between :start and :end
          and f.isDeleted = false
        """)
    Page<FinancialRecord> findAllByUsernameAndDateBetween(@Param("username") String username,
                                                          @Param("start") LocalDate start,
                                                          @Param("end") LocalDate end,
                                                          Pageable pageable);

    @Query("""
        select f
        from FinancialRecord f
        where f.user.id = :userId
          and f.date between :start and :end
          and f.isDeleted = false
        """)
    Page<FinancialRecord> findAllByUserIdAndDateBetween(@Param("userId") UUID userId,
                                                        @Param("start") LocalDate start,
                                                        @Param("end") LocalDate end,
                                                        Pageable pageable);


    default List<MonthlyTrendRawProjection> getMonthlyTrends(String username) {
        LocalDate startDate = java.time.YearMonth.now()
                .minusMonths(5)
                .atDay(1);

        return getMonthlyTrendsFromDate(username, startDate);
    }
}
