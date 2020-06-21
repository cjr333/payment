package org.example.payment.service;

import org.apache.commons.lang3.StringUtils;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;
import org.example.payment.util.SampleGenerator;
import org.example.payment.model.PayTransaction;
import org.example.payment.model.request.CancelRequest;
import org.example.payment.model.request.PayRequest;
import org.example.payment.repository.CreditCardRepository;
import org.example.payment.repository.PayTransactionRepository;
import org.example.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {
  @Autowired PaymentService paymentService;
  @MockBean CreditCardRepository creditCardRepository;
  @MockBean PaymentRepository paymentRepository;
  @MockBean PayTransactionRepository payTransactionRepository;
  @MockBean ApprovalService approvalService;

  @Test
  void successPayFirstPay() {
    PayRequest payRequest = SampleGenerator.genPayRequest();

    // mock
    when(creditCardRepository.findById(payRequest.getCardNum())).thenReturn(Optional.empty());
    when(creditCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(payTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PayTransaction payTransaction = paymentService.pay(payRequest);

    // verify
    assertNotNull(payTransaction.getTransactionId());
    assertEquals(TransactionType.PAYMENT, payTransaction.getTransactionType());
    assertEquals(payRequest.getAmount(), payTransaction.getAmount());
    assertEquals(payRequest.getTax(), payTransaction.getTax());
    assertNull(payTransaction.getRemainAmount());
    assertNull(payTransaction.getRemainTax());
    assertNull(payTransaction.getCardNum());
    assertNull(payTransaction.getValidThru());
    assertNull(payTransaction.getCvc());

    verify(creditCardRepository, times(1)).save(argThat(creditCardEntity ->
        creditCardEntity.getCardNum().equals(payRequest.getCardNum())
            && creditCardEntity.getValidThru().equals(payRequest.getValidThru())
            && creditCardEntity.getCvc().equals(payRequest.getCvc())
            && StringUtils.isNotEmpty(creditCardEntity.getEncCardInfo())));
    verify(paymentRepository, times(1)).save(argThat(paymentEntity ->
        paymentEntity.getPaymentId().equals(payTransaction.getTransactionId())
            && paymentEntity.getCardNum().equals(payRequest.getCardNum())
            && paymentEntity.getInstallment().equals(payRequest.getInstallment())
            && paymentEntity.getOrgAmount().equals(payRequest.getAmount())
            && paymentEntity.getRemainAmount().equals(payRequest.getAmount())
            && paymentEntity.getOrgTax().equals(payRequest.getTax())
            && paymentEntity.getRemainTax().equals(payRequest.getTax())));
    verify(payTransactionRepository, times(1)).save(argThat(payTransactionEntity ->
        payTransactionEntity.getTransactionId().equals(payTransaction.getTransactionId())
            && payTransactionEntity.getPaymentId().equals(payTransaction.getTransactionId())
            && payTransactionEntity.getTransactionType().equals(payTransaction.getTransactionType())
            && payTransactionEntity.getInstallment().equals(payRequest.getInstallment())
            && payTransactionEntity.getAmount().equals(payRequest.getAmount())
            && payTransactionEntity.getRemainAmount().equals(payRequest.getAmount())
            && payTransactionEntity.getTax().equals(payRequest.getTax())
            && payTransactionEntity.getRemainTax().equals(payRequest.getTax())));
    verify(approvalService, times(1)).requestApproval(any(), any());
  }

  @Test
  void successPaySecondPay() {
    PayRequest payRequest = SampleGenerator.genPayRequest();
    final long lastPayDateMs = System.currentTimeMillis() - 1000000;
    CreditCardEntity dbCreditCardEntity = payRequest.toCreditCardEntity().encrypt();
    dbCreditCardEntity.setLastPayDateMs(lastPayDateMs);

    // mock
    when(creditCardRepository.findById(payRequest.getCardNum())).thenReturn(Optional.of(dbCreditCardEntity));
    when(creditCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(payTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    paymentService.pay(payRequest);

    // verify
    verify(creditCardRepository, times(1)).save(argThat(creditCardEntity ->
        creditCardEntity.getCardNum().equals(payRequest.getCardNum())
            && creditCardEntity.getValidThru().equals(payRequest.getValidThru())
            && creditCardEntity.getCvc().equals(payRequest.getCvc())
            && StringUtils.isNotEmpty(creditCardEntity.getEncCardInfo())
            && creditCardEntity.getLastPayDateMs() > lastPayDateMs && creditCardEntity.getLastPayDateMs() <= System.currentTimeMillis()));
  }

  @Test
  void failPayInvalidCardInfo() {
    PayRequest payRequest = SampleGenerator.genPayRequest();
    final long lastPayDateMs = System.currentTimeMillis() - 1000000;
    CreditCardEntity dbCreditCardEntity = payRequest.toCreditCardEntity().encrypt();
    dbCreditCardEntity.setLastPayDateMs(lastPayDateMs);

    // mock
    when(creditCardRepository.findById(payRequest.getCardNum())).thenReturn(Optional.of(dbCreditCardEntity));
    when(creditCardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(payTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    payRequest.setCvc("000");   // malicious

    // verify
    assertThrows(IllegalArgumentException.class, () -> paymentService.pay(payRequest), "invalid card info");
  }

  @Test
  void successCancel() {
    PaymentEntity dbPaymentEntity = SampleGenerator.genPaymentEntity();
    CreditCardEntity dbCreditCardEntity = SampleGenerator.genCreditCardEntity().encrypt();
    dbCreditCardEntity.setCardNum(dbPaymentEntity.getCardNum());
    dbPaymentEntity.setCreditCardEntity(dbCreditCardEntity);
    CancelRequest cancelRequest = SampleGenerator.genCancelRequest(dbPaymentEntity);

    // mock
    when(paymentRepository.findById(dbPaymentEntity.getPaymentId())).thenReturn(Optional.of(dbPaymentEntity));
    when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(payTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    final long expectedRemainAmount = dbPaymentEntity.getRemainAmount() - cancelRequest.getAmount();
    final long expectedRemainTax = dbPaymentEntity.getRemainTax() - cancelRequest.getTax();

    PayTransaction payTransaction = paymentService.cancel(cancelRequest);

    // verify
    assertNotNull(payTransaction.getTransactionId());
    assertEquals(TransactionType.CANCEL, payTransaction.getTransactionType());
    assertEquals(cancelRequest.getAmount(), payTransaction.getAmount());
    assertEquals(cancelRequest.getTax(), payTransaction.getTax());
    assertNull(payTransaction.getRemainAmount());
    assertNull(payTransaction.getRemainTax());
    assertNull(payTransaction.getCardNum());
    assertNull(payTransaction.getValidThru());
    assertNull(payTransaction.getCvc());
    verify(paymentRepository, times(1)).save(argThat(paymentEntity ->
        paymentEntity.getPaymentId().equals(dbPaymentEntity.getPaymentId())
            && paymentEntity.getRemainAmount().equals(expectedRemainAmount)
            && paymentEntity.getRemainTax().equals(expectedRemainTax)));
    verify(payTransactionRepository, times(1)).save(argThat(payTransactionEntity ->
        payTransactionEntity.getTransactionId().equals(payTransaction.getTransactionId())
            && payTransactionEntity.getPaymentId().equals(dbPaymentEntity.getPaymentId())
            && payTransactionEntity.getTransactionType().equals(payTransaction.getTransactionType())
            && payTransactionEntity.getInstallment().equals(0)
            && payTransactionEntity.getAmount().equals(cancelRequest.getAmount())
            && payTransactionEntity.getRemainAmount().equals(expectedRemainAmount)
            && payTransactionEntity.getTax().equals(cancelRequest.getTax())
            && payTransactionEntity.getRemainTax().equals(expectedRemainTax)));
    verify(approvalService, times(1)).requestApproval(any(), any());
  }

  @Test
  void failCancel() {
    PaymentEntity dbPaymentEntity = SampleGenerator.genPaymentEntity();
    CreditCardEntity dbCreditCardEntity = SampleGenerator.genCreditCardEntity().encrypt();
    dbCreditCardEntity.setCardNum(dbPaymentEntity.getCardNum());

    // mock
    when(creditCardRepository.findById(dbPaymentEntity.getCardNum())).thenReturn(Optional.of(dbCreditCardEntity));
    when(paymentRepository.findById(dbPaymentEntity.getPaymentId())).thenReturn(Optional.of(dbPaymentEntity));
    when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(payTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // case. 취소금액이 남은 결제액보다 큰 경우
    CancelRequest cancelRequest = SampleGenerator.genCancelRequest(dbPaymentEntity);
    cancelRequest.setAmount(dbPaymentEntity.getRemainAmount() + 1);
    assertThrows(IllegalArgumentException.class, () -> paymentService.cancel(cancelRequest), "invalid amount or tax");

    // case. 취소부가가치세가 남은 부가가치세보다 큰 경우
    CancelRequest cancelRequest2 = SampleGenerator.genCancelRequest(dbPaymentEntity);
    cancelRequest2.setTax(dbPaymentEntity.getRemainTax() + 1);
    assertThrows(IllegalArgumentException.class, () -> paymentService.cancel(cancelRequest2), "invalid amount or tax");

    // case. 남은 결제금액이 남은 부가가치세보다 작은 경우
    CancelRequest cancelRequest3 = SampleGenerator.genCancelRequest(dbPaymentEntity);
    cancelRequest3.setAmount(dbPaymentEntity.getRemainAmount());
    assertThrows(IllegalArgumentException.class, () -> paymentService.cancel(cancelRequest3), "invalid amount or tax");
  }

  @Test
  void successFindTransaction() {
    PayTransactionEntity payTransactionEntity = SampleGenerator.genPayTransactionEntity(TransactionType.PAYMENT);
    PaymentEntity paymentEntity = SampleGenerator.genPaymentEntity();
    CreditCardEntity creditCardEntity = SampleGenerator.genCreditCardEntity().encrypt();
    payTransactionEntity.setPaymentEntity(paymentEntity);
    paymentEntity.setCreditCardEntity(creditCardEntity);

    // mock
    when(payTransactionRepository.findById(payTransactionEntity.getTransactionId())).thenReturn(Optional.of(payTransactionEntity));

    PayTransaction payTransaction = paymentService.findTransaction(payTransactionEntity.getTransactionId());

    // verify
    String cardNum = creditCardEntity.getCardNum();
    String maskedCardNum = StringUtils.left(cardNum, 6) + StringUtils.repeat('*', cardNum.length() - 9) + StringUtils.right(cardNum, 3);
    assertNotNull(payTransaction.getTransactionId());
    assertEquals(payTransactionEntity.getTransactionType(), payTransaction.getTransactionType());
    assertEquals(payTransactionEntity.getAmount(), payTransaction.getAmount());
    assertEquals(payTransactionEntity.getTax(), payTransaction.getTax());
    assertEquals(payTransactionEntity.getRemainAmount(), payTransaction.getRemainAmount());
    assertEquals(payTransactionEntity.getRemainTax(), payTransaction.getRemainTax());
    assertEquals(payTransaction.getCardNum(), maskedCardNum);
    assertEquals(creditCardEntity.getValidThru(), payTransaction.getValidThru());
    assertEquals(creditCardEntity.getCvc(), payTransaction.getCvc());
  }

  @Test
  void failFindTransaction() {
    // mock
    when(payTransactionRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> paymentService.findTransaction("transactionId"), "not exist transaction");
  }
}