package org.example.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;
import org.example.payment.model.request.CancelRequest;
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
        .orElse(payRequest.toCreditCardEntity().encrypt());
    if (!Objects.equals(creditCardEntity.getCardNum(), payRequest.getCardNum())
        || !Objects.equals(creditCardEntity.getValidThru(), payRequest.getValidThru())
        || !Objects.equals(creditCardEntity.getCvc(), payRequest.getCvc())) {
      throw new IllegalArgumentException("invalid card info");
    }
    creditCardEntity.setLastPayDateMs(nowMs);
    creditCardRepository.save(creditCardEntity);

    // 엔티티 생성 및 카드사 승인 요청
    PaymentEntity paymentEntity = payRequest.toPaymentEntity();
    paymentEntity.setPaymentId(uniqueId);
    paymentEntity.setPayDateMs(nowMs);
    PayTransactionEntity payTransactionEntity = payRequest.toPayTransactionEntity();
    payTransactionEntity.setTransactionId(uniqueId);
    payTransactionEntity.setPaymentId(uniqueId);
    payTransactionEntity.setTransactionDateMs(nowMs);
    approvalService.requestApproval(creditCardEntity, payTransactionEntity);

    // Payment 정보 저장
    paymentRepository.save(paymentEntity);

    // PayTransaction 정보 저장
    payTransactionRepository.save(payTransactionEntity);

    return PayTransaction.builder()
        .transactionId(payTransactionEntity.getTransactionId())
        .transactionType(payTransactionEntity.getTransactionType())
        .amount(payTransactionEntity.getAmount())
        .tax(payTransactionEntity.getTax())
        .build();
  }

  @Transactional
  public PayTransaction cancel(CancelRequest cancelRequest) {
    PayTransactionEntity payTransactionEntity = cancelRequest.toPayTransactionEntity();
    final long nowMs = System.currentTimeMillis();
    String uniqueId = UniqueIdGenerator.generate(20);
    payTransactionEntity.setTransactionId(uniqueId);
    payTransactionEntity.setTransactionDateMs(nowMs);

    // Payment 정보 조회 및 유효성 검증
    PaymentEntity paymentEntity = paymentRepository.findById(payTransactionEntity.getPaymentId())
        .orElseThrow(() -> new IllegalArgumentException("not exist payment"));
    if (paymentEntity.getRemainAmount() < payTransactionEntity.getAmount()
      || paymentEntity.getRemainTax() < payTransactionEntity.getTax()
      || (paymentEntity.getRemainAmount() - payTransactionEntity.getAmount() < paymentEntity.getRemainTax() - payTransactionEntity.getTax())) {
      throw new IllegalArgumentException("invalid amount or tax");
    }

    // 카드사에 승인 요청
    CreditCardEntity creditCardEntity = creditCardRepository.findById(paymentEntity.getCardNum())
        .orElseThrow(() -> new IllegalArgumentException("not exist credit card"));
    creditCardEntity.decrypt();
    approvalService.requestApproval(creditCardEntity, payTransactionEntity);

    // 취소 금액 적용
    paymentEntity.setRemainAmount(paymentEntity.getRemainAmount() - payTransactionEntity.getAmount());
    paymentEntity.setRemainTax(paymentEntity.getRemainTax() - payTransactionEntity.getTax());
    payTransactionEntity.setRemainAmount(paymentEntity.getRemainAmount());
    payTransactionEntity.setRemainTax(paymentEntity.getRemainTax());

    // Payment 정보 저장
    paymentRepository.save(paymentEntity);

    // PayTransaction 정보 저장
    payTransactionRepository.save(payTransactionEntity);

    return PayTransaction.builder()
        .transactionId(payTransactionEntity.getTransactionId())
        .transactionType(payTransactionEntity.getTransactionType())
        .amount(payTransactionEntity.getAmount())
        .tax(payTransactionEntity.getTax())
        .build();
  }

  public PayTransaction findTransaction(String transactionId) {
    PayTransactionEntity payTransactionEntity = payTransactionRepository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("not exist transaction"));
    PaymentEntity paymentEntity = paymentRepository.findById(payTransactionEntity.getPaymentId())
        .orElseThrow(() -> new IllegalArgumentException("not exist payment"));
    CreditCardEntity creditCardEntity = creditCardRepository.findById(paymentEntity.getCardNum())
        .orElseThrow(() -> new IllegalArgumentException("not exist credit card"));
    creditCardEntity.decrypt();

    String cardNum = creditCardEntity.getCardNum();
    int startMask = 6;
    int endMask = cardNum.length() - 3;
    String maskedCardNum = cardNum.substring(0, startMask) + StringUtils.repeat('*', endMask - startMask) + cardNum.substring(endMask);

    return PayTransaction.builder()
        .transactionId(payTransactionEntity.getTransactionId())
        .transactionType(payTransactionEntity.getTransactionType())
        .amount(payTransactionEntity.getAmount())
        .tax(payTransactionEntity.getTax())
        .remainAmount(payTransactionEntity.getRemainAmount())
        .remainTax(payTransactionEntity.getRemainTax())
        .cardNum(maskedCardNum)
        .validThru(creditCardEntity.getValidThru())
        .cvc(creditCardEntity.getCvc())
        .build();
  }
}
