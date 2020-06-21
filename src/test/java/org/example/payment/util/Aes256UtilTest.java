package org.example.payment.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Aes256UtilTest {
  @Test
  void success() {
    String sample = RandomStringUtils.randomAlphanumeric(16, 32) + "|" + RandomStringUtils.randomAlphanumeric(16, 32);
    byte[] encrypted = Aes256Util.encrypt(sample);
    String decrypted = Aes256Util.decrypt(encrypted);
    assertEquals(sample, decrypted);
  }
}