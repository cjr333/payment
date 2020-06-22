package org.example.payment.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueIdGeneratorTest {

  @Test
  void successGenerate() {
    String uniqueId = UniqueIdGenerator.generate(20);
    assertEquals(uniqueId.length(), 20);
    assertTrue(StringUtils.isAlphanumeric(uniqueId));

    String uniqueId2 = UniqueIdGenerator.generate(20);
    assertNotEquals(uniqueId, uniqueId2);

    String uniqueId3 = UniqueIdGenerator.generate(20);
    assertNotEquals(uniqueId, uniqueId3);
    assertNotEquals(uniqueId2, uniqueId3);
  }

  @Test
  void failGenerate() {
    assertThrows(IllegalArgumentException.class, () -> UniqueIdGenerator.generate(9));
  }
}