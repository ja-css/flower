package com.flower.engine.function;

import com.flower.utilities.FlowerException;

import java.lang.reflect.Field;

public class StateField {
  public final Class<?> stateFieldClass;
  public final Field field;
  public final boolean isFinal;

  public StateField(Class<?> stateFieldClass, Field field, boolean isFinal) {
    this.stateFieldClass = stateFieldClass;
    this.field = field;
    this.field.setAccessible(true);
    this.isFinal = isFinal;
  }

  public Object getFieldValue(Object flowState) {
    try {
      return field.get(flowState);
    } catch (IllegalAccessException e) {
      throw new FlowerException(e);
    }
  }

  public void updateFieldValue(Object flowState, Object newValue) {
    try {
      field.set(flowState, newValue);
    } catch (IllegalAccessException e) {
      throw new FlowerException(e);
    }
  }
}
