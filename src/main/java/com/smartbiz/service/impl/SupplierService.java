package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Supplier;
import com.smartbiz.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final BusinessContextService businessContextService;

    public List<Supplier> getAll() {
        return supplierRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }

    public Supplier getById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        validateOwnership(supplier);
        return supplier;
    }

    public Supplier create(Supplier supplier) {
        Business business = businessContextService.getCurrentBusiness();
        supplier.setBusiness(business);
        return supplierRepository.save(supplier);
    }

    public Supplier update(Integer id, Supplier updated) {
        Supplier existing = getById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        return supplierRepository.save(existing);
    }

    public void delete(Integer id) {
        Supplier supplier = getById(id);
        supplierRepository.delete(supplier);
    }

    private void validateOwnership(Supplier supplier) {
        Integer businessId = businessContextService.getCurrentBusinessId();
        if (!supplier.getBusiness().getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied");
        }
    }
}
