package com.flower.nullaway;

import com.flower.anno.event.EventCall;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.engine.Flower;
import javax.annotation.Nullable;

import com.flower.anno.event.EventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventProfileNullAwayCallTest {
  @Test
  public void test1() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_1.class);
    flower.registerEventProfile(ENAC_TestEventProfile1.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, () -> flower.initialize());
    Assertions.assertTrue(e.getMessage().contains("Make parameter @Nullable or initialize"));
  }

  @Test
  public void test2() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_2.class);
    flower.registerEventProfile(ENAC_TestEventProfile2.class);
    flower.initialize();
  }

  @Test
  public void test3() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_3.class);
    flower.registerEventProfile(ENAC_TestEventProfile3.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, () -> flower.initialize());
    Assertions.assertTrue(
        e.getMessage()
            .contains("Change parameter type to '@InOut NullableInOutPrm prm' or initialize"));
  }

  @Test
  public void test4() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_4.class);
    flower.registerEventProfile(ENAC_TestEventProfile4.class);
    flower.initialize();
  }

  @Test
  public void test5() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_5.class);
    flower.registerEventProfile(ENAC_TestEventProfile5.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, () -> flower.initialize());
    Assertions.assertTrue(e.getMessage().contains("Make parameter @Nullable or initialize"));
  }

  @Test
  public void test6() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_6.class);
    flower.registerEventProfile(ENAC_TestEventProfile6.class);
    flower.initialize();
  }

  @Test
  public void test7() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_7.class);
    flower.registerEventProfile(ENAC_TestEventProfile7.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, () -> flower.initialize());
    Assertions.assertTrue(
        e.getMessage()
            .contains("Change parameter type to '@InOut NullableInOutPrm prm' or initialize"));
  }

  @Test
  public void test8() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_8.class);
    flower.registerEventProfile(ENAC_TestEventProfile8.class);
    flower.initialize();
  }

  @Test
  public void test9() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_9.class);
    flower.registerEventProfile(ENAC_TestEventProfile9.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, () -> flower.initialize());
    Assertions.assertTrue(e.getMessage().contains("Make parameter @Nullable or initialize"));
  }

  @Test
  public void test10() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer_10.class);
    flower.registerEventProfile(ENAC_TestEventProfile10.class);
    flower.initialize();
  }

  @Test
  public void test() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(ENAC_GlobalFunctionContainer.class);
    flower.registerEventProfile(ENAC_TestEventProfile.class);
    flower.initialize();
  }

  @Test
  public void testBIG() {
    Flower flower = new Flower();
    flower.registerEventProfile(ENAC_TestEventProfileBIG.class);
    flower.initialize();
  }
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_1 {
  @GlobalFunction
  static void BEFORE_FLOW(@In Integer i1) {}
}

@EventProfileContainer(name = "TestEventProfile1")
class ENAC_TestEventProfile1 {
  @State Integer i1;

  public ENAC_TestEventProfile1() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_1.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@In Integer i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_2 {
  @GlobalFunction
  static void BEFORE_FLOW(@Nullable @In Integer i1) {}
}

@EventProfileContainer(name = "TestEventProfile2")
class ENAC_TestEventProfile2 {
  @State Integer i1;

  public ENAC_TestEventProfile2() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_2.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Nullable @In Integer i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_3 {
  @GlobalFunction
  static void BEFORE_FLOW(@InOut InOutPrm<Integer> i1) {}
}

@EventProfileContainer(name = "TestEventProfile3")
class ENAC_TestEventProfile3 {
  @State Integer i1;

  public ENAC_TestEventProfile3() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_3.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@InOut InOutPrm<Integer> i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_4 {
  @GlobalFunction
  static void BEFORE_FLOW(@InOut NullableInOutPrm<Integer> i1) {}
}

@EventProfileContainer(name = "TestEventProfile4")
class ENAC_TestEventProfile4 {
  @State
  Integer i1;

  public ENAC_TestEventProfile4() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_4.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@InOut NullableInOutPrm<Integer> i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_5 {
  @GlobalFunction
  static void BEFORE_FLOW(@InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@In Integer i1) {}
}

@EventProfileContainer(name = "TestEventProfile5")
class ENAC_TestEventProfile5 {
  @State Integer i1;

  public ENAC_TestEventProfile5() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_5.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_5.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@In Integer i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_6 {
  @GlobalFunction
  static void BEFORE_FLOW(@InOut NullableInOutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@In Integer i1) {}
}

@EventProfileContainer(name = "TestEventProfile6")
class ENAC_TestEventProfile6 {
  @State Integer i1;

