package com.flower.misc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.engine.runner.parameters.comparison.TypeComparator;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TypeComparatorTest {
  public boolean compareAllCombinations(Type t1, Type t2) {
    return (compareCombinations(t1, t2) && TypeComparator.isTypeAssignable1(t1, t2, null));
  }

  public boolean compareCombinations(Type parent, Type child) {
    return (TypeComparator.isTypeAssignable1(parent, parent, null)
        && TypeComparator.isTypeAssignable1(child, parent, null)
        && TypeComparator.isTypeAssignable1(child, child, null));
  }

  @Test
  public void testPrimitiveTypes() {
    assertTrue(compareAllCombinations(byte.class, Byte.class));
    assertTrue(compareAllCombinations(short.class, Short.class));
    assertTrue(compareAllCombinations(int.class, Integer.class));
    assertTrue(compareAllCombinations(long.class, Long.class));
    assertTrue(compareAllCombinations(float.class, Float.class));
    assertTrue(compareAllCombinations(double.class, Double.class));
    assertTrue(compareAllCombinations(boolean.class, Boolean.class));
    assertTrue(compareAllCombinations(char.class, Character.class));
    assertTrue(compareAllCombinations(void.class, Void.class));

    assertFalse(compareAllCombinations(byte.class, short.class));
    assertFalse(compareAllCombinations(byte.class, Short.class));
    assertFalse(compareAllCombinations(Byte.class, short.class));
    assertFalse(compareAllCombinations(Byte.class, Short.class));
  }

  @Test
  public void testInterfacesAndClasses() {
    assertTrue(compareAllCombinations(String.class, String.class)); // simple class
    assertTrue(compareAllCombinations(Iterable.class, Iterable.class)); // simple interface

    assertTrue(compareCombinations(Collection.class, List.class)); // interface inheritance
    assertFalse(compareCombinations(List.class, Collection.class));
    assertTrue(compareCombinations(List.class, AbstractList.class)); // class implements interface
    assertFalse(compareCombinations(AbstractList.class, List.class));
    assertTrue(compareCombinations(AbstractList.class, ArrayList.class)); // class inheritance
    assertFalse(compareCombinations(ArrayList.class, AbstractList.class));
  }

  class Cls1<T> {}

  class Cls2<A, B> extends Cls1<B> {}

  class Cls3<I, J, K> extends Cls2<K, J> {}

  public void dummy(
      String obj1,
      List obj2,
      List<String> obj3,
      List<List<String>> obj4,
      Cls1<String> obj5,
      Cls2<Integer, String> obj6,
      Cls3<Character, String, Integer> obj7,
      Cls1<List<List<String>>> obj8,
      Cls2<Integer, List<List<String>>> obj9,
      Cls3<Character, List<List<String>>, Integer> obj10,
      Cls1<Collection<Collection<String>>> obj11,
      Cls2<Integer, Collection<List<String>>> obj12,
      Cls3<Character, List<List<String>>, Integer> obj13,
      Cls3<Character, List<AbstractList<String>>, Integer> obj14,
      Cls3<Character, AbstractList<AbstractList<String>>, Integer> obj15,
      Cls3<Character, ArrayList<ArrayList<String>>, Integer> obj16,
      Collection<String> obj17,
      List<String> obj18,
      AbstractList<String> obj19,
      ArrayList<String> obj20,
      Collection obj21,
      ArrayList obj22,
      ArrayList<AbstractList> obj23,
      AbstractList<ArrayList> obj24,
      AbstractList<AbstractList> obj25,
      List<AbstractList> obj26,
      AbstractList<List> obj27,
      List<List> obj28) {}

  public void dummy2(String... strs) {}

  public void dummy3(List<String>... strs) {}

  public void dummy4(String[] strs) {}

  public void dummy5(
      List<String>[] strs,
      ArrayList<String>[] strs2,
      AbstractList<List<String>>[] strs3,
      ArrayList<AbstractList<String>>[] strs4,
      List[] strs5,
      ArrayList<AbstractList>[] strs6,
      List<Integer>[] strs7,
      ArrayList<Integer>[] strs8,
      AbstractList<List<Integer>>[] strs9,
      ArrayList<AbstractList<Integer>>[] strs10,
      ArrayList[] strs11) {}

  public void dummy6(List... strs) {}

  @Test
  public void testParameterizedInterfacesAndClasses() {
    Method dummyMethod = null,
        dummyMethod2 = null,
        dummyMethod3 = null,
        dummyMethod4 = null,
        dummyMethod5 = null,
        dummyMethod6 = null;
    for (Method method : this.getClass().getMethods()) {
      switch (method.getName()) {
        case "dummy":
          dummyMethod = method;
          break;
        case "dummy2":
          dummyMethod2 = method;
          break;
        case "dummy3":
          dummyMethod3 = method;
          break;
        case "dummy4":
          dummyMethod4 = method;
          break;
        case "dummy5":
          dummyMethod5 = method;
          break;
        case "dummy6":
          dummyMethod6 = method;
          break;
      }
    }

    Type[] genericParameterTypes = dummyMethod.getGenericParameterTypes();

    Type string = genericParameterTypes[0];
    Type list = genericParameterTypes[1];
    Type list_string = genericParameterTypes[2];
    Type list_listOfString = genericParameterTypes[3];

    Type cls1_string = genericParameterTypes[4];
    Type cls2_integer_string = genericParameterTypes[5];
    Type cls3_character_integer_string = genericParameterTypes[6];

    Type cls1_listOfListOfString = genericParameterTypes[7];
    Type cls2_integer_listOfListOfString = genericParameterTypes[8];
    Type cls3_character_listOfListOfString_integer = genericParameterTypes[9];

    Type cls1 = genericParameterTypes[10];
    Type cls2 = genericParameterTypes[11];
    Type cls3 = genericParameterTypes[12];
    Type cls4 = genericParameterTypes[13];
    Type cls5 = genericParameterTypes[14];
    Type cls6 = genericParameterTypes[15];

    Type collection_string = genericParameterTypes[16];
    Type list_string2 = genericParameterTypes[17];

    Type abList_string = genericParameterTypes[18];
    Type arList_string = genericParameterTypes[19];

    Type collection = genericParameterTypes[20];
    Type arList = genericParameterTypes[21];

    Type arListAbstractList = genericParameterTypes[22];
    Type abstractListArList = genericParameterTypes[23];

    Type abstractListAbstractList = genericParameterTypes[24];
    Type listAbstractList = genericParameterTypes[25];
    Type abstractListList = genericParameterTypes[26];
    Type listList = genericParameterTypes[27];

    assertTrue(compareCombinations(list, list_string));
    assertTrue(compareCombinations(list, list_listOfString));

    assertTrue(compareCombinations(cls1_string, cls2_integer_string));
    assertTrue(compareCombinations(cls1_string, cls3_character_integer_string));
    assertTrue(compareCombinations(cls2_integer_string, cls3_character_integer_string));

    assertTrue(compareCombinations(cls1_listOfListOfString, cls2_integer_listOfListOfString));
    assertTrue(
        compareCombinations(cls1_listOfListOfString, cls3_character_listOfListOfString_integer));
    assertTrue(
        compareCombinations(
            cls2_integer_listOfListOfString, cls3_character_listOfListOfString_integer));

    assertTrue(compareCombinations(cls1, cls2));
    assertTrue(compareCombinations(cls1, cls3));
    assertTrue(compareCombinations(cls1, cls4));
    assertTrue(compareCombinations(cls1, cls5));
    assertTrue(compareCombinations(cls1, cls6));

    assertTrue(compareCombinations(cls2, cls3));
    assertTrue(compareCombinations(cls2, cls4));
    assertTrue(compareCombinations(cls2, cls5));
    assertTrue(compareCombinations(cls2, cls6));

    assertTrue(compareCombinations(cls3, cls4));
    assertTrue(compareCombinations(cls3, cls5));
    assertTrue(compareCombinations(cls3, cls6));

    assertTrue(compareCombinations(cls4, cls5));
    assertTrue(compareCombinations(cls4, cls6));

    assertTrue(compareCombinations(cls5, cls6));

    assertTrue(compareCombinations(collection_string, list_string2));

    assertTrue(compareCombinations(abList_string, arList_string));

    assertTrue(compareCombinations(collection, list));
    assertTrue(compareCombinations(collection, collection_string));
    assertTrue(compareCombinations(list, list_string));

    assertTrue(compareCombinations(collection, arList));
    assertTrue(compareCombinations(list, arList));
    assertTrue(compareCombinations(arList, arList_string));

    assertTrue(compareCombinations(list, abstractListArList));

    assertTrue(compareCombinations(abstractListAbstractList, abstractListArList));
    assertTrue(compareCombinations(abstractListAbstractList, arListAbstractList));

    assertTrue(compareCombinations(listAbstractList, abstractListAbstractList));
    assertTrue(compareCombinations(listAbstractList, abstractListArList));
    assertTrue(compareCombinations(listAbstractList, arListAbstractList));

    assertTrue(compareCombinations(abstractListList, abstractListAbstractList));
    assertTrue(compareCombinations(abstractListList, abstractListArList));
    assertTrue(compareCombinations(abstractListList, arListAbstractList));

    assertTrue(compareCombinations(listList, listAbstractList));
    assertTrue(compareCombinations(listList, abstractListList));
    assertTrue(compareCombinations(listList, abstractListAbstractList));
    assertTrue(compareCombinations(listList, abstractListArList));
    assertTrue(compareCombinations(listList, arListAbstractList));

    assertTrue(compareCombinations(listList, list_listOfString));

    //

    assertFalse(compareCombinations(list_string2, collection_string));

    assertFalse(compareCombinations(arList_string, abList_string));

    assertFalse(compareCombinations(list, collection));
    assertFalse(compareCombinations(collection_string, collection));
    assertFalse(compareCombinations(list_string, list));

    assertFalse(compareCombinations(arList, collection));
    assertFalse(compareCombinations(arList, list));
    assertFalse(compareCombinations(arList_string, arList));

    assertFalse(compareCombinations(cls2, cls1));
    assertFalse(compareCombinations(cls3, cls1));
    assertFalse(compareCombinations(cls4, cls1));
    assertFalse(compareCombinations(cls5, cls1));
    assertFalse(compareCombinations(cls6, cls1));

    assertFalse(compareCombinations(cls3, cls2));
    assertFalse(compareCombinations(cls4, cls2));
    assertFalse(compareCombinations(cls5, cls2));
    assertFalse(compareCombinations(cls6, cls2));

    assertFalse(compareCombinations(cls4, cls3));
    assertFalse(compareCombinations(cls5, cls3));
    assertFalse(compareCombinations(cls6, cls3));

    assertFalse(compareCombinations(cls5, cls4));
    assertFalse(compareCombinations(cls6, cls4));

    assertFalse(compareCombinations(cls6, cls5));

    assertFalse(compareCombinations(list_string, list));
    assertFalse(compareCombinations(list_listOfString, list));

    assertFalse(compareCombinations(cls2_integer_string, cls1_string));
    assertFalse(compareCombinations(cls3_character_integer_string, cls1_string));
    assertFalse(compareCombinations(cls3_character_integer_string, cls2_integer_string));

    assertFalse(compareCombinations(cls2_integer_listOfListOfString, cls1_listOfListOfString));
    assertFalse(
        compareCombinations(cls3_character_listOfListOfString_integer, cls1_listOfListOfString));
    assertFalse(
        compareCombinations(
            cls3_character_listOfListOfString_integer, cls2_integer_listOfListOfString));

    // ---------------------

    Type stringDots = dummyMethod2.getGenericParameterTypes()[0];
    Type listOfStringDots = dummyMethod3.getGenericParameterTypes()[0];
    Type listDots = dummyMethod6.getGenericParameterTypes()[0];
    Type stringArray = dummyMethod4.getGenericParameterTypes()[0];

    Type[] genericParameterTypes5 = dummyMethod5.getGenericParameterTypes();

    Type listOfStringArray = genericParameterTypes5[0];
    Type arrayListOfStringArray = genericParameterTypes5[1];
    Type abstractListOfListOfStringArray = genericParameterTypes5[2];
    Type arrayListOfAbstractListOfStringArray = genericParameterTypes5[3];
    Type listArray = genericParameterTypes5[4];
    Type arrayListOfAbstractListArray = genericParameterTypes5[5];

    Type listOfIntArray = genericParameterTypes5[6];
    Type arrayListOfIntArray = genericParameterTypes5[7];
    Type abstractListOfListOfIntArray = genericParameterTypes5[8];
    Type arrayListOfAbstractListOfIntArray = genericParameterTypes5[9];
    Type arrayListArray = genericParameterTypes5[10];

    assertTrue(compareCombinations(stringDots, stringArray));
    assertTrue(compareCombinations(listOfStringDots, listOfStringArray));
    assertTrue(compareCombinations(listDots, listOfStringDots));
    assertTrue(compareCombinations(listDots, listOfStringArray));

    assertTrue(compareCombinations(listArray, arrayListOfAbstractListArray));
    assertTrue(compareCombinations(listArray, arrayListArray));
    assertTrue(compareCombinations(listOfStringArray, arrayListOfStringArray));
    assertTrue(
        compareCombinations(abstractListOfListOfStringArray, arrayListOfAbstractListOfStringArray));

    assertTrue(compareCombinations(listOfIntArray, arrayListOfIntArray));
    assertTrue(
        compareCombinations(abstractListOfListOfIntArray, arrayListOfAbstractListOfIntArray));
  }
}
