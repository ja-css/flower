package com.flower.engine.runner.parameters.comparison.context;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

public interface GenericComparisonContext {
  /** "from" IS a TypeVariable, "to" IS a TypeVariable */
  boolean isAssignableVariable(TypeVariable from, TypeVariable to);

  /** "from" IS a TypeVariable, "to" is NOT a TypeVariable */
  boolean isAssignableFromVariableToMaterialized(TypeVariable from, Type to);

  /** "from" is NOT a TypeVariable, "to" IS a TypeVariable */
  boolean isAssignableMaterializedToVariable(Type from, TypeVariable to);

  @Nullable
  GlobalFunctionAssumedType getAssumedType();
}
