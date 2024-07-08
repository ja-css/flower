package com.flower.engine.runner.callfunc;

import com.flower.anno.event.EventType;
import com.flower.anno.params.common.Output;
import com.flower.conf.FactoryOfFlowTypeFactories;
import com.flower.conf.FlowId;
import com.flower.conf.Transition;
import com.flower.engine.function.FlowerInOutPrm;
import com.flower.engine.function.FlowerOutPrm;
import com.flower.engine.function.FlowerRetValOrException;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.event.EventParametersProvider;
import com.flower.engine.runner.state.StateAccess;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.utilities.FuturesTool;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class FunctionCallUtil {
  // ---------------- STEP FUNCTION ----------------

  public static ListenableFuture<?> invokeStepFunction(
          FlowId flowId, StepFunctionCallState callState, FunctionCallContext callContext)
      throws Exception {
    // 1) Get Function parameters from Flow State
    List<Object> functionCallParameters =
        getStepParametersFromFlowState(flowId, callState, callContext);

    try {
      // 2) Call function
      ListenableFuture<?> functionFuture =
          rawInvokeFunction(callContext.function, functionCallParameters);

      // 3) Update Flow state with Output parameters
      return FuturesTool.tryCatchAsync(functionFuture,
          functionReturnValue ->
              updateState(
                  callState.stateAccess,
                  callState.returnValueToFlowParameterName,
                  callContext.functionParameters,
                  functionCallParameters,
                  functionReturnValue,
                  callContext.getFlowStepInfo(),
                  false),
          Throwable.class,
          t -> {
            updateState(
                callState.stateAccess,
                callState.returnValueToFlowParameterName,
                callContext.functionParameters,
                functionCallParameters,
                null,
                callContext.getFlowStepInfo(),
                true);
            return Futures.immediateFailedFuture(t);
          },
          MoreExecutors.directExecutor());
    } catch(Throwable t) {
      updateState(
          callState.stateAccess,
          callState.returnValueToFlowParameterName,
          callContext.functionParameters,
          functionCallParameters,
          null,
          callContext.getFlowStepInfo(),
          true);
      throw t;
    }
  }

  static List<Object> getStepParametersFromFlowState(
      FlowId flowId, StepFunctionCallState callState, FunctionCallContext functionCallContext) {
    String flowStepInfo = functionCallContext.getFlowStepInfo();
    List<Object> parameters = new ArrayList<>();
    for (FunctionCallParameter callParameter : functionCallContext.functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      switch (parameterType) {
        case IN:
        case OUT:
        case IN_OUT:
          parameters.add(getInOutParameter(callState.stateAccess, callParameter, flowStepInfo));
          break;
        case EXEC:
        case FLOW_REPO:
          parameters.add(callParameter.getSpecialObject());
          break;
        case CHILD_FLOW_FACTORY_REF:
          parameters.add(
              ((FactoryOfFlowTypeFactories) callParameter.getSpecialObject()).getFactory(flowId));
          break;
        case TRANSIT_IN_RET: // Transit-only
        case STEP_REF: // Transit-only
        case TERMINAL: // Transit-only
        case IN_FROM_FLOW: // Event-only
        case FLOW_INFO: // Event-only
        case STEP_INFO: // Event-only
        case TRANSITION_INFO: // Event-only
        case EVENT_INFO: // Event-only
        case FLOW_EXCEPTION: // Event-only
          throw new IllegalStateException(
              String.format(
                  flowStepInfo + " Fatal: Function parameter type is not supported in step functions: [%s]. FlowField [%s] FunctionPrm [%s]",
                  parameterType,
                  callParameter.getStateFieldName(),
                  callParameter.getFunctionParameterName()));
      }
    }
    return parameters;
  }

  // ---------------- TRANSIT FUNCTION ----------------

  public static ListenableFuture<InternalTransition> invokeTransitFunctionOnException(
      TransitFunctionExceptionCallState callState, FunctionCallContext callContext)
      throws Exception {
    // 1) Get Function parameters from Flow State
    List<Object> functionParameterObjects =
        getTransitParametersFromFlowState(callState, callContext);

    try {
      // 2) Call function
      ListenableFuture<?> functionFuture =
          rawInvokeFunction(callContext.function, functionParameterObjects);

      // 3) Update Flow state with Output parameters
      return FuturesTool.tryCatchAsync(
          functionFuture,
          functionReturnValue ->
              updateTransitionState(
                  callState.stateAccess,
                  callContext.functionParameters,
                  functionParameterObjects,
                  (InternalTransition) functionReturnValue,
                  callContext.getFlowStepInfo(),
                  false),
          Throwable.class,
          t -> {
            updateTransitionState(
                callState.stateAccess,
                callContext.functionParameters,
                functionParameterObjects,
                null,
                callContext.getFlowStepInfo(),
                true);
            return Futures.immediateFailedFuture(t);
          },
          MoreExecutors.directExecutor());
    } catch(Throwable t) {
      updateTransitionState(
          callState.stateAccess,
          callContext.functionParameters,
          functionParameterObjects,
          null,
          callContext.getFlowStepInfo(),
          true);
      throw t;
    }
  }

  public static ListenableFuture<InternalTransition> invokeTransitFunction(
      TransitFunctionCallState callState, FunctionCallContext callContext) throws Exception {
    // 1) Get Function parameters from Flow State
    List<Object> functionParameterObjects =
        getTransitParametersFromFlowState(callState, callContext);

    try {
      // 2) Call function
      ListenableFuture<?> functionFuture =
          rawInvokeFunction(callContext.function, functionParameterObjects);

      // 3) Update Flow state with Output parameters
      return FuturesTool.tryCatchAsync(
          functionFuture,
          functionReturnValue ->
              updateTransitionState(
                  callState.stateAccess,
                  callContext.functionParameters,
                  functionParameterObjects,
                  (InternalTransition) functionReturnValue,
                  callContext.getFlowStepInfo(),
                  false),
          Throwable.class,
          t -> {
            updateTransitionState(
                callState.stateAccess,
                callContext.functionParameters,
                functionParameterObjects,
                null,
                callContext.getFlowStepInfo(),
                true);
            return Futures.immediateFailedFuture(t);
          },
          MoreExecutors.directExecutor());
    } catch(Throwable t) {
      updateTransitionState(
          callState.stateAccess,
          callContext.functionParameters,
          functionParameterObjects,
          null,
          callContext.getFlowStepInfo(),
          true);
      throw t;
    }
  }

  static List<Object> getTransitParametersFromFlowState(
      TransitFunctionCallState callState, FunctionCallContext functionCallContext) {
    String flowStepInfo = functionCallContext.getFlowStepInfo();
    List<Object> parameters = new ArrayList<>();
    for (FunctionCallParameter callParameter : functionCallContext.functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      switch (parameterType) {
        case IN:
        case OUT:
        case IN_OUT:
          parameters.add(getInOutParameter(callState.stateAccess, callParameter, flowStepInfo));
          break;
        case EXEC:
        case STEP_REF:
        case TERMINAL:
          parameters.add(callParameter.getSpecialObject());
          break;
        case TRANSIT_IN_RET:
          parameters.add(callState.previousReturnValue);
          break;
        case TRANSIT_IN_RET_OR_EXCEPTION:
          parameters.add(new FlowerRetValOrException<>(callState.previousReturnValue));
          break;
        case CHILD_FLOW_FACTORY_REF: // Step-only
        case IN_FROM_FLOW: // Event-only
        case FLOW_INFO: // Event-only
        case STEP_INFO: // Event-only
          // case TRANSITIONER_INFO: // Event-only
        case TRANSITION_INFO: // Event-only
        case EVENT_INFO: // Event-only
        case FLOW_EXCEPTION: // Event-only
        case FLOW_REPO: // Event-only
          //        case RUNNING_TIME_STATS: // Event-only
          throw new IllegalStateException(
              String.format(
                  flowStepInfo + " Fatal: Function parameter type is not supported in transit functions: [%s]. FlowField [%s] FunctionPrm [%s]",
                  parameterType,
                  callParameter.getStateFieldName(),
                  callParameter.getFunctionParameterName()));
      }
    }
    return parameters;
  }

  static List<Object> getTransitParametersFromFlowState(
      TransitFunctionExceptionCallState callState, FunctionCallContext functionCallContext) {
    String flowStepInfo = functionCallContext.getFlowStepInfo();
    List<Object> parameters = new ArrayList<>();
    for (FunctionCallParameter callParameter : functionCallContext.functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      switch (parameterType) {
        case IN:
        case OUT:
        case IN_OUT:
          parameters.add(getInOutParameter(callState.stateAccess, callParameter, flowStepInfo));
          break;
        case EXEC:
        case STEP_REF:
        case TERMINAL:
          parameters.add(callParameter.getSpecialObject());
          break;
        case TRANSIT_IN_RET_OR_EXCEPTION:
          parameters.add(new FlowerRetValOrException<>(callState.stepFunctionException));
          break;
        case TRANSIT_IN_RET: // That's why transitioner can't have both TRANSIT_IN_RET and
          // TRANSIT_IN_RET_OR_EXCEPTION
        case CHILD_FLOW_FACTORY_REF: // Step-only
        case IN_FROM_FLOW: // Event-only
        case FLOW_INFO: // Event-only
        case STEP_INFO: // Event-only
          // case TRANSITIONER_INFO: // Event-only
        case TRANSITION_INFO: // Event-only
        case EVENT_INFO: // Event-only
        case FLOW_EXCEPTION: // Event-only
        case FLOW_REPO: // Event-only
          //        case RUNNING_TIME_STATS: // Event-only
          throw new IllegalStateException(
              String.format(
                  flowStepInfo + " Fatal: Function parameter type is not supported in transit functions: [%s]. FlowField [%s] FunctionPrm [%s]",
                  parameterType,
                  callParameter.getStateFieldName(),
                  callParameter.getFunctionParameterName()));
      }
    }
    return parameters;
  }

  // ---------------- STEP-AND-TRANSIT FUNCTION ----------------

  public static ListenableFuture<InternalTransition> invokeStepAndTransitFunction(
      FlowId flowId, StepAndTransitFunctionCallState callState, FunctionCallContext callContext)
      throws Exception {
    // 1) Get Function parameters from Flow State
    List<Object> functionCallParameters =
        getStepAndTransitParametersFromFlowState(flowId, callState, callContext);

    try {
    // 2) Call function
    ListenableFuture<?> functionFuture =
        rawInvokeFunction(callContext.function, functionCallParameters);

    // 3) Update Flow state with Output parameters
    return FuturesTool.tryCatchAsync(
        functionFuture,
        functionReturnValue ->
            updateTransitionState(
                callState.stateAccess,
                callContext.functionParameters,
                functionCallParameters,
                (InternalTransition) functionReturnValue,
                callContext.getFlowStepInfo(),
                false),
        Throwable.class,
        t -> {
          updateTransitionState(
              callState.stateAccess,
              callContext.functionParameters,
              functionCallParameters,
              null,
              callContext.getFlowStepInfo(),
              true);
          return Futures.immediateFailedFuture(t);
        },
        MoreExecutors.directExecutor());
    } catch(Throwable t) {
      updateTransitionState(
          callState.stateAccess,
          callContext.functionParameters,
          functionCallParameters,
          null,
          callContext.getFlowStepInfo(),
          true);
      throw t;
    }
  }

  static List<Object> getStepAndTransitParametersFromFlowState(
      FlowId flowId,
      StepAndTransitFunctionCallState callState,
      FunctionCallContext functionCallContext) {
    String flowStepInfo = functionCallContext.getFlowStepInfo();
    List<Object> parameters = new ArrayList<>();
    for (FunctionCallParameter callParameter : functionCallContext.functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      switch (parameterType) {
        case IN:
        case OUT:
        case IN_OUT:
          parameters.add(getInOutParameter(callState.stateAccess, callParameter, flowStepInfo));
          break;
        case EXEC:
        case STEP_REF:
        case TERMINAL:
        case FLOW_REPO:
          parameters.add(callParameter.getSpecialObject());
          break;
        case CHILD_FLOW_FACTORY_REF:
          parameters.add(
              ((FactoryOfFlowTypeFactories) callParameter.getSpecialObject()).getFactory(flowId));
          break;
        case TRANSIT_IN_RET: // Transit-only
        case IN_FROM_FLOW: // Event-only
        case FLOW_INFO: // Event-only
        case STEP_INFO: // Event-only
          // case TRANSITIONER_INFO: // Event-only
        case TRANSITION_INFO: // Event-only
        case EVENT_INFO: // Event-only
        case FLOW_EXCEPTION: // Event-only
          //        case RUNNING_TIME_STATS: // Event-only
          throw new IllegalStateException(
              String.format(
                  flowStepInfo + " Fatal: Function parameter type is not supported in step-and-transit functions: [%s]. FlowField [%s] FunctionPrm [%s]",
                  parameterType,
                  callParameter.getStateFieldName(),
                  callParameter.getFunctionParameterName()));
      }
    }
    return parameters;
  }

  // ---------------- EVENT FUNCTION ----------------

  public static ListenableFuture<Void> invokeEventFunction(
      EventFunctionCallState callState,
      FunctionCallContext callContext,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    // 1) Get Function parameters from Flow State
    List<Object> functionCallParameters =
        getEventParametersFromEventProfileAndFlowState(
            callState, callContext, eventParametersProvider, eventType, transition, flowException);
    try {
      // 2) Call function
      ListenableFuture<?> functionFuture;

      try {
        functionFuture = rawInvokeFunction(callContext.function, functionCallParameters);
      } catch (Throwable t) {
        functionFuture = Futures.immediateFailedFuture(t);
      }

      // 3) Update Flow state with Output parameters
      return FuturesTool.tryCatchAsync(
          functionFuture,
          functionReturnValue ->
              updateEventState(
                  callState.flowStateAccess,
                  callState.eventProfileStateAccess,
                  callContext.functionParameters,
                  functionCallParameters,
                  functionReturnValue,
                  callContext.getFlowStepInfo(),
                  false),
          Throwable.class,
          t -> {
            updateEventState(
                callState.flowStateAccess,
                callState.eventProfileStateAccess,
                callContext.functionParameters,
                functionCallParameters,
                null,
                callContext.getFlowStepInfo(),
                true);
            return Futures.immediateFailedFuture(t);
          },
          MoreExecutors.directExecutor());
    } catch(Throwable t) {
      updateEventState(
          callState.flowStateAccess,
          callState.eventProfileStateAccess,
          callContext.functionParameters,
          functionCallParameters,
          null,
          callContext.getFlowStepInfo(),
          true);
      throw t;
    }
  }

  static List<Object> getEventParametersFromEventProfileAndFlowState(
      EventFunctionCallState callState,
      FunctionCallContext functionCallContext,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    String flowStepInfo = functionCallContext.getFlowStepInfo();
    List<Object> parameters = new ArrayList<>();
    for (FunctionCallParameter callParameter : functionCallContext.functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      switch (parameterType) {
        case IN_FROM_FLOW:
          parameters.add(getInOutParameter(callState.flowStateAccess, callParameter, flowStepInfo));
          break;
        case IN:
        case OUT:
        case IN_OUT:
          parameters.add(getInOutParameter(callState.eventProfileStateAccess, callParameter, flowStepInfo));
          break;
        case EXEC:
          parameters.add(callParameter.getSpecialObject());
        case FLOW_INFO:
          parameters.add(eventParametersProvider.getFlowInfo());
          break;
        case STEP_INFO:
          parameters.add(eventParametersProvider.getStepInfo());
          break;
        case TRANSITION_INFO:
          parameters.add(transition);
          break;
        case EVENT_INFO:
          parameters.add(eventType);
          break;
        case FLOW_EXCEPTION: // Event-only
          parameters.add(flowException);
          break;
        case TRANSIT_IN_RET: // Transit-only
        case STEP_REF: // Transit-only
        case TERMINAL: // Transit-only
        case CHILD_FLOW_FACTORY_REF: // Step-only
        case FLOW_REPO: // Step-only
          throw new IllegalStateException(
              String.format(
                  flowStepInfo + " Fatal: Function parameter type is not supported in event functions: [%s]. FlowField [%s] FunctionPrm [%s]",
                  parameterType,
                  callParameter.getStateFieldName(),
                  callParameter.getFunctionParameterName()));
      }
    }
    return parameters;
  }

  // ---------------- COMMON ----------------

  @Nullable
  static Object getInOutParameter(StateAccess stateAccess, FunctionCallParameter callParameter, String flowStepInfo) {
    ParameterType parameterType = callParameter.getFunctionParameterType();
    Object parameterValue;
    if (parameterType == ParameterType.OUT) parameterValue = new FlowerOutPrm<>();
    else {
      if (parameterType == ParameterType.IN_FROM_FLOW) {
        if (!stateAccess.hasField(Preconditions.checkNotNull(callParameter.getStateFieldName()))) {

          // Defaults for primitive type parameters when Flow State Field doesn't exist
          if (callParameter.getParameterType().equals(byte.class)) parameterValue = (byte) 0;
          else if (callParameter.getParameterType().equals(short.class)) parameterValue = (short) 0;
          else if (callParameter.getParameterType().equals(int.class)) parameterValue = 0;
          else if (callParameter.getParameterType().equals(long.class)) parameterValue = 0L;
          else if (callParameter.getParameterType().equals(float.class)) parameterValue = 0F;
          else if (callParameter.getParameterType().equals(double.class)) parameterValue = 0D;
          else if (callParameter.getParameterType().equals(boolean.class)) parameterValue = false;
          else if (callParameter.getParameterType().equals(char.class)) parameterValue = (char) 0;
          else parameterValue = null;

        } else {
          parameterValue =
              stateAccess.getField(Preconditions.checkNotNull(callParameter.getStateFieldName()));
        }
      } else {
        parameterValue =
            stateAccess.getField(Preconditions.checkNotNull(callParameter.getStateFieldName()));
        if (parameterType == ParameterType.IN_OUT) {
          parameterValue = new FlowerInOutPrm<>(parameterValue);
        }
      }
    }

    if (callParameter.isCheckNotNull()) {
      if (parameterValue == null) {
        throw new IllegalStateException(
            String.format(
                flowStepInfo + " Fatal: Function parameter marked as `CheckNotNull` has value null: [%s]. FlowField [%s] FunctionPrm [%s]",
                parameterType,
                callParameter.getStateFieldName(),
                callParameter.getFunctionParameterName()));
      }
    }
    return parameterValue;
  }

  static ListenableFuture<?> rawInvokeFunction(Method function, List<Object> functionCallParameters)
      throws Exception {
    // Call function
    ListenableFuture<?> retFuture;
    try {
      Object retObj = function.invoke(null, functionCallParameters.toArray());
      retFuture =
          (retObj instanceof ListenableFuture)
              ? (ListenableFuture<?>) retObj
              : Futures.immediateFuture(retObj);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw getUnwrappedException(e);
    }

    return retFuture;
  }

  static ListenableFuture<?> updateState(
      StateAccess flowStateAccess,
      @Nullable String returnValueToFlowParameterName,
      List<FunctionCallParameter> functionParameters,
      List<Object> functionParameterObjects,
      @Nullable Object returnValue,
      String flowStepInfo,
      boolean partial) {
    return Futures.transformAsync(
        waitForOutParams(functionParameters, functionParameterObjects),
        void_ -> {
          // Flow State
          updateState0(
              flowStateAccess,
              returnValueToFlowParameterName,
              functionParameters,
              functionParameterObjects,
              returnValue,
              flowStepInfo,
              partial);
          return Futures.immediateFuture(returnValue);
        },
        MoreExecutors.directExecutor());
  }

  static ListenableFuture<InternalTransition> updateTransitionState(
      StateAccess flowStateAccess,
      List<FunctionCallParameter> functionParameters,
      List<Object> functionParameterObjects,
      @Nullable InternalTransition transitionReturnValue,
      String flowStepInfo,
      boolean partial) {
    return Futures.transformAsync(
        waitForOutParams(functionParameters, functionParameterObjects),
        void_ -> {
          // Flow State
          updateState0(
              flowStateAccess,
              null,
              functionParameters,
              functionParameterObjects,
              transitionReturnValue,
              flowStepInfo,
              partial);
          return Futures.immediateFuture(transitionReturnValue);
        },
        MoreExecutors.directExecutor());
  }

  static ListenableFuture<Void> updateEventState(
      StateAccess flowStateAccess,
      StateAccess eventProfileStateAccess,
      List<FunctionCallParameter> functionParameters,
      List<Object> functionParameterObjects,
      @Nullable Object returnValue,
      String flowStepInfo,
      boolean partial) {
    return Futures.transformAsync(
        waitForOutParams(functionParameters, functionParameterObjects),
        void_ -> {
          // Event Profile State
          updateState0(
              eventProfileStateAccess,
              null,
              functionParameters,
              functionParameterObjects,
              returnValue,
              flowStepInfo,
              partial);

          //TODO: I don't think this should be here, Event shouldn't be able to update flow state.
          // commenting out for now.
          // Update Flow State
          /*updateState0(
              flowStateAccess,
              null,
              functionParameters,
              functionParameterObjects,
              returnValue,
              flowStepInfo,
              partial);*/
          return Futures.immediateVoidFuture();
        },
        MoreExecutors.directExecutor());
  }

  static ListenableFuture<Void> waitForOutParams(
      List<FunctionCallParameter> functionParameters, List<Object> functionParameterObjects) {
    List<ListenableFuture<?>> futureList = new ArrayList<>();

    int i = 0;
    for (FunctionCallParameter callParameter : functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      if (parameterType == ParameterType.OUT || parameterType == ParameterType.IN_OUT) {
        FlowerOutPrm<?> flowerOutPrm = (FlowerOutPrm<?>) functionParameterObjects.get(i);
        if (flowerOutPrm.getOptFuture().isPresent()) {
          futureList.add(flowerOutPrm.getOptFuture().get());
        }
      }
      i++;
    }

    if (futureList.isEmpty()) {
      return Futures.immediateVoidFuture();
    } else {
      return Futures.whenAllComplete(futureList).call(() -> null, MoreExecutors.directExecutor());
    }
  }

  static void updateState0(
      StateAccess stateAccess,
      @Nullable String returnValueToFlowParameterName,
      List<FunctionCallParameter> functionParameters,
      List<Object> functionParameterObjects,
      @Nullable Object returnValue,
      String flowStepInfo,
      boolean partial)
      throws Exception {
    ParameterType outType = ParameterType.OUT;
    ParameterType inOutType = ParameterType.IN_OUT;

    // Update values of Out and InOut Flow fields
    int i = 0;
    for (FunctionCallParameter callParameter : functionParameters) {
      ParameterType parameterType = callParameter.getFunctionParameterType();
      if (parameterType == outType || parameterType == inOutType) {
        FlowerOutPrm<?> flowerOutPrm = (FlowerOutPrm<?>) functionParameterObjects.get(i);
        updateOutParameter(callParameter, flowerOutPrm, stateAccess, flowStepInfo, partial);
      }
      i++;
    }
    if (!partial) {
      // If function's return value is bound to a Flow field, update that field as well
      if (returnValueToFlowParameterName != null && returnValue != null)
        stateAccess.updateField(returnValueToFlowParameterName, returnValue);
    }
  }

  static Exception getUnwrappedException(Exception e) {
    if (e.getCause() != null) {
      Throwable t = e.getCause();
      if (t instanceof Exception) return (Exception) t;
      else return new Exception(t);
    } else return e;
  }

  static void updateOutParameter(
      FunctionCallParameter callParameter, FlowerOutPrm<?> flowerOutPrm, StateAccess stateAccess, String flowStepInfo, boolean partial)
      throws Exception {
    Object value = null;
    if (flowerOutPrm.getOpt().isPresent()) {
      value = flowerOutPrm.getOpt().get();
    } else if (flowerOutPrm.getOptFuture().isPresent()) {
      try {
        // Please note that Future.get() will never block here, because in
        // FunctionCallUtil.updateEventState(...)
        // the call to updateState0(...), which calls this method is preceded by waitForOutParams()
        value = flowerOutPrm.getOptFuture().get().get();
      } catch (Exception e) {
        if (!partial) {
          //Ignore Exceptions for partial updates
          throw getUnwrappedException(e);
        }
      }
    }

    if (value != null) {
      stateAccess.updateField(Preconditions.checkNotNull(callParameter.getStateFieldName()), value);
    } else {
      // TODO: at some point this needs to be changed to compile-level or initialization-level check
      // TODO: to prevent raising this Exception at runtime
      ParameterType parameterType = callParameter.getFunctionParameterType();
      if ((parameterType == ParameterType.OUT || parameterType == ParameterType.IN_OUT)
          && callParameter.getOutput() == Output.MANDATORY && !partial)
        //TODO: inject flow and function name here for better message clarity

        //TODO: should we throw? this can cause NPE potentially
        // BUT when we throw, it can destroy otherwise perfectly fine flow mid flight for no good reason and
        // is VERY hard to catch since it's runtime.
        // My current take on this is that possible future error-prone/null-away style code analysis should take better care of it.
        System.err.println(
            String.format(
                flowStepInfo + " Fatal: value of Out or InOut parameter with Output.MANDATORY wasn't set in the Function call. FlowField [%s] FunctionPrm [%s]",
                callParameter.getStateFieldName(), callParameter.getFunctionParameterName()));
    }
  }
}
