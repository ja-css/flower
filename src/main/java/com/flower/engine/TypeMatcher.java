package com.flower.engine;

public class TypeMatcher {
  /*    public static WerkParameterType matchType(Type type) {
      //TODO: add support
      //STEP,
      //FUNCTION;

      if (type.equals(int.class) || type.equals(Integer.class) || type.equals(long.class) || type.equals(Long.class)) {
          return WerkParameterType.LONG;
      } else if (type.equals(char.class) || type.equals(Character.class)) {
          return WerkParameterType.CHAR;
      } else if (type.equals(double.class) || type.equals(Double.class)) {
          return WerkParameterType.DOUBLE;
      } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
          return WerkParameterType.BOOL;
      } else if (type.equals(String.class)) {
          return WerkParameterType.STRING;
      } else if (type.equals(byte[].class)) {
          return WerkParameterType.BYTES;
      } else {
          //TODO: ???MAYBE implement a separate method - smth. like: checkList / Map Serializeability()???
          //TODO: make sure that if Parameter is denoted as LIST or MAP it holds serializeable types only
          //TODO: this is used somewhere to match List to List, correct that usage at the moment of feature update
          if (type.equals(List.class))
              return WerkParameterType.LIST;
          if (type.equals(Map.class))
              return WerkParameterType.MAP;

          Class<?>[] interfaces = null;
          if (type instanceof ParameterizedType) {
              Type rawType = ((ParameterizedType)type).getRawType();
              if (rawType instanceof Class) {
                  if (rawType.equals(List.class))
                      return WerkParameterType.LIST;
                  if (rawType.equals(Map.class))
                      return WerkParameterType.MAP;

                  interfaces = ((Class<?>)rawType).getInterfaces();
              }
          }
          if (type instanceof Class) {
              interfaces = ((Class<?>)type).getInterfaces();
          }
          if (interfaces != null)
              for (Class<?> intrf : interfaces) {
                  if (intrf.equals(List.class))
                      return WerkParameterType.LIST;
                  if (intrf.equals(Map.class))
                      return WerkParameterType.MAP;
              }
      }

      return WerkParameterType.RUNTIME;
  }*/
}
