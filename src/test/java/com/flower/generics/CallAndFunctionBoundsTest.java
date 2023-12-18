package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

public class CallAndFunctionBoundsTest {
  @Test
  void test_GenericMissingFlow() {
    Flower flower = new Flower();
    flower.registerFlow(GenericMissingFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage().contains("Flower Function generics must have corresponding Flow generics"));
  }

  @Test
  void test_GenericMatchBasicFlow() {
    Flower flower = new Flower();
    flower.registerFlow(GenericMatchBasicFlow.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow1() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow1.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow2() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow2.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow3() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow3.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMismatchFlow() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow2() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow3() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow4() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow4.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow5() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow5.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow6() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow6.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow7() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow7.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow8() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow8.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchChildFlow() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow3.class);
    flower.registerFlow(GenericBoundMismatchChildFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMatchChildFlow() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow3.class);
    flower.registerFlow(GenericBoundMatchChildFlow.class);

    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow4() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow4.class);

    flower.initialize();
  }

  @Test
  void test_GenericBoundMismatchFlow9() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow9.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMatchFlow5() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow5.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow6() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow5.class);
    flower.registerFlow(GenericBoundMatchFlow6.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow7() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow7.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMatchFlow8() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow7.class);
    flower.registerFlow(GenericBoundMatchFlow8.class);
    flower.initialize();
  }

  @Test
  void test_GenericBoundMismatchFlow10() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow10.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow11() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow11.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow12() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow12.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow13() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow13.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow14() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMismatchFlow14.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow15() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow5.class);
    flower.registerFlow(GenericBoundMismatchFlow15.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }

  @Test
  void test_GenericBoundMismatchFlow16() {
    Flower flower = new Flower();
    flower.registerFlow(GenericBoundMatchFlow7.class);
    flower.registerFlow(GenericBoundMismatchFlow16.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(
        e.getMessage()
            .contains(
                "Flower Function generic bounds must equal to corresponding Flow generic bounds"));
  }
}

@FlowType(firstStep = "STEP")
class GenericMissingFlow<C> {
  @State C in;

