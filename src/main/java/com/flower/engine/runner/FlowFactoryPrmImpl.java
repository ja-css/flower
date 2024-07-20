package com.flower.engine.runner;

import com.flower.conf.FlowFactoryPrm;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.FlowRepoPrm;
import com.flower.conf.InternalFlowExec;
import com.google.common.util.concurrent.ListenableFuture;
import java.time.Duration;
import javax.annotation.Nullable;

public class FlowFactoryPrmImpl<T> implements FlowFactoryPrm<T>  {
  final InternalFlowExec flowExec;
  final FlowId flowId;
  final FlowRepoPrm flowRepoPrm;

  public FlowFactoryPrmImpl(InternalFlowExec flowExec, FlowId flowId, FlowRepoPrm flowRepoPrm) {
    this.flowExec = flowExec;
    this.flowId = flowId;
    this.flowRepoPrm = flowRepoPrm;
  }

  @Override
  public FlowFuture<T> runChildFlow(T childFlow) {
    FlowFuture flowFuture = flowExec.runChildFlow(flowId, childFlow);
    return new SimpleFlowFuture(flowFuture.getFlowId(), flowFuture.getFuture());
  }

  @Override
  public FlowFuture<T> runChildFlow(T childFlow, Duration delay) {
    FlowFuture flowFuture = flowExec.runChildFlow(flowId, childFlow, delay);
    return new SimpleFlowFuture(flowFuture.getFlowId(), flowFuture.getFuture());
  }

  @Nullable
  @Override
  public ListenableFuture<T> getFlowFuture(FlowId flowId) {
    return flowRepoPrm.getFlowFuture(flowId);
  }
}
