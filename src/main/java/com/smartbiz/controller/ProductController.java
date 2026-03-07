package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Product;
import com.smartbiz.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Product>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowStock()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.create(product)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Integer id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.update(id, product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
