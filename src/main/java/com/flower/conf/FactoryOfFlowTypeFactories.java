package com.flower.conf;

import com.flower.engine.runner.FlowFactoryPrmImpl;
import com.flower.engine.runner.FlowRunner;
import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.StringUtils;

public class FactoryOfFlowTypeFactories<T> implements FlowRepoPrm<T> {
  final String parentFlowName;
  final String functionName;
  final String functionParameterName;

  final FlowRunner flowRunner;
  @Nullable InternalFlowExec<T> flowExec;

  final boolean dynamic;
  final ParameterizedType genericParameterType;

  public FactoryOfFlowTypeFactories(
      String parentFlowName,
      String functionName,
      String functionParameterName,
      FlowRunner flowRunner,
      boolean dynamic,
      ParameterizedType genericParameterType) {
    this.parentFlowName = parentFlowName;
    this.functionName = functionName;
    this.functionParameterName = functionParameterName;
    this.dynamic = dynamic;
    this.flowRunner = flowRunner;
    this.flowExec = null;
    this.genericParameterType = genericParameterType;
  }

  public FlowFactoryPrm<T> getFactory(FlowId flowId) {
    return new FlowFactoryPrmImpl<>(Preconditions.checkNotNull(flowExec), flowId, this);
  }

  public void initFlowExec() {
    if (dynamic) {
      flowExec = flowRunner.getDynamicFlowExec();
    } else {
      Type genericParameterFlowType = genericParameterType.getActualTypeArguments()[0];
      flowExec = flowRunner.getInternalFlowExec((Class)genericParameterFlowType);
    }
  }

  @Override
  @Nullable
  public ListenableFuture<T> getFlowFuture(FlowId flowId) {
    //TODO: implement type safety
    return (ListenableFuture<T>)flowRunner.getFlowFuture(flowId);
  }
}
