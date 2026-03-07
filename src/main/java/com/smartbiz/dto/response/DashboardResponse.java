package com.smartbiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalSalesThisMonth;
    private BigDecimal totalExpensesThisMonth;
    private BigDecimal netProfitThisMonth;
    private Long totalInvoices;
    private Long totalCustomers;
    private Long totalProducts;
    private Long lowStockCount;
    private List<Map<String, Object>> topSellingProducts;
    private List<Map<String, Object>> recentInvoices;
}
