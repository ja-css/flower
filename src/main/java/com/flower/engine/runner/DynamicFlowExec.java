package com.flower.engine.runner;

import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.InternalFlowExec;
import com.flower.conf.StateSerializer;

import javax.annotation.Nullable;
import java.time.Duration;

/**
 * Don't use DynamicFlowExec if you can avoid it.
 * Creating FlowExec explicitly on startup allows to fail fast in case you're trying to execute a flow of type
 * not registered/configured in Flower Engine.
 * This one will only fail in runtime.
 */
public class DynamicFlowExec implements InternalFlowExec {
    final FlowRunner flowRunner;

    public DynamicFlowExec(FlowRunner flowRunner) {
        this.flowRunner = flowRunner;
    }

    @Override
    public FlowFuture runChildFlow(FlowId parentFlowId, Object flow) {
        //Dynamic executor for templated flows.
        FlowExec flowExec = flowRunner.getFlowExec(flow.getClass());
        return ((InternalFlowExec)flowExec).runChildFlow(parentFlowId, flow);
    }

    @Override
    public FlowFuture runChildFlow(FlowId parentFlowId, Object flow, Duration startupDelay) {
        FlowExec flowExec = flowRunner.getFlowExec(flow.getClass());
        return ((InternalFlowExec)flowExec).runChildFlow(parentFlowId, flow, startupDelay);
    }

    @Override
    public FlowFuture runFlow(Object flow) {
        FlowExec flowExec = flowRunner.getFlowExec(flow.getClass());
        return flowExec.runFlow(flow);
    }

    @Override
    public FlowFuture runFlow(Object flow, Duration startupDelay) {
        FlowExec flowExec = flowRunner.getFlowExec(flow.getClass());
        return flowExec.runFlow(flow, startupDelay);
    }

    public String buildMermaidGraph(Class<?> flowType) {
        FlowExec flowExec = flowRunner.getFlowExec(flowType);
        return flowExec.buildMermaidGraph(false);
    }

    @Override
    public String buildMermaidGraph(boolean addHeader) {
        throw new UnsupportedOperationException("Method buildMermaidGraph() can't be implemented in DynamicFlowExec, use buildMermaidGraph(Class<?>)");
    }

    @Override
    public Class getFlowType() {
        return void.class;
    }

    @Nullable
    public StateSerializer getStateSerializer(Class<?> flowType) {
        FlowExec flowExec = flowRunner.getFlowExec(flowType);
        return flowExec.getStateSerializer();
    }

    @Nullable
    @Override
    public StateSerializer getStateSerializer() {
        throw new UnsupportedOperationException("Method getStateSerializer() can't be implemented in DynamicFlowExec, use getStateSerializer(Class<?>)");
    }
}
