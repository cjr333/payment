package org.example.payment.repository;

import org.example.payment.entity.PayApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayApprovalRepository extends JpaRepository<PayApprovalEntity, String> {
}
