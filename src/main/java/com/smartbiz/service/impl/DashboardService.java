package com.smartbiz.service.impl;

import com.smartbiz.dto.response.DashboardResponse;
import com.smartbiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final BusinessContextService businessContextService;

    public DashboardResponse getDashboard() {
        Integer businessId = businessContextService.getCurrentBusinessId();
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        BigDecimal totalSales = invoiceRepository.sumTotalByBusinessAndDateRange(businessId, startOfMonth, endOfMonth);
        BigDecimal totalExpenses = expenseRepository.sumAmountByBusinessAndDateRange(businessId, startOfMonth, endOfMonth);
        BigDecimal netProfit = totalSales.subtract(totalExpenses);

        Long totalInvoices = invoiceRepository.countByBusinessId(businessId);
        Long totalCustomers = (long) customerRepository.findByBusiness_BusinessId(businessId).size();
        Long totalProducts = (long) productRepository.findByBusiness_BusinessId(businessId).size();
        Long lowStockCount = (long) productRepository.findLowStockProducts(businessId, 5).size();

        // Top selling products
        List<Object[]> topRaw = invoiceItemRepository.findTopSellingProducts(businessId);
        List<Map<String, Object>> topProducts = new ArrayList<>();
        for (Object[] row : topRaw) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", row[0]);
            item.put("name", row[1]);
            item.put("totalQty", row[2]);
            item.put("totalRevenue", row[3]);
            topProducts.add(item);
        }

        // Recent invoices
        List<Map<String, Object>> recentInvoices = new ArrayList<>();
        invoiceRepository.findByBusiness_BusinessId(businessId).stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5)
                .forEach(inv -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("invoiceId", inv.getInvoiceId());
                    item.put("customerName", inv.getCustomer().getName());
                    item.put("totalAmount", inv.getTotalAmount());
                    item.put("date", inv.getDate());
                    recentInvoices.add(item);
                });

        return DashboardResponse.builder()
                .totalSalesThisMonth(totalSales)
                .totalExpensesThisMonth(totalExpenses)
                .netProfitThisMonth(netProfit)
                .totalInvoices(totalInvoices)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .topSellingProducts(topProducts)
                .recentInvoices(recentInvoices)
                .build();
    }
}
