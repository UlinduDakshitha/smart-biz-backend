
package com.smartbiz.repository;

import com.smartbiz.entity.Payment;
import com.smartbiz.entity.Payment.PaymentStatus;
import com.smartbiz.entity.Payment.ApprovalStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByBusiness_BusinessIdOrderByCreatedAtDesc(
            Integer businessId
    );

    List<Payment> findByApprovalStatusOrderByCreatedAtDesc(
            ApprovalStatus approvalStatus
    );

    List<Payment> findByPaymentStatusAndApprovalStatusOrderByCreatedAtDesc(
            PaymentStatus paymentStatus,
            ApprovalStatus approvalStatus
    );

    Optional<Payment> findByMerchantReference(String merchantReference);

    Optional<Payment> findByGatewayReference(String gatewayReference);

    boolean existsByMerchantReference(String merchantReference);
}
