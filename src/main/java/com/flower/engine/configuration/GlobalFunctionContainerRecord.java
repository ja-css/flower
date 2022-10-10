package com.flower.engine.configuration;

import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalFunctionContainerRecord extends ContainerRecord {
  final Class<?> globalFunctionContainerType;
  final GlobalFunctionContainer annotation;
  final String globalFunctionContainerName;

  // TODO: this should be global, unless "globalFunctionContainerName" is used as a namespace
  Map<String, GlobalFunctionRecord> globalFunctions;

  public GlobalFunctionContainerRecord(
      Class<?> globalFunctionContainerType,
      GlobalFunctionContainer annotation,
      String globalFunctionContainerName) {
    this.globalFunctionContainerType = globalFunctionContainerType;
    this.annotation = annotation;
    this.globalFunctionContainerName = globalFunctionContainerName;

    globalFunctions = new HashMap<>();
  }

  public static void validateFunction(Method method) {
    // Validation: Flower function must be static
    if ((method.getModifiers() & Modifier.STATIC) == 0)
      throw new AnnotationFormatError(
          String.format(
              "Flower Global Function must be static: type [%s] method [%s]",
              method.getDeclaringClass(), method.getName()));
  }

  public void initialize() {
    List<Method> globalFunctionList =
        getMethodsAnnotatedWith(globalFunctionContainerType, GlobalFunction.class);
    for (Method globalFunctionMethod : globalFunctionList) {
      validateFunction(globalFunctionMethod);
      GlobalFunction annotation = globalFunctionMethod.getAnnotation(GlobalFunction.class);
      String globalFunctionName = annotation.name();
      if (globalFunctionName.trim().equals("")) globalFunctionName = globalFunctionMethod.getName();
      GlobalFunctionRecord globalFunctionRecord =
          new GlobalFunctionRecord(
              globalFunctionContainerType, globalFunctionMethod, annotation, globalFunctionName);
      globalFunctionRecord.initialize();

      if (globalFunctions.containsKey(globalFunctionName))
        throw new IllegalStateException(
            "Duplicate GlobalFunction name. GlobalFunctionName: ["
                + globalFunctionName
                + "] Container class 1: ["
                + globalFunctions.get(globalFunctionName).globalFunctionContainerType
                + "] Container class 2: ["
                + globalFunctionContainerType
                + "]");
      globalFunctions.put(globalFunctionName, globalFunctionRecord);
    }
  }

  public Collection<GlobalFunctionRecord> getGlobalFunctions() {
    return globalFunctions.values();
  }

  public Class<?> getGlobalFunctionContainerType() {
    return globalFunctionContainerType;
  }
}