  public ENAC_TestEventProfile6() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_6.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@InOut NullableInOutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_6.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@In Integer i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_7 {
  @GlobalFunction
  static void BEFORE_FLOW(@Out(out = Output.OPTIONAL) OutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@InOut InOutPrm<Integer> i1) {}
}

@EventProfileContainer(name = "TestEventProfile7")
class ENAC_TestEventProfile7 {
  @State Integer i1;

  public ENAC_TestEventProfile7() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_7.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Out(out = Output.OPTIONAL) OutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_7.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@InOut InOutPrm<Integer> i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_8 {
  @GlobalFunction
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@InOut InOutPrm<Integer> i1) {}
}

@EventProfileContainer(name = "TestEventProfile8")
class ENAC_TestEventProfile8 {
  @State Integer i1;

  public ENAC_TestEventProfile8() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_8.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_8.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@InOut InOutPrm<Integer> i1) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_9 {
  @GlobalFunction
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @GlobalFunction
  static void AFTER_EXEC(@Out OutPrm<Integer> i2) {}

  @GlobalFunction
  static void FLOW_EXCEPTION(@InOut InOutPrm<Integer> i1, @In Integer i2) {}
}

@EventProfileContainer(name = "TestEventProfile9")
class ENAC_TestEventProfile9 {
  @State Integer i1;
  @State Integer i2;

  public ENAC_TestEventProfile9() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_9.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_9.class,
      globalFunctionName = "AFTER_EXEC",
      types = {EventType.AFTER_EXEC})
  static void AFTER_EXEC(@Out OutPrm<Integer> i2) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_9.class,
      globalFunctionName = "FLOW_EXCEPTION",
      types = {EventType.FLOW_EXCEPTION})
  static void FLOW_EXCEPTION(@InOut InOutPrm<Integer> i1, @In Integer i2) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer_10 {
  @GlobalFunction
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@Out OutPrm<Integer> i2) {}

  @GlobalFunction
  static void FLOW_EXCEPTION(@InOut InOutPrm<Integer> i1, @In Integer i2) {}
}

@EventProfileContainer(name = "TestEventProfile10")
class ENAC_TestEventProfile10 {
  @State Integer i1;
  @State Integer i2;

  public ENAC_TestEventProfile10() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_10.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_10.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@Out OutPrm<Integer> i2) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer_10.class,
      globalFunctionName = "FLOW_EXCEPTION",
      types = {EventType.FLOW_EXCEPTION})
  static void FLOW_EXCEPTION(@InOut InOutPrm<Integer> i1, @In Integer i2) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainer {
  @GlobalFunction
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@In Integer i1, @InOut NullableInOutPrm<Integer> i2) {}

  @GlobalFunction
  static void BEFORE_STEP_ITERATION(@InOut InOutPrm<Integer> i2, @Out OutPrm<Integer> i3) {}

  @GlobalFunction
  static void BEFORE_EXEC(@In Integer i3, @InOut NullableInOutPrm<Integer> i4) {}

  @GlobalFunction
  static void AFTER_EXEC(@InOut InOutPrm<Integer> i4, @Out OutPrm<Integer> i5) {}

  @GlobalFunction
  static void BEFORE_TRANSIT(@In Integer i5, @InOut NullableInOutPrm<Integer> i6) {}

  @GlobalFunction
  static void AFTER_TRANSIT(@InOut InOutPrm<Integer> i6, @Out OutPrm<Integer> i7) {}

  @GlobalFunction
  static void AFTER_STEP_ITERATION(@In Integer i7, @InOut NullableInOutPrm<Integer> i8) {}

  @GlobalFunction
  static void AFTER_STEP(@InOut InOutPrm<Integer> i8, @Out OutPrm<Integer> i9) {}

  @GlobalFunction
  static void AFTER_FLOW(@In Integer i9, @InOut NullableInOutPrm<Integer> i10) {}
}

@EventProfileContainer(name = "TestEventProfile")
class ENAC_TestEventProfile {
  @State Integer i1;
  @State Integer i2;
  @State Integer i3;
  @State Integer i4;
  @State Integer i5;
  @State Integer i6;
  @State Integer i7;
  @State Integer i8;
  @State Integer i9;
  @State Integer i10;

