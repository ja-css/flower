package com.flower.engine.configuration;

import com.flower.anno.flow.State;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class StateFieldRecord {
  public Class<?> stateFieldClass;
  public final Field stateField;
  final State annotation;
  public final String stateFieldName;
  public final boolean isFinal;
  public final boolean isNullable;

  public StateFieldRecord(
      Class<?> stateFieldClass,
      Field stateField,
      State annotation,
      String stateFieldName,
      boolean isFinal,
      boolean isNullable) {
    this.stateFieldClass = stateFieldClass;
    this.stateField = stateField;
    this.annotation = annotation;
    this.stateFieldName = stateFieldName;
    this.isFinal = isFinal;
    this.isNullable = isNullable;
  }
}
