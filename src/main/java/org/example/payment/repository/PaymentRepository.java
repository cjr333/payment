package org.example.payment.repository;

import org.example.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
  @EntityGraph(attributePaths = "creditCardEntity")
  Optional<PaymentEntity> findById(String id);
}
