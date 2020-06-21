package org.example.payment.service;

import org.apache.commons.lang3.StringUtils;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.SampleEntityGenerator;
import org.example.payment.repository.PayApprovalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ApprovalServiceTest {
  @MockBean PayApprovalRepository payApprovalRepository;
  @Autowired ApprovalService approvalService;

  /*
    데이터길이 숫자(4)
    데이터구분 문자(10)
    관리번호 문자(20)
    카드번호 숫자L(20)
    할부개월수 숫자0(2)
    카드유효기간 숫자L(4)
    CVC 숫자L(3)
    거래금액 숫자(10)
    부가가치세 숫자0(10)
    원거래관리번호 문자(20) --> 결제 시는 빈문자열
    암호화된카드정보 문자(300)
    예비필드 문자(47)
   */

  @Test
  void successRequestApproval() {
    CreditCardEntity creditCardEntity = SampleEntityGenerator.genCreditCardEntity();
    creditCardEntity.encrypt();
    PayTransactionEntity payTransactionEntity = SampleEntityGenerator.genPayTransactionEntity(TransactionType.PAYMENT);
    approvalService.requestApproval(creditCardEntity, payTransactionEntity);

    payTransactionEntity = SampleEntityGenerator.genPayTransactionEntity(TransactionType.CANCEL);
    approvalService.requestApproval(creditCardEntity, payTransactionEntity);

    verify(payApprovalRepository, times(2)).save(argThat(argument -> verifyApprovalInfo(argument.getApprovalInfo())));
  }

  private boolean verifyApprovalInfo(String approvalInfo) {
    AtomicReference<String> atomicReference = new AtomicReference<>(approvalInfo);
    Integer length = parseInt(atomicReference, 4);
    if (length != atomicReference.get().length()) {
      return false;
    }
    TransactionType transactionType = TransactionType.valueOf(parseStr(atomicReference, 10));
    String transactionId = parseStr(atomicReference, 20);
    Long cardNum = parseLong(atomicReference, 20);
    int installment = parseInt(atomicReference, 2);
    if (installment < 0 || installment > 12) {
      return false;
    }
    if (transactionType == TransactionType.CANCEL && installment != 0) {
      return false;
    }
    int validThru = parseInt(atomicReference, 4);
    int cvc = parseInt(atomicReference, 3);
    long amount = parseLong(atomicReference, 10);
    long tax = parseLong(atomicReference, 10);
    String paymentId = parseStr(atomicReference, 20);
    if (transactionType == TransactionType.PAYMENT && StringUtils.isNotEmpty(paymentId)) {
      return false;
    }
    if (transactionType == TransactionType.CANCEL && StringUtils.isEmpty(paymentId)) {
      return false;
    }
    String encCardInfo = parseStr(atomicReference, 300);
    String buffer = parseStr(atomicReference, 47);
    if (StringUtils.isNotEmpty(buffer)) {
      return false;
    }
    // ApprovalInfo 소진이 다 안됨.
    return !StringUtils.isNotEmpty(atomicReference.get());
  }

  private Integer parseInt(AtomicReference<String> atomicReference, int length) {
    String str = atomicReference.get();
    int val = Integer.parseInt(str.substring(0, length).trim());
    atomicReference.set(str.substring(length));
    return val;
  }

  private Long parseLong(AtomicReference<String> atomicReference, int length) {
    String str = atomicReference.get();
    long val = Long.parseLong(str.substring(0, length).trim());
    atomicReference.set(str.substring(length));
    return val;
  }

  private String parseStr(AtomicReference<String> atomicReference, int length) {
    String str = atomicReference.get();
    String val = str.substring(0, length).trim();
    atomicReference.set(str.substring(length));
    return val;
  }
}