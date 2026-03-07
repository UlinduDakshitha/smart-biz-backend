package com.smartbiz.repository;

import com.smartbiz.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    List<Supplier> findByBusiness_BusinessId(Integer businessId);
}
