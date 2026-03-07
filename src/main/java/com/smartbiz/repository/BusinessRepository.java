package com.smartbiz.repository;

import com.smartbiz.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer> {
    Optional<Business> findByEmail(String email);
    boolean existsByEmail(String email);
}
