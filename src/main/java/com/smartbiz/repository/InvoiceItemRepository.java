package com.smartbiz.repository;

import com.smartbiz.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Integer> {
    List<InvoiceItem> findByInvoice_InvoiceId(Integer invoiceId);

    @Query(value = "SELECT ii.product_id, p.name, SUM(ii.quantity) as total_qty, SUM(ii.quantity * ii.price) as total_revenue " +
            "FROM invoice_items ii JOIN products p ON ii.product_id = p.product_id " +
            "JOIN invoices i ON ii.invoice_id = i.invoice_id " +
            "WHERE i.business_id = :businessId " +
            "GROUP BY ii.product_id, p.name ORDER BY total_qty DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopSellingProducts(@Param("businessId") Integer businessId);
}
