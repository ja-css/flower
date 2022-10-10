package com.flower.engine.runner.parameters;

import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.runner.FlowRunner;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;

public class FunctionCallParameterCreator {
  final FlowRunner flowRunner;

  final InParameterCreator inParameterCreator;
  final InRetParameterCreator inRetParameterCreator;
  final InRetOrExceptionParameterCreator inRetOrExceptionParameterCreator;
  final InOutParameterCreator inOutParameterCreator;
  final OutParameterCreator outParameterCreator;
  final StepRefOrTerminalParameterCreator stepRefOrTerminalParameterCreator;
  final FlowTypeFactoryParameterCreator flowTypeFactoryParameterCreator;
  final FlowRepoParameterCreator flowRepoParameterCreator;
  final EventParameterCreator eventParameterCreator;

  public FunctionCallParameterCreator(FlowRunner flowRunner) {
    this.flowRunner = flowRunner;

    inParameterCreator = new InParameterCreator();
    inRetParameterCreator = new InRetParameterCreator();
    inRetOrExceptionParameterCreator = new InRetOrExceptionParameterCreator();
    inOutParameterCreator = new InOutParameterCreator();
    outParameterCreator = new OutParameterCreator();
    stepRefOrTerminalParameterCreator = new StepRefOrTerminalParameterCreator();
    flowTypeFactoryParameterCreator = new FlowTypeFactoryParameterCreator(flowRunner);
    flowRepoParameterCreator = new FlowRepoParameterCreator(flowRunner);
    eventParameterCreator = new EventParameterCreator();
  }

  public ParameterCreationResult createFunctionCallParameter(
      FlowTypeRecord flowTypeRecord,
      @Nullable Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethod,
      @Nullable FunctionReturnValueRecord stepReturnValueRecord,
      @Nullable Method globalFunctionMethod,
      final FunctionParameterRecord functionParameter,
      StateAccessConfig flowStateAccess,
      @Nullable StateAccessConfig eventProfileStateAccess,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride,
      @Nullable Type genericInRetType,
      List<InternalTransition> stepRefPrms) {
    ParameterCreator parameterCreator;
    StateAccessConfig stateAccessConfig = flowStateAccess;

    // TODO: why was this assignment here?
    // if (parameterOverrideFromCall != null) functionParameter = parameterOverrideFromCall;

    if (functionParameter.inAnnotation != null || functionParameter.inFromFlowAnnotation != null) {
      parameterCreator = inParameterCreator;
      // @In for event functions references EventProfileState
      if (functionParameter.inAnnotation != null && eventProfileStateAccess != null)
        stateAccessConfig = eventProfileStateAccess;
    } else if (functionParameter.inRetAnnotation != null) {
      parameterCreator = inRetParameterCreator;
    } else if (functionParameter.inRetOrExceptionAnnotation != null) {
      parameterCreator = inRetOrExceptionParameterCreator;
    } else if (functionParameter.outAnnotation != null) {
      parameterCreator = outParameterCreator;
      // @Out for event functions references EventProfileState
      if (eventProfileStateAccess != null) stateAccessConfig = eventProfileStateAccess;
    } else if (functionParameter.inOutAnnotation != null) {
      parameterCreator = inOutParameterCreator;
      // @InOut for event functions references EventProfileState
      if (eventProfileStateAccess != null) stateAccessConfig = eventProfileStateAccess;
    } else if (functionParameter.stepRefAnnotation != null
        || functionParameter.terminalAnnotation != null) {
      parameterCreator = stepRefOrTerminalParameterCreator;
    } else if (functionParameter.flowFactoryAnnotation != null) {
      parameterCreator = flowTypeFactoryParameterCreator;
    } else if (functionParameter.flowRepoAnnotation != null) {
      parameterCreator = flowRepoParameterCreator;
    } else if (functionParameter.flowInfoAnnotation != null
        || functionParameter.stepInfoAnnotation != null
        || functionParameter.transitionInfoAnnotation != null
        || functionParameter.eventInfoAnnotation != null
        || functionParameter.flowExceptionAnnotation != null) {
      parameterCreator = eventParameterCreator;
    } else {
      throw new IllegalStateException(
          String.format(
              "Parameter must be annotated with one of [@In, @Out, @InOut, @StepRef, @Terminal, @InRet, @InRetOrException, @FlowFactory, @FlowRepo, @InFromFlow, @OutFromFlow, @InOutFromFlow, @EventInfo, @FlowInfo, @RunningTimeStats, @StepInfo, @TransitionInfo, @FlowException]. Flow [%s] ParameterName [%s]",
              flowTypeRecord.flowTypeName, functionParameter.name));
    }

    return parameterCreator.createParameter(
        flowTypeRecord,
        functionFlowType,
        functionOrCallMethod,
        functionOrCallName,
        stepFunctionMethod,
        stepReturnValueRecord,
        globalFunctionMethod,
        functionParameter,
        stateAccessConfig,
        parameterOverrideFromCall,
        transitParameterOverride,
        genericInRetType,
        stepRefPrms);
  }
}
