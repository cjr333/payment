package org.example.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.PayApprovalEntity;
import org.example.payment.entity.PaymentEntity;
import org.example.payment.repository.PayApprovalRepository;
import org.example.payment.util.FormattedStringBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalService {
  private final PayApprovalRepository payApprovalRepository;
  private final int STRING_LENGTH = 446;

  /*
    데이터길이 숫자(4)
    데이터구분 문자(10)
    관리번호 문자(20)
    카드번호 숫자L(20) --> 문자로 처리
    할부개월수 숫자0(2)
    카드유효기간 숫자L(4) --> 문자로 처리
    CVC 숫자L(3) --> 문자로 처리
    거래금액 숫자(10)
    부가가치세 숫자0(10)
    원거래관리번호 문자(20) --> 결제 시는 빈문자열
    암호화된카드정보 문자(300)
    예비필드 문자(47)
   */

  public boolean requestPay(CreditCardEntity creditCardEntity, PaymentEntity paymentEntity) {
    FormattedStringBuilder fsb = new FormattedStringBuilder();
    String approvalInfo = fsb.num(STRING_LENGTH, 4)
        .str(TransactionType.PAYMENT.name(), 10)
        .str(paymentEntity.getPaymentId(), 20)
        .str(creditCardEntity.getCardNum(), 20)
        .num0(paymentEntity.getInstallment(), 2)
        .str(creditCardEntity.getValidThru(), 4)
        .str(creditCardEntity.getCvc(), 3)
        .num(paymentEntity.getOrgAmount(), 10)
        .num0(paymentEntity.getOrgTax(), 10)
        .str("", 20)
        .str(creditCardEntity.getEncCardInfo(), 300)
        .str("", 47)
        .toString();
    PayApprovalEntity payApprovalEntity = PayApprovalEntity.builder()
        .paymentId(paymentEntity.getPaymentId())
        .approvalInfo(approvalInfo)
        .build();
    payApprovalRepository.save(payApprovalEntity);
    return true;
  }
}
