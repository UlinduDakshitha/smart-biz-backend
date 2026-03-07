package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Expense;
import com.smartbiz.service.impl.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Expense>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Expense>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Expense>> create(@RequestBody Expense expense) {
        return ResponseEntity.ok(ApiResponse.success("Expense created", expenseService.create(expense)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Expense>> update(@PathVariable Integer id, @RequestBody Expense expense) {
        return ResponseEntity.ok(ApiResponse.success("Expense updated", expenseService.update(id, expense)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        expenseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted", null));
    }
}
