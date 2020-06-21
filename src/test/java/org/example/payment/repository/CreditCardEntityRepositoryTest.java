package org.example.payment.repository;

import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.SampleEntityGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Profile("local")
class CreditCardEntityRepositoryTest {
  @Autowired private CreditCardRepository creditCardRepository;

  @Test
  void successSave() {
    CreditCardEntity creditCardEntity = SampleEntityGenerator.genCreditCardEntity();
    creditCardEntity.encrypt();
    CreditCardEntity saved = creditCardRepository.save(creditCardEntity);
    assertEquals(creditCardEntity.getCardNum(), saved.getCardNum());
    assertEquals(creditCardEntity.getEncCardInfo(), saved.getEncCardInfo());
    assertEquals(creditCardEntity.getLastPayDateMs(), saved.getLastPayDateMs());
  }

  @Test
  void successEncDec() {
    CreditCardEntity creditCardEntity = SampleEntityGenerator.genCreditCardEntity();
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