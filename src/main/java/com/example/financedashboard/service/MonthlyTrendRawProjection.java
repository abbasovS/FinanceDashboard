package com.example.financedashboard.service;

import java.math.BigDecimal;

public interface MonthlyTrendRawProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getIncome();
    BigDecimal getExpense();

}
