package com.flower.engine.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ContainerRecord {
  List<Method> getMethodsAnnotatedWith(
      final Class<?> type, final Class<? extends Annotation> annotation) {
    final List<Method> methods = new ArrayList<>();
    for (final Method method : type.getDeclaredMethods()) {
      if (method.isAnnotationPresent(annotation)) {
        methods.add(method);
      }
    }
    return methods;
  }
}
