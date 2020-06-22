package org.example.payment.controller;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.example.payment.util.ObjectUtils.setField;

public class SizeMap {
  private Supplier<Object> supplier;
  private Map<String, Size> sizeMap = new HashMap<>();

  public SizeMap(Supplier<Object> supplier) {
    this.supplier = supplier;
  }

  public SizeMap putInt(String key, Integer min, Integer max) {
    sizeMap.put(key, new IntSize(min, max));
    return this;
  }

  public SizeMap putLong(String key, Long min, Long max) {
    sizeMap.put(key, new LongSize(min, max));
    return this;
  }

  public SizeMap putString(String key, Integer min, Integer max) {
    sizeMap.put(key, new StrSize(min, max));
    return this;
  }

  public List<Object> toList() {
    List<Object> list = new ArrayList<>();

    for (Map.Entry<String, Size> entry: sizeMap.entrySet()) {
      String fieldName = entry.getKey();
      Object ltVal = entry.getValue().getLT();
      Object gtVal = entry.getValue().getGT();
      if (ltVal != null) {
        Object element = supplier.get();
        setField(element, fieldName, ltVal);
        list.add(element);
      }
      if (gtVal != null) {
        Object element = supplier.get();
        setField(element, fieldName, gtVal);
        list.add(element);
      }
    }

    return list;
  }

  // region inner class
  interface Size {
    Object getLT();
    Object getGT();
  }

  static class IntSize implements Size {
    private Integer min;
    private Integer max;
    public IntSize(Integer min, Integer max) {
      if (min != null && min == Integer.MIN_VALUE) {
        throw new IllegalArgumentException("min equals Integer.MIN_VALUE");
      }
      if (max != null && max == Integer.MAX_VALUE) {
        throw new IllegalArgumentException("max equals Integer.MAX_VALUE");
      }
      this.min = min;
      this.max = max;
    }

    @Override
    public Object getLT() {
      return min == null ? null : min - 1;
    }

    @Override
    public Object getGT() {
      return max == null ? null : max + 1;
    }
  }

  static class LongSize implements Size {
    private Long min;
    private Long max;
    public LongSize(Long min, Long max) {
      if (min != null && min == Long.MIN_VALUE) {
        throw new IllegalArgumentException("min equals Long.MIN_VALUE");
      }
      if (max != null && max == Long.MAX_VALUE) {
        throw new IllegalArgumentException("max equals Long.MAX_VALUE");
      }
      this.min = min;
      this.max = max;
    }

    @Override
    public Object getLT() {
      return min == null ? null : min - 1;
    }

    @Override
    public Object getGT() {
      return max == null ? null : max + 1;
    }
  }

  static class StrSize implements Size {
    private Integer min;
    private Integer max;
    public StrSize(Integer min, Integer max) {
      if (min != null && min <= 0) {
        throw new IllegalArgumentException("min must be positive");
      }
      if (max != null && max == Integer.MAX_VALUE) {
        throw new IllegalArgumentException("max equals Integer.MAX_VALUE");
      }
      this.min = min;
      this.max = max;
    }

    @Override
    public Object getLT() {
      return min == null ? null : RandomStringUtils.randomAlphanumeric(min - 1);
    }

    @Override
    public Object getGT() {
      return max == null ? null : RandomStringUtils.randomAlphanumeric(max + 1);
    }
  }
  // endregion
}
