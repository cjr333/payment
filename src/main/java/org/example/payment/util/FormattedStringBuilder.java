package org.example.payment.util;

import org.apache.commons.lang3.StringUtils;

public class FormattedStringBuilder {
  private final StringBuilder sb;

  public FormattedStringBuilder() {
    sb = new StringBuilder();
  }

  public FormattedStringBuilder(int capacity) {
    sb = new StringBuilder(capacity);
  }

  public String toString() {
    return sb.toString();
  }

  public FormattedStringBuilder num(int intVal, int length) {
    sb.append(leftPad(String.valueOf(intVal), length, ' '));
    return this;
  }

  public FormattedStringBuilder num0(int intVal, int length) {
    sb.append(leftPad(String.valueOf(intVal), length, '0'));
    return this;
  }

  public FormattedStringBuilder numL(int intVal, int length) {
    sb.append(rightPad(String.valueOf(intVal), length, ' '));
    return this;
  }

  public FormattedStringBuilder num(long longVal, int length) {
    sb.append(leftPad(String.valueOf(longVal), length, ' '));
    return this;
  }

  public FormattedStringBuilder num0(long longVal, int length) {
    sb.append(leftPad(String.valueOf(longVal), length, '0'));
    return this;
  }

  public FormattedStringBuilder numL(long longVal, int length) {
    sb.append(rightPad(String.valueOf(longVal), length, ' '));
    return this;
  }

  public FormattedStringBuilder str(String str, int length) {
    sb.append(rightPad(str, length, ' '));
    return this;
  }

  private String leftPad(String str, int length, char padding) {
    if (str == null) {
      throw new IllegalArgumentException("null is not allowed");
    }
    if (str.length() > length) {
      throw new IllegalArgumentException(String.format("%s exceeds the length %d", str, length));
    }
    return StringUtils.leftPad(str, length, padding);
  }

  private String rightPad(String str, int length, char padding) {
    if (str == null) {
      throw new IllegalArgumentException("null is not allowed");
    }
    if (str.length() > length) {
      throw new IllegalArgumentException(String.format("%s exceeds the length %d", str, length));
    }
    return StringUtils.rightPad(str, length, padding);
  }
}
