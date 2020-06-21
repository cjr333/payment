package org.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PayApproval")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayApprovalEntity {
  @Id
  private String paymentId;

  private String approvalInfo;
}
