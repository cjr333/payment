package org.example.payment.repository;

import org.example.payment.entity.PayTransactionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayTransactionRepository extends JpaRepository<PayTransactionEntity, String> {
  @EntityGraph(value = "payTransactionWithPaymentAndCreditCard")
  Optional<PayTransactionEntity> findById(String id);
}
