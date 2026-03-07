package com.smartbiz.repository;

import com.smartbiz.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByBusiness_BusinessId(Integer businessId);
    List<Expense> findByBusiness_BusinessIdAndDateBetween(Integer businessId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.business.businessId = :businessId AND e.date BETWEEN :start AND :end")
    BigDecimal sumAmountByBusinessAndDateRange(@Param("businessId") Integer businessId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
