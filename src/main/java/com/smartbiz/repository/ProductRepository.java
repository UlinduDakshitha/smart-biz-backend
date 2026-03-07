package com.smartbiz.repository;

import com.smartbiz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByBusiness_BusinessId(Integer businessId);

    @Query("SELECT p FROM Product p WHERE p.business.businessId = :businessId AND p.stockQty <= :threshold")
    List<Product> findLowStockProducts(@Param("businessId") Integer businessId, @Param("threshold") Integer threshold);
}
