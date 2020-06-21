package org.example.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;
import org.example.payment.repository.CreditCardRepository;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.model.PayTransaction;
import org.example.payment.model.request.PayRequest;
import org.example.payment.repository.PayTransactionRepository;
import org.example.payment.repository.PaymentRepository;
import org.example.payment.util.UniqueIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
  private final ApprovalService approvalService;
  private final CreditCardRepository creditCardRepository;
  private final PaymentRepository paymentRepository;
  private final PayTransactionRepository payTransactionRepository;

  @Transactional
  public PayTransaction pay(PayRequest payRequest) {
    final long nowMs = System.currentTimeMillis();
    String uniqueId = UniqueIdGenerator.generate(20);

    // CreditCard 정보 저장
    CreditCardEntity creditCardEntity = creditCardRepository.findById(payRequest.getCardNum())
        .map(CreditCardEntity::decrypt)
        .orElse(payRequest.toCreditCardEntity());
    if (!Objects.equals(creditCardEntity.getCardNum(), payRequest.getCardNum())
        || !Objects.equals(creditCardEntity.getValidThru(), payRequest.getValidThru())
        || !Objects.equals(creditCardEntity.getCvc(), payRequest.getCvc())) {
      throw new IllegalArgumentException("invalid card info");
    }
    creditCardEntity.setLastPayDateMs(nowMs);
    creditCardRepository.save(creditCardEntity);

    // 카드사에 승인 요청
    PaymentEntity paymentEntity = payRequest.toPaymentEntity();
    paymentEntity.setPaymentId(uniqueId);
    paymentEntity.setPayDateMs(nowMs);
    approvalService.requestPay(creditCardEntity, paymentEntity);

    // Payment 정보 저장
    paymentRepository.save(paymentEntity);

    // PayTransaction 정보 저장
    PayTransactionEntity payTransactionEntity = payRequest.toPayTransactionEntity();
    payTransactionEntity.setTransactionId(uniqueId);
    payTransactionEntity.setPaymentId(uniqueId);
    payTransactionEntity.setTransactionDateMs(nowMs);
    payTransactionRepository.save(payTransactionEntity);

    return PayTransaction.builder()
        .transactionId(payTransactionEntity.getTransactionId())
        .transactionType(payTransactionEntity.getTransactionType())
        .amount(payTransactionEntity.getAmount())
        .tax(payTransactionEntity.getTax())
        .build();
  }
}