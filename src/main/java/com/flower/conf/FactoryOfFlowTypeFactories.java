package com.flower.conf;

import com.flower.engine.runner.FlowFactoryPrmImpl;
import com.flower.engine.runner.FlowRunner;
import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FactoryOfFlowTypeFactories<T> {
  final String parentFlowName;
  final String functionName;
  final String functionParameterName;

  final FlowRunner flowRunner;
  @Nullable InternalFlowExec<T> flowExec;

  final String childFlowName;
  final Class<T> childFlowType;
  final boolean dynamic;
  final ParameterizedType genericParameterType;

  public FactoryOfFlowTypeFactories(
      String parentFlowName,
      String functionName,
      String functionParameterName,
      FlowRunner flowRunner,
      String childFlowName,
      Class<T> childFlowType,
      boolean dynamic,
      ParameterizedType genericParameterType) {
    this.parentFlowName = parentFlowName;
    this.functionName = functionName;
    this.functionParameterName = functionParameterName;
    this.dynamic = dynamic;
    this.flowRunner = flowRunner;
    this.flowExec = null;
    this.childFlowName = childFlowName;
    this.childFlowType = childFlowType;
    this.genericParameterType = genericParameterType;
  }

  public FlowFactoryPrm<T> getFactory(FlowId flowId) {
    return new FlowFactoryPrmImpl<>(Preconditions.checkNotNull(flowExec), flowId, flowRunner);
  }

  public void initFlowExec() {
    if (dynamic) {
      flowExec = flowRunner.getDynamicFlowExec();
    } else {
      if (!StringUtils.isBlank(childFlowName)) {
        flowExec = flowRunner.getInternalFlowExec(childFlowName);
      } else {
        flowExec = flowRunner.getInternalFlowExec(childFlowType);
      }

      Type genericParameterFlowType = genericParameterType.getActualTypeArguments()[0];
      Class<T> execFlowType = flowExec.getFlowType();
      if (!genericParameterFlowType.equals(flowExec.getFlowType())) {
        throw new IllegalStateException(
            String.format(
                "Flow factory parameter generic subtype mismatch: Flow type from FlowExec [%s] Parameter generic subtype [%s]. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
                execFlowType,
                genericParameterFlowType,
                parentFlowName,
                functionName,
                functionParameterName));
      }
    }
  }
}
