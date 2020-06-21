package org.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment.util.Aes256Util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Table(name = "CreditCard")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardEntity {
  @Id
  private String cardNum;

  @Transient
  private String validThru;    // mmyy

  @Transient
  private String cvc;

  @Column(updatable = false)
  private String encCardInfo;

  private Long lastPayDateMs;

  @Version
  private int version;

  public CreditCardEntity encrypt() {
    String concatenated = String.join("|", cardNum, validThru, cvc);
    encCardInfo = Aes256Util.encrypt(concatenated);
    return this;
  }

  public CreditCardEntity decrypt() {
    try {
      String[] parts = Aes256Util.decrypt(encCardInfo).split("\\|");
      cardNum = parts[0];
      validThru = parts[1];
      cvc = parts[2];
      return this;
    } catch (Exception ex) {
      throw new IllegalStateException("Decrypt encCardInfo failed");
    }
  }
}
