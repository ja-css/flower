package com.flower.engine.runner.parameters.comparison;

import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
import com.google.common.base.Preconditions;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

// TODO: implement polymorphic type comparator - to enable less strict comparison
public class TypeComparator {
  /**
   * Determine whether [type1, type2] is any combination of {option1, option2}
   *
   * @param type1 Type1
   * @param type2 Type2
   * @param option1 Option1
   * @param option2 Option2
   * @return true, IIF [type1, type2] is a combination of {option1, option2}
   */
  private static boolean compareWithOptions(Type type1, Type type2, Type option1, Type option2) {
    return ((type1.equals(option1) || type1.equals(option2))
        && (type2.equals(option1) || type2.equals(option2)));
  }

  /**
   * Determine if "type" is assignable from "from"
   *
   * @param to type
   * @param from type from parameter "type" should be assignable from this type
   * @return true, IFF "type" is assignable from "from"
   */
  public static boolean isTypeAssignable1(Type from, Type to, GenericComparisonContext context) {
    // 1. At least One of {"from, "to"} is a Generic Type (e.g. "T" in "class Cls<T> {}")
    if (from instanceof TypeVariable && to instanceof TypeVariable) {
      // Both from and to are generic.
      return Preconditions.checkNotNull(context)
          .isAssignableVariable((TypeVariable) from, (TypeVariable) to);
    } else if (from instanceof TypeVariable) {
      // from is generic, to is not generic.
      return Preconditions.checkNotNull(context)
          .isAssignableFromVariableToMaterialized((TypeVariable) from, to);
    } else if (to instanceof TypeVariable) {
      // from is not generic, to is generic.
      // This can happen when a generic variable is materialized
      return Preconditions.checkNotNull(context)
          .isAssignableMaterializedToVariable(from, (TypeVariable) to);
    }

    // 2. At least One of {"from, "to"} is a Parameterized Type (e.g. List<String>)
    if (from instanceof ParameterizedType && to instanceof ParameterizedType) {
      // both "from" and "to" are Parameterized
      return ParameterizedTypeComparator.isParameterizedTypeAssignable(
          (ParameterizedType) from, (ParameterizedType) to, context);
    } else if (!(from instanceof ParameterizedType) && to instanceof ParameterizedType) {
      // "from" isn't Parameterized, "to" is Parameterized
      // RawType -> ParameterizedType can't be cast in Flower
      // e.g. raw "List" can't be assigned to "List<String>"
      return false;
    } else if (from instanceof ParameterizedType) {
      // "from" is Parameterized, "to" isn't Parameterized
      // in this case we ignore Type parameters on "from" and compare raw type with "to"
      // i.e. we allow "List<String>" to be assigned to raw "List"
      from = ((ParameterizedType) from).getRawType();
    }

    // 3. Neither of {"from", "to"} is Parameterized or Generic
    // 3.1 Basic types
    if (to.equals(Object.class)) return true;

    if (compareWithOptions(to, from, void.class, Void.class)
        || compareWithOptions(to, from, byte.class, Byte.class)
        || compareWithOptions(to, from, short.class, Short.class)
        || compareWithOptions(to, from, int.class, Integer.class)
        || compareWithOptions(to, from, long.class, Long.class)
        || compareWithOptions(to, from, float.class, Float.class)
        || compareWithOptions(to, from, double.class, Double.class)
        || compareWithOptions(to, from, boolean.class, Boolean.class)
        || compareWithOptions(to, from, char.class, Character.class)) return true;

    // 2. Raw classes
    if (to instanceof Class && from instanceof Class) {
      return ((Class) to).isAssignableFrom((Class) from);
    }

    // 3. Arrays
    if (to instanceof GenericArrayType && from instanceof GenericArrayType) {
      return isTypeAssignable1(
          ((GenericArrayType) from).getGenericComponentType(),
          ((GenericArrayType) to).getGenericComponentType(),
          context);
    }

    // TODO: Comment?
    if (to instanceof Class && from instanceof GenericArrayType) {
      return isTypeAssignable1(
          ((GenericArrayType) from).getGenericComponentType(),
          ((Class) to).getComponentType(),
          context);
    }

    return false;
  }
}
