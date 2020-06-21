package org.example.payment.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class UniqueIdGenerator {
  private static final long NORMALIZE_FACTOR = 50 * 365 * 24 * 60 * 60;    // 2020년 기준으로 50년에 해당하는 초

  public static String generate(int length) {
    if (length < 10) {
      throw new IllegalArgumentException("too short length for unique id");
    }
    int normalizedTimestamp = (int)((System.currentTimeMillis() / 1000) - NORMALIZE_FACTOR);

    String timestampHex = StringUtils.leftPad(Integer.toHexString(normalizedTimestamp), 8, '0');
    return timestampHex + RandomStringUtils.randomAlphanumeric(length - 8);
  }
}
