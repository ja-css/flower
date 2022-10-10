package com.flower.engine.configuration;

import com.flower.anno.flow.State;

import java.lang.reflect.Field;

public class StateFieldRecord {
  public Class<?> stateFieldClass;
  public final Field stateField;
  final State annotation;
  public final String stateFieldName;
  public final boolean isFinal;

  public StateFieldRecord(
      Class<?> stateFieldClass,
      Field stateField,
      State annotation,
      String stateFieldName,
      boolean isFinal) {
    this.stateFieldClass = stateFieldClass;
    this.stateField = stateField;
    this.annotation = annotation;
    this.stateFieldName = stateFieldName;
    this.isFinal = isFinal;
  }
}