  public ENAC_TestEventProfile() {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Out OutPrm<Integer> i1) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@In Integer i1, @InOut NullableInOutPrm<Integer> i2) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "BEFORE_STEP_ITERATION",
      types = {EventType.BEFORE_STEP_ITERATION})
  static void BEFORE_STEP_ITERATION(@InOut InOutPrm<Integer> i2, @Out OutPrm<Integer> i3) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "BEFORE_EXEC",
      types = {EventType.BEFORE_EXEC})
  static void BEFORE_EXEC(@In Integer i3, @InOut NullableInOutPrm<Integer> i4) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "AFTER_EXEC",
      types = {EventType.AFTER_EXEC})
  static void AFTER_EXEC(@InOut InOutPrm<Integer> i4, @Out OutPrm<Integer> i5) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "BEFORE_TRANSIT",
      types = {EventType.BEFORE_TRANSIT})
  static void BEFORE_TRANSIT(@In Integer i5, @InOut NullableInOutPrm<Integer> i6) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "AFTER_TRANSIT",
      types = {EventType.AFTER_TRANSIT})
  static void AFTER_TRANSIT(@InOut InOutPrm<Integer> i6, @Out OutPrm<Integer> i7) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "AFTER_STEP_ITERATION",
      types = {EventType.AFTER_STEP_ITERATION})
  static void AFTER_STEP_ITERATION(@In Integer i7, @InOut NullableInOutPrm<Integer> i8) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "AFTER_STEP",
      types = {EventType.AFTER_STEP})
  static void AFTER_STEP(@InOut InOutPrm<Integer> i8, @Out OutPrm<Integer> i9) {}

  @EventCall(globalFunctionContainer = ENAC_GlobalFunctionContainer.class,
      globalFunctionName = "AFTER_FLOW",
      types = {EventType.AFTER_FLOW})
  static void AFTER_FLOW(@In Integer i9, @InOut NullableInOutPrm<Integer> i10) {}
}

@GlobalFunctionContainer
class ENAC_GlobalFunctionContainerBIG {
  @GlobalFunction
  static void BEFORE_FLOW(@Nullable @In Integer i1) {}

  @GlobalFunction
  static void BEFORE_STEP(@InOut NullableInOutPrm<Integer> i2) {}

  @GlobalFunction
  static void BEFORE_STEP_ITERATION(@Nullable @In Integer i3) {}

  @GlobalFunction
  static void BEFORE_EXEC(@InOut NullableInOutPrm<Integer> i4) {}

  @GlobalFunction
  static void AFTER_EXEC(@Nullable @In Integer i5) {}

  @GlobalFunction
  static void BEFORE_TRANSIT(@InOut NullableInOutPrm<Integer> i6) {}

  @GlobalFunction
  static void AFTER_TRANSIT(@Nullable @In Integer i7) {}

  @GlobalFunction
  static void AFTER_STEP_ITERATION(@InOut NullableInOutPrm<Integer> i8) {}

  @GlobalFunction
  static void AFTER_STEP(@Nullable @In Integer i9) {}

  @GlobalFunction
  static void AFTER_FLOW(@InOut NullableInOutPrm<Integer> i10) {}
}

@EventProfileContainer(name = "TestEventProfile")
class ENAC_TestEventProfileBIG {
  @State Integer i1;
  @State Integer i2;
  @State Integer i3;
  @State Integer i4;
  @State Integer i5;
  @State Integer i6;
  @State Integer i7;
  @State Integer i8;
  @State Integer i9;
  @State Integer i10;

  public ENAC_TestEventProfileBIG() {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "BEFORE_FLOW",
      types = {EventType.BEFORE_FLOW})
  static void BEFORE_FLOW(@Nullable @In Integer i1) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "BEFORE_STEP",
      types = {EventType.BEFORE_STEP})
  static void BEFORE_STEP(@InOut NullableInOutPrm<Integer> i2) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "BEFORE_STEP_ITERATION",
      types = {EventType.BEFORE_STEP_ITERATION})
  static void BEFORE_STEP_ITERATION(@Nullable @In Integer i3) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "BEFORE_EXEC",
      types = {EventType.BEFORE_EXEC})
  static void BEFORE_EXEC(@InOut NullableInOutPrm<Integer> i4) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "AFTER_EXEC",
      types = {EventType.AFTER_EXEC})
  static void AFTER_EXEC(@Nullable @In Integer i5) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "BEFORE_TRANSIT",
      types = {EventType.BEFORE_TRANSIT})
  static void BEFORE_TRANSIT(@InOut NullableInOutPrm<Integer> i6) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "AFTER_TRANSIT",
      types = {EventType.AFTER_TRANSIT})
  static void AFTER_TRANSIT(@Nullable @In Integer i7) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "AFTER_STEP_ITERATION",
      types = {EventType.AFTER_STEP_ITERATION})
  static void AFTER_STEP_ITERATION(@InOut NullableInOutPrm<Integer> i8) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "AFTER_STEP",
      types = {EventType.AFTER_STEP})
  static void AFTER_STEP(@Nullable @In Integer i9) {}

  @EventCall(globalFunctionContainer = ENAC_TestEventProfileBIG.class,
      globalFunctionName = "AFTER_FLOW",
      types = {EventType.AFTER_FLOW})
  static void AFTER_FLOW(@InOut NullableInOutPrm<Integer> i10) {}
}
