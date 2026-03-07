package com.smartbiz.controller;

import com.smartbiz.dto.request.InvoiceRequest;
import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Invoice;
import com.smartbiz.service.impl.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Invoice>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> create(@RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Invoice created", invoiceService.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        invoiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted", null));
    }
}
