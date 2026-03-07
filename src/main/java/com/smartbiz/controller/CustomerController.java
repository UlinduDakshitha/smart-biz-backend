package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Customer;
import com.smartbiz.service.impl.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(customerService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> create(@RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.success("Customer created", customerService.create(customer)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> update(@PathVariable Integer id, @RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerService.update(id, customer)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted", null));
    }
}
