package com.flower.generics;

import com.flower.conf.OutPrm;
import com.flower.engine.function.FlowerOutPrm;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class GenerTest {
  @Test
  void test() {
    System.out.println("sdsdsd");

    TypeVariable<Class<ChildFlow3>>[] child3 = ChildFlow3.class.getTypeParameters();
    TypeVariable<Class<ChildFlow2>>[] child2 = ChildFlow2.class.getTypeParameters();
    TypeVariable<Class<ChildFlow>>[] child = ChildFlow.class.getTypeParameters();
    TypeVariable<Class<ParentFlow>>[] parent = ParentFlow.class.getTypeParameters();
    TypeVariable<Class<ParentFlow0>>[] parent0 = ParentFlow0.class.getTypeParameters();

    Type child3Sc = ChildFlow3.class.getGenericSuperclass();
    Type child2Sc = ChildFlow2.class.getGenericSuperclass();
    Type childSc = ChildFlow.class.getGenericSuperclass();
    Type parentSc = ParentFlow.class.getGenericSuperclass();
    Type parent0Sc = ParentFlow0.class.getGenericSuperclass();

    System.out.println("sdsdsd2");
  }
}

// TODO: check that generic parameter of generic step and transit functions match corresponding
// generic Flow parameters
// TODO: check that @In, @Out, @InOut, @InFromFlow, @OutFromFlow, @InOutFromFlow are compatible

class ChildFlow3 extends ChildFlow2<ArrayList<String>> {}

class ChildFlow2<Y extends List<String>> extends ChildFlow<String, Y> {}

class ChildFlow<Z extends String, W extends List<Z>> extends ParentFlow<W> {}

class ParentFlow<C> extends ParentFlow0<C> {}

class ParentFlow0<U> {}

class P {
  void doo(OutPrm<String> out, Supplier<String> outV) {
    out.setOutValue(outV.get());
  }
}

/*//This doesn't work
class P1<C extends String> extends P {
    public void action() { action(() -> "hello"); }
    public void action(Supplier<C> sp) { this.doo(new FlowerOutPrm<C>(), sp); }
}*/

// This works - NO GENERIC
class P2 extends P {
  public void action() {
    action(() -> "hello");
  }

  public void action(Supplier<String> sp) {
    this.doo(new FlowerOutPrm<>(), sp);
  }
}

// --------------------------------------

class Q<C> {
  void doo(OutPrm<C> out, Supplier<C> outV) {
    out.setOutValue(outV.get());
  }
}

/*//This doesn't work
class Q1<C extends String> extends Q<C> {
    public void action() { action(() -> "hello"); }
    public void action(Supplier<C> sp) { this.doo(new FlowerOutPrm<C>(), sp); }
}*/

// This works - GENERIC TRANSLATES
class Q2 extends Q<String> {
  public void action() {
    action(() -> "hello");
  }

  public void action(Supplier<String> sp) {
    this.doo(new FlowerOutPrm<>(), sp);
  }
}

class ttt<T extends X, X extends String> {
  void m0(String str) {}

  void m1(X x) {
    m0(x);
  }

  void m2(T t) {
    m1(t);
  }

  void m3(T t) {
    m0(t);
  }
}

// this causes error
/*class ttt2<T1, X2 extends String> extends ttt<T1, X2> {
    //
}*/

// this causes error
/*class ttt2<T1 extends X2, X2> extends ttt<T1, X2> {
    //
}*/

class ttt2<T2 extends X2, X2 extends String> extends ttt<T2, X2> {
  void m1_1(X2 x) {
    m0(x);
  }

  void m1_2(X2 x) {
    m1(x);
  }

  void m2_1(T2 t) {
    m1(t);
  }

  void m3(T2 t) {
    m0(t);
  }

  void m4_1(T2 t) {
    m1_1(t);
  }

  void m4_2(T2 t) {
    m2(t);
  }
}

interface gg {
  String str();
}

class gg1 implements gg {
  @Override
  public String str() {
    return "gg1";
  }
}

class gg2 implements gg {
  @Override
  public String str() {
    return "gg2";
  }
}

class ttt3 {
  void action() {
    str(new gg1(), new gg2());
  }

  <T extends gg> void str(T str1, T str2) {}
}

class ttt4 {
  void action() {
    OutPrm<gg2> out = new FlowerOutPrm<>();
    Supplier<gg2> supp = gg2::new;

    str2(out, supp);
  }

  <T extends gg> void str2(OutPrm<T> str, Supplier<T> supp) {}
}
