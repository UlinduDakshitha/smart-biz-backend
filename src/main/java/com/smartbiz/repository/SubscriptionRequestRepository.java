
package com.smartbiz.repository;

import com.smartbiz.entity.SubscriptionRequest;
import com.smartbiz.entity.SubscriptionRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRequestRepository
        extends JpaRepository<SubscriptionRequest, Integer> {

    List<SubscriptionRequest> findByBusiness_BusinessIdOrderByRequestDateDesc(
            Integer businessId
    );

    List<SubscriptionRequest> findByStatusOrderByRequestDateDesc(
            RequestStatus status
    );

    boolean existsByBusiness_BusinessIdAndStatus(
            Integer businessId,
            RequestStatus status
    );
}
