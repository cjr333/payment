package org.example.payment.util;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Aes256Util {
  private static byte[] SECRET_KEY = "SVhWYAaYYJfrh2v841hJGPaD74Jh6JiQ".getBytes(StandardCharsets.UTF_8);
  private static byte[] IV = "oXyTJJ5fWNUtXIYl".getBytes(StandardCharsets.UTF_8);

  public static String encrypt(String str) {
    try {
      SecretKey secureKey = new SecretKeySpec(SECRET_KEY, "AES");

      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
      c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV));

      return Base64Utils.encodeToString(c.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception ex) {
      throw new RuntimeException("Encrypt failed", ex);
    }
  }

  public static String decrypt(String encStr) {
    try {
      SecretKey secureKey = new SecretKeySpec(SECRET_KEY, "AES");

      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
      c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV));

      return new String(c.doFinal(Base64Utils.decodeFromString(encStr)), StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new RuntimeException("Decrypt failed", ex);
    }
  }
}
