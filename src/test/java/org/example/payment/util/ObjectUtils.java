package org.example.payment.util;

import java.lang.reflect.Field;

public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {
  public static void setField(Object item, String fieldName, Object value) {
    try {
      Field declaredField = item.getClass().getDeclaredField(fieldName);
      declaredField.setAccessible(true);
      declaredField.set(item, value);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
}
