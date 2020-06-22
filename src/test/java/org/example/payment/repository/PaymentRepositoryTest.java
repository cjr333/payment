package org.example.payment.repository;

import org.example.payment.entity.PaymentEntity;
import org.example.payment.util.SampleGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Profile("local")
class PaymentRepositoryTest {
  @Autowired private PaymentRepository paymentRepository;

  @Test
  void successSave() {
    PaymentEntity paymentEntity = SampleGenerator.genPaymentEntity();
    PaymentEntity saved = paymentRepository.save(paymentEntity);
    assertTrue(new ReflectionEquals(paymentEntity, "version").matches(saved));
  }

  @Test
  void failConcurrentModify() {
    PaymentEntity paymentEntity = SampleGenerator.genPaymentEntity();
    paymentRepository.save(paymentEntity);
    List<PaymentEntity> paymentEntities = IntStream.range(0, 4)
        .mapToObj(intValue -> {
          PaymentEntity entity = SampleGenerator.genPaymentEntity();
          entity.setPaymentId(paymentEntity.getPaymentId());   // 동시 업데이트 시뮬레이션을 위해 같은 키로 설정
          return entity;
        })
        .collect(Collectors.toList());

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> paymentEntities.parallelStream().forEach(paymentRepository::save));
  }
}