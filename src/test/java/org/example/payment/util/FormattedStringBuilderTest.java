package org.example.payment.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FormattedStringBuilderTest {
  private FormattedStringBuilder fsb = new FormattedStringBuilder();

  @Test
  void successNum() {
    fsb.num(12, 5);
    fsb.num(345L, 5);
    assertEquals(fsb.toString(), "   12  345");
  }

  @Test
  void successNum0() {
    fsb.num0(12, 5);
    fsb.num0(345L, 5);
    assertEquals(fsb.toString(), "0001200345");
  }

  @Test
  void successNumL() {
    fsb.numL(12, 5);
    fsb.numL(345L, 5);
    assertEquals(fsb.toString(), "12   345  ");
  }

  @Test
  void successStr() {
    fsb.str("abc", 5);
    assertEquals(fsb.toString(), "abc  ");
  }

  @Test
  void fail() {
    assertThrows(IllegalArgumentException.class, () -> fsb.num(123456, 5));
    assertThrows(IllegalArgumentException.class, () -> fsb.num(123456L, 5));
    assertThrows(IllegalArgumentException.class, () -> fsb.str("abcdefg", 5));
  }
}