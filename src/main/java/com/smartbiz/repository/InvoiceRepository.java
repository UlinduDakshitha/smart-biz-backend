package com.smartbiz.repository;

import com.smartbiz.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByBusiness_BusinessId(Integer businessId);
    List<Invoice> findByBusiness_BusinessIdAndDateBetween(Integer businessId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.business.businessId = :businessId AND i.date BETWEEN :start AND :end")
    BigDecimal sumTotalByBusinessAndDateRange(@Param("businessId") Integer businessId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.business.businessId = :businessId")
    Long countByBusinessId(@Param("businessId") Integer businessId);
}
