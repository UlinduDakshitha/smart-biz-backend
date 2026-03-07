package com.smartbiz.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceRequest {
    private Integer customerId;
    private List<InvoiceItemRequest> items;

    @Data
    public static class InvoiceItemRequest {
        private Integer productId;
        private Integer quantity;
    }
}
