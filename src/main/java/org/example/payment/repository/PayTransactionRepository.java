package org.example.payment.repository;

import org.example.payment.entity.PayTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayTransactionRepository extends JpaRepository<PayTransactionEntity, String> {
}
