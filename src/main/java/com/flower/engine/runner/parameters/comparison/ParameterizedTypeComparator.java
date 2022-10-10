package com.flower.engine.runner.parameters.comparison;

import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/*
    The context of comments is as follows:
    Let's say we have two classes:
        class Cls1<T> { }
        class Cls2<A, B> extends Cls1<B> { }

    the point is to determine that Cls2<Integer, String> is assignable to Cls1<String>, but not to Cls1<Integer>, and likewise for all points in type hierarchy.

    Map<K, V>                - we refer to [K, V] as to {generic argument declared name}(s).
    HashMap<String, Integer> - we refer to [String, Integer] as to {generic argument declared type}(s).
*/
public class ParameterizedTypeComparator {
  /**
   * Remap subtype's {generic argument declared type}s to its supertype
   *
   * @param genericSupertype Supertype
   * @param typeArgumentsMap Subtype's {generic argument declared type}s indexed by {generic
   *     argument declared name}s
   * @return Supertype with remapped {generic argument declared type}s from its subtype
   */
  private static GenericTypeInfo mapParameters(
      ParameterizedType genericSupertype, Map<String, Type> typeArgumentsMap) {
    // for superclass Cls1<T> (T <- B) of class Cls2<A, B> will return [B],
    // i.e. {generic argument declared name}s for corresponding arguments of subclass Cls2
    Type[] superclassGenericParameters = genericSupertype.getActualTypeArguments();

    Type[] mappedGenericArguments = new Type[superclassGenericParameters.length];
    for (int i = 0; i < superclassGenericParameters.length; i++) {
      // Here we get typeArgumentsMap[B] -> String
      // i.e. for Cls2 we get {generic argument declared type} by its {generic argument declared
      // name}
      String genericParameterName = ((TypeVariable) superclassGenericParameters[i]).getName();
      mappedGenericArguments[i] = typeArgumentsMap.get(genericParameterName);
    }

    return new GenericTypeInfo(genericSupertype.getRawType(), mappedGenericArguments);
  }

  /**
   * Get List of supertypes with properly remapped {generic argument declared type}s for a given
   * type
   *
   * @param type Type with mapped {generic argument declared type}s
   * @return List of supertypes with mapped {generic argument declared type}s
   */
  private static List<GenericTypeInfo> mapGenericSuperTypes(GenericTypeInfo type) {
    List<GenericTypeInfo> supertypes = new ArrayList<>();

    Type rawType = type.rawType;

    // for declared Cls2<Integer, String> will return [Integer, String]
    // i.e. {generic argument declared type}s
    Type[] typeArguments = type.typeArguments;

    // for type Cls2<A, B> will return [A, B]
    // i.e. {generic argument declared name}s
    TypeVariable[] genericArguments = ((Class) rawType).getTypeParameters();
    Map<String, Type> typeArgumentsMap = new HashMap<>();
    for (int i = 0; i < genericArguments.length; i++) {
      typeArgumentsMap.put(genericArguments[i].getName(), typeArguments[i]);
    }

    Type rawGenericSuperclass = ((Class) rawType).getGenericSuperclass();
    // If superclass is not generic/parameterized it's out of context of comparing generic types
    if (rawGenericSuperclass instanceof ParameterizedType) {
      supertypes.add(mapParameters((ParameterizedType) rawGenericSuperclass, typeArgumentsMap));
    }

    Type[] rawGenericInterfaces = ((Class) rawType).getGenericInterfaces();
    for (Type rawGenericInterface : rawGenericInterfaces) {
      // If super-interface is not generic/parameterized it's out of context of comparing generic
      // types
      if (rawGenericInterface instanceof ParameterizedType) {
        supertypes.add(mapParameters((ParameterizedType) rawGenericInterface, typeArgumentsMap));
      }
    }

    return supertypes;
  }

  /**
   * Get supertypes with properly remapped {generic argument declared type}s for all given types
   *
   * @param types Types with mapped {generic argument declared type}s
   * @return List of all supertypes with mapped {generic argument declared type}s
   */
  private static List<GenericTypeInfo> mapGenericSuperTypes(List<GenericTypeInfo> types) {
    List<GenericTypeInfo> supertypes = new ArrayList<>();

    for (GenericTypeInfo type : types) {
      supertypes.addAll(mapGenericSuperTypes(type));
    }

    return supertypes;
  }

  /**
   * Find a matching GenericTypeInfo from a list
   *
   * @param rawType Type to match
   * @param types List of GenericTypeInfo-s
   * @return Matching Type Info or null if not found
   */
  @Nullable
  private static GenericTypeInfo getMatchingType(Type rawType, List<GenericTypeInfo> types) {
    for (GenericTypeInfo candidate : types) {
      if (candidate.rawType.equals(rawType)) {
        return candidate;
      }
    }
    return null;
  }

  /**
   * Determine whether "type" is assignable from "from"
   *
   * @param to type
   * @param from type from parameter "type" should be assignable from this type
   * @return true, IFF "type" has same or derived type as "from", recursively for all generic types
   *     involved
   */
  public static boolean isParameterizedTypeAssignable(
      ParameterizedType from, ParameterizedType to, GenericComparisonContext context) {
    GenericTypeInfo toInfo = new GenericTypeInfo(to.getRawType(), to.getActualTypeArguments());
    GenericTypeInfo fromInfo =
        new GenericTypeInfo(from.getRawType(), from.getActualTypeArguments());

    // Raw types should be assignable
    if (!TypeComparator.isTypeAssignable1(fromInfo.rawType, toInfo.rawType, context)) return false;

    // Find type matching "to"'s raw type in "from" type hierarchy - i.e. "to" should be in that
    // hierarchy
    List<GenericTypeInfo> types = ImmutableList.of(fromInfo);

    GenericTypeInfo matchingType = null;
    while (matchingType == null && !types.isEmpty()) {
      matchingType = getMatchingType(toInfo.rawType, types);
      if (matchingType == null) {
        types = mapGenericSuperTypes(types);
      }
    }

    // if there is no matching type found, we can't assign
    if (matchingType == null) return false;

    // compare "from" and "to" type parameters, those should be assignable
    if (toInfo.typeArguments.length != matchingType.typeArguments.length) {
      return false;
    }

    for (int i = 0; i < toInfo.typeArguments.length; i++) {
      if (!TypeComparator.isTypeAssignable1(
          matchingType.typeArguments[i], toInfo.typeArguments[i], context)) return false;
    }

    return true;
  }
}
