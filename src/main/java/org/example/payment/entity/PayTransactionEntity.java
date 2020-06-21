package org.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment.constant.TransactionType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "PayTransaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "payTransactionWithPaymentAndCreditCard",
    attributeNodes = {
        @NamedAttributeNode("paymentEntity"),
        @NamedAttributeNode(value = "paymentEntity", subgraph = "payment-subgraph"),
    },
    subgraphs = {
        @NamedSubgraph(
            name = "payment-subgraph",
            attributeNodes = {
                @NamedAttributeNode("creditCardEntity")
            }
        )
    }
)
public class PayTransactionEntity {
  @PreUpdate
  private void preUpdate() {
    throw new UnsupportedOperationException("only insertable entity");
  }

  @Id
  private String transactionId;

  private String paymentId;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  private Long transactionDateMs;

  private Integer installment;

  private Long amount;

  private Long tax;

  private Long remainAmount;

  private Long remainTax;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "paymentId", insertable = false, updatable = false)
  private PaymentEntity paymentEntity;
}
