package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Supplier;
import com.smartbiz.service.impl.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Supplier>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Supplier>> create(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponse.success("Supplier created", supplierService.create(supplier)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> update(@PathVariable Integer id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponse.success("Supplier updated", supplierService.update(id, supplier)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted", null));
    }
}
