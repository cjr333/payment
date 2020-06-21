package org.example.payment.repository;

import org.example.payment.entity.CreditCardEntity;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("local")
class CreditCardEntityRepositoryTest {
  @Autowired private CreditCardRepository creditCardRepository;

  @Test
  void successSave() {
    CreditCardEntity creditCardEntity = CreditCardEntity.builder()
        .cardNum("1234567890123456")
        .validThru("1228")
        .cvc("123")
        .lastPayDateMs(System.currentTimeMillis())
        .build();
    creditCardEntity.encrypt();
    CreditCardEntity saved = creditCardRepository.save(creditCardEntity);
    assertEquals(creditCardEntity.getCardNum(), saved.getCardNum());
    assertArrayEquals(creditCardEntity.getEncCardInfo(), saved.getEncCardInfo());
    assertEquals(creditCardEntity.getLastPayDateMs(), saved.getLastPayDateMs());
  }

  @Test
  void successEncDec() {
    CreditCardEntity creditCardEntity = CreditCardEntity.builder()
        .cardNum("1234567890123456")
        .validThru("1228")
        .cvc("123")
        .lastPayDateMs(System.currentTimeMillis())
        .build();
    creditCardEntity.encrypt();
    assertNotNull(creditCardEntity.getEncCardInfo());
    creditCardRepository.save(creditCardEntity);

    CreditCardEntity loaded = creditCardRepository.findById(creditCardEntity.getCardNum()).orElseThrow(() -> new IllegalStateException("not exist"));
    loaded.decrypt();

    assertEquals(creditCardEntity.getCardNum(), loaded.getCardNum());
    assertArrayEquals(creditCardEntity.getEncCardInfo(), loaded.getEncCardInfo());
    assertEquals(creditCardEntity.getLastPayDateMs(), loaded.getLastPayDateMs());
    assertEquals(creditCardEntity.getValidThru(), loaded.getValidThru());
    assertEquals(creditCardEntity.getCvc(), loaded.getCvc());
  }
}