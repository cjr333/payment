package org.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "Payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {
  @Id
  private String paymentId;

  private String cardNum;

  private Long payDateMs;

  private Integer installment;

  private Long remainAmount;

  private Long remainTax;

  private Long orgAmount;

  private Long orgTax;

  @Version
  private int version;
}
