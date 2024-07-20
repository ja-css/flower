package com.flower.flows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFactoryPrm;
import com.flower.conf.FlowFuture;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowerGenericParentFlowTest {
    @Test
    public void testChildFlowExecution() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(RunGenericChildParentFlow.class);
        flower.registerFlow(SuperFlow.class);
        flower.registerFlow(InheritedFlow.class);
        flower.initialize();

        FlowExec<RunGenericChildParentFlow> flowExec = flower.getFlowExec(RunGenericChildParentFlow.class);
        FlowExec<SuperFlow> flowExecSp = flower.getFlowExec(SuperFlow.class);
        FlowExec<InheritedFlow> flowExecIn = flower.getFlowExec(InheritedFlow.class);

        {
            SuperFlow superFlow = new SuperFlow();
            assertEquals("~~~~~~~~~~~~~ SuperFlow execution done! ~~~~~~~~~~~~~",
                flowExecSp.runFlow(superFlow).getFuture().get().result);
        }

        {
            InheritedFlow inheritedFlow = new InheritedFlow();
            assertEquals("~~~~~~~~~~~~~ InheritedFlow execution done! ~~~~~~~~~~~~~",
                flowExecIn.runFlow(inheritedFlow).getFuture().get().result);
        }

        {
            SuperFlow superFlow = new SuperFlow();
            RunGenericChildParentFlow testSuperFlow = new RunGenericChildParentFlow(superFlow);
            flowExec.runFlow(testSuperFlow).getFuture().get();
            assertEquals("~~~~~~~~~~~~~ SuperFlow execution done! ~~~~~~~~~~~~~",
                superFlow.result);
        }

        {
            InheritedFlow inheritedFlow = new InheritedFlow();
            RunGenericChildParentFlow testChildFlow = new RunGenericChildParentFlow(inheritedFlow);
            flowExec.runFlow(testChildFlow).getFuture().get();
            assertEquals("~~~~~~~~~~~~~ InheritedFlow execution done! ~~~~~~~~~~~~~",
                inheritedFlow.result);
        }
    }

    @Test
    public void testUnknownChildFlowFailure() {
        Flower flower = new Flower();
        flower.registerFlow(RunGenericChildParentFlow.class);
        flower.registerFlow(SuperFlow.class);
        flower.registerFlow(InheritedFlow.class);
        flower.initialize();

        FlowExec<RunGenericChildParentFlow> flowExec = flower.getFlowExec(RunGenericChildParentFlow.class);

        InheritedFlow2 inheritedFlow = new InheritedFlow2();
        RunGenericChildParentFlow testChildFlow = new RunGenericChildParentFlow(inheritedFlow);
        try {
            flowExec.runFlow(testChildFlow).getFuture().get();
            //should never reach here
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("FlowExec not found"));
        }
    }
}

@FlowType(firstStep = "runChild")
class RunGenericChildParentFlow {
    @State
    final SuperFlow child;

    public RunGenericChildParentFlow(SuperFlow child) {
        this.child = child;
    }

    @SimpleStepFunction
    public static ListenableFuture<Transition> runChild(@FlowFactory FlowFactoryPrm<SuperFlow> childFlowFactory,
                                                        @In SuperFlow child,
                                                        @Terminal Transition end) {
        FlowFuture<SuperFlow> childFlowFuture = childFlowFactory.runChildFlow(child);
        System.out.println("Child flow created. Id: " + childFlowFuture.getFlowId());
        return Futures.transform(childFlowFuture.getFuture(),
            childFlow -> {
                System.out.println("Child flow done. Result: " + childFlow.result);
                return end;
            },
            MoreExecutors.directExecutor());
    }
}

@FlowType(name="SuperFlow", firstStep = "runAction")
class SuperFlow {
    @State public String result = "SuperFlow didn't execute";

    @SimpleStepFunction
    public static Transition runAction(@Out OutPrm<String> result,
                                       @Terminal Transition end) {
        result.setOutValue("~~~~~~~~~~~~~ SuperFlow execution done! ~~~~~~~~~~~~~");
        return end;
    }
}

@FlowType(extendz=SuperFlow.class)
class InheritedFlow extends SuperFlow {
    @SimpleStepFunction
    public static Transition runAction(@Out OutPrm<String> result,
                                       @Terminal Transition end) {
        result.setOutValue("~~~~~~~~~~~~~ InheritedFlow execution done! ~~~~~~~~~~~~~");
        return end;
    }
}

@FlowType(extendz=SuperFlow.class)
class InheritedFlow2 extends SuperFlow {
    @SimpleStepFunction
    public static Transition runAction(@Out OutPrm<String> result,
                                       @Terminal Transition end) {
        result.setOutValue("~~~~~~~~~~~~~ InheritedFlow execution done! ~~~~~~~~~~~~~");
        return end;
    }
}
