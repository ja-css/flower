package com.flower.engine.runner.state;

import com.flower.engine.function.StateField;
import com.google.common.base.Preconditions;
import java.util.Map;
import javax.annotation.Nullable;

public class StateAccessConfig {
  public final Map<String, StateField> stateFieldMap;

  public StateAccessConfig(Map<String, StateField> stateFieldMap) {
    this.stateFieldMap = stateFieldMap;
  }

  @Nullable
  public StateField getFieldRecord(String fieldName) {
    return stateFieldMap.get(fieldName);
  }

  public Object getField(Object flowState, String fieldName) {
    return Preconditions.checkNotNull(stateFieldMap.get(fieldName)).getFieldValue(flowState);
  }

  public boolean hasField(String fieldName) {
    return stateFieldMap.containsKey(fieldName);
  }

  public void updateField(Object flowState, String fieldName, Object parameterValue) {
    Preconditions.checkNotNull(stateFieldMap.get(fieldName))
        .updateFieldValue(flowState, parameterValue);
  }
}
