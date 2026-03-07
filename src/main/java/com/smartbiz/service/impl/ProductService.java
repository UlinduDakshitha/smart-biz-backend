package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Product;
import com.smartbiz.entity.Supplier;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final BusinessContextService businessContextService;

    public List<Product> getAll() {
        return productRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }

    public Product getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        validateOwnership(product);
        return product;
    }

    public Product create(Product product) {
        Business business = businessContextService.getCurrentBusiness();
        product.setBusiness(business);
        return productRepository.save(product);
    }

    public Product update(Integer id, Product updated) {
        Product existing = getById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setStockQty(updated.getStockQty());
        if (updated.getSupplier() != null && updated.getSupplier().getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(updated.getSupplier().getSupplierId()).orElse(null);
            existing.setSupplier(supplier);
        }
        return productRepository.save(existing);
    }

    public void delete(Integer id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    public List<Product> getLowStock() {
        return productRepository.findLowStockProducts(businessContextService.getCurrentBusinessId(), 5);
    }

    private void validateOwnership(Product product) {
        Integer businessId = businessContextService.getCurrentBusinessId();
        if (!product.getBusiness().getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied");
        }
    }
}
