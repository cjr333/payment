package org.example.payment.repository;

import org.example.payment.entity.CreditCardEntity;
import org.example.payment.util.SampleGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Profile("local")
class CreditCardEntityRepositoryTest {
  @Autowired private CreditCardRepository creditCardRepository;

  @Test
  void successSave() {
    CreditCardEntity creditCardEntity = SampleGenerator.genCreditCardEntity();
    creditCardEntity.encrypt();
    CreditCardEntity saved = creditCardRepository.save(creditCardEntity);
    assertEquals(creditCardEntity.getCardNum(), saved.getCardNum());
    assertEquals(creditCardEntity.getEncCardInfo(), saved.getEncCardInfo());
    assertEquals(creditCardEntity.getLastPayDateMs(), saved.getLastPayDateMs());
  }

  @Test
  void failConcurrentModify() {
    CreditCardEntity creditCardEntity = SampleGenerator.genCreditCardEntity().encrypt();
    creditCardRepository.save(creditCardEntity);
    List<CreditCardEntity> creditCardEntities = IntStream.range(0, 4)
        .mapToObj(intValue -> {
          CreditCardEntity entity = SampleGenerator.genCreditCardEntity().encrypt();
          entity.setCardNum(creditCardEntity.getCardNum());   // 동시 업데이트 시뮬레이션을 위해 같은 키로 설정
          return entity;
        })
        .collect(Collectors.toList());

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> creditCardEntities.parallelStream().forEach(creditCardRepository::save));
  }

  @Test
  void successEncDec() {
    CreditCardEntity creditCardEntity = SampleGenerator.genCreditCardEntity();
    creditCardEntity.encrypt();
    assertNotNull(creditCardEntity.getEncCardInfo());
    creditCardRepository.save(creditCardEntity);

    CreditCardEntity loaded = creditCardRepository.findById(creditCardEntity.getCardNum()).orElseThrow(() -> new IllegalStateException("not exist"));
    loaded.decrypt();

    assertEquals(creditCardEntity.getCardNum(), loaded.getCardNum());
    assertEquals(creditCardEntity.getEncCardInfo(), loaded.getEncCardInfo());
    assertEquals(creditCardEntity.getLastPayDateMs(), loaded.getLastPayDateMs());
    assertEquals(creditCardEntity.getValidThru(), loaded.getValidThru());
    assertEquals(creditCardEntity.getCvc(), loaded.getCvc());
  }
}