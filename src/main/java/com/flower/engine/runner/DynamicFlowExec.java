package com.flower.engine.runner;

import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.InternalFlowExec;

import java.time.Duration;

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

    @Override
    public String buildMermaidGraph() {
        return "~~~~~ Method buildMermaidGraph() is NOT supported by DynamicFlowExec ~~~~~";
    }

    @Override
    public Class getFlowType() {
        return void.class;
    }
}