  @SimpleStepFunction
  static <Z> Transition STEP(@In Z in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericMatchBasicFlow<C> {
  @State C in;

  @SimpleStepFunction
  static <C> Transition STEP(@Nullable @In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow<C extends String> {
  @State C in;

  @SimpleStepFunction
  static <C> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow2<C> {
  @State C in;

  @SimpleStepFunction
  static <C extends String> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMatchFlow1<C extends String> {
  @State C in;

  @SimpleStepFunction
  static <C extends String> Transition STEP(@Nullable @In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow3<C extends String & List> {
  @State C in;

  @SimpleStepFunction
  static <C extends String> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow4<C extends String & List> {
  @State C in;

  @SimpleStepFunction
  static <C extends List> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMatchFlow2<C extends String & List> {
  @State C in;

  @SimpleStepFunction
  static <C extends String & List> Transition STEP(@Nullable @In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow5<C extends List<String>> {
  @State
  C in;

  @SimpleStepFunction
  static <C extends List> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow6<C extends List> {
  @State C in;

  @SimpleStepFunction
  static <C extends List<String>> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow7<C extends String & List<String>> {
  @State C in;

  @SimpleStepFunction
  static <C extends String & List> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow8<C extends String & List> {
  @State C in;

  @SimpleStepFunction
  static <C extends String & List<String>> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "FLOW3")
class GenericBoundMatchFlow3<C extends List<String>> {
  @State C in;

  @SimpleStepFunction
  static <C extends List<String>> Transition STEP(@Nullable @In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow3.class)
class GenericBoundMismatchChildFlow<C extends ArrayList<String>> extends GenericBoundMatchFlow3<C> {
  @State C in;

  @SimpleStepFunction
  static <C extends List<String>> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow3.class)
class GenericBoundMatchChildFlow<C2 extends ArrayList<String>> extends GenericBoundMatchFlow3<C2> {
  @State C2 in;

  @SimpleStepFunction
  static <C2 extends ArrayList<String>> Transition STEP(@Nullable @In C2 in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMismatchFlow9<C, Z extends String> {
  @State C in;
  @State Z in2;

  @SimpleStepFunction
  static <C extends String> Transition STEP(@In C in) {
    return null;
  }
}

@FlowType(firstStep = "STEP")
class GenericBoundMatchFlow4<C extends List<String>, Z extends String> {
  @State C in;
  @State Z in2;

  @SimpleStepFunction
  static <C extends List<String>> Transition STEP(@Nullable @In C in) {
    return null;
  }

  @SimpleStepFunction
  static <Z extends String> Transition STEP2(@Nullable @In Z in2) {
    return null;
  }

  @SimpleStepFunction
  static <C extends List<String>, Z extends String> Transition STEP3(
      @Nullable @In C in, @Nullable @In Z in2) {
    return null;
  }
}

// -----------------------

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMatchFlow5<C extends X, X> {
  @State C c;
  @State X x;

  @SimpleStepFunction
  static <C extends X, X> Transition STEP(@Nullable @In C c) {
    return null;
  }

  @SimpleStepFunction
  static <X> Transition STEP2(@Nullable @In X x) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow5.class)
class GenericBoundMatchFlow6<C2 extends X2, X2 extends String>
    extends GenericBoundMatchFlow5<C2, X2> {
  @SimpleStepFunction
  static <C2 extends X2, X2 extends String> Transition STEP3(@Nullable @In C2 c) {
    return null;
  }

  @SimpleStepFunction
  static <X2 extends String> Transition STEP4(@Nullable @In X2 x) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMatchFlow7<C extends List<X>, X> {
  @State C c;
  @State X x;

  @SimpleStepFunction
  static <C extends List<X>, X> Transition STEP(@Nullable @In C c) {
    return null;
  }

  @SimpleStepFunction
  static <X> Transition STEP2(@Nullable @In X x) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow7.class)
class GenericBoundMatchFlow8<C2 extends List<X2>, X2 extends String>
    extends GenericBoundMatchFlow7<C2, X2> {
  @SimpleStepFunction
  static <C2 extends List<X2>, X2 extends String> Transition STEP3(@Nullable @In C2 c) {
    return null;
  }

  @SimpleStepFunction
  static <X2 extends String> Transition STEP4(@Nullable @In X2 x) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMismatchFlow10<C extends List<X>, X> {
  @State C c;

  @SimpleStepFunction
  static <C extends X, X> Transition STEP(@In C c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMismatchFlow11<C extends List<X>, X, Y> {
  @State C c;

  @SimpleStepFunction
  static <C extends Y, Y> Transition STEP(@In C c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMismatchFlow12<C extends List<X>, X, Y> {
  @State C c;

  @SimpleStepFunction
  static <C extends List<Y>, Y> Transition STEP(@In C c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMismatchFlow13<C extends X, X, Y> {
  @State C c;

  @SimpleStepFunction
  static <C extends Y, Y> Transition STEP(@In C c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", name = "BASE")
class GenericBoundMismatchFlow14<C extends X, X extends String> {
  @State C c;

  @SimpleStepFunction
  static <C extends X, X> Transition STEP(@In C c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow5.class)
class GenericBoundMismatchFlow15<C2 extends X2, X2 extends String>
    extends GenericBoundMatchFlow5<C2, X2> {
  @SimpleStepFunction
  static <C2 extends String> Transition STEP3(@In C2 c) {
    return null;
  }
}

@FlowType(firstStep = "STEP", extendz = GenericBoundMatchFlow7.class)
class GenericBoundMismatchFlow16<C2 extends List<X2>, X2 extends String>
    extends GenericBoundMatchFlow7<C2, X2> {
  @SimpleStepFunction
  static <C2 extends List<String>> Transition STEP3(@In C2 c) {
    return null;
  }
}
