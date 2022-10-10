package com.flower.engine.runner.step;

import java.util.Objects;

public class ExpectedInitializedField {
  public final String fieldName;
  public final String functionName;

  public ExpectedInitializedField(String fieldName, String functionName) {
    this.fieldName = fieldName;
    this.functionName = functionName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ExpectedInitializedField)) return false;
    ExpectedInitializedField that = (ExpectedInitializedField) o;
    return fieldName.equals(that.fieldName) && functionName.equals(that.functionName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, functionName);
  }
}
