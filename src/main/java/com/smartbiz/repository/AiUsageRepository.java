package com.smartbiz.repository;

import com.smartbiz.entity.AiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiUsageRepository extends JpaRepository<AiUsage, Integer> {
    List<AiUsage> findByBusiness_BusinessId(Integer businessId);
    Long countByBusiness_BusinessId(Integer businessId);
}
