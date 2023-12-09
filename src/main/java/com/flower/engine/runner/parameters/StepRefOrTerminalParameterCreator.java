package com.flower.engine.runner.parameters;

import com.flower.anno.params.step.transitOverride.TransitStepRefPrm;
import com.flower.anno.params.step.transitOverride.TransitTerminalPrm;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.Transition;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.engine.runner.step.TransitionImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StepRefOrTerminalParameterCreator extends ParameterCreator {
  StepRefOrTerminalParameterCreator() {}

  InternalTransition getStepRefObject(String flowName, String stepName, String parameterName) {
    return getStepRef(flowName, StringUtils.defaultIfEmpty(stepName, parameterName));
  }

  @Override
  public ParameterCreationResult createParameter(
      FlowTypeRecord flowTypeRecord,
      @Nullable Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethodForTransitionerReference,
      @Nullable FunctionReturnValueRecord stepFunctionReturnValue,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess, // NOT USED
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride,
      @Nullable Type genericInRetType, // NOT USED
      List<InternalTransition> stepRefPrms) {
    final String parameterName = baseParameter.name;

    final ParameterType functionParameterType;
    final Object specialObject;

    validateFixedTypeParameter(
        baseParameter,
        parameterOverrideFromCall,
        transitParameterOverride,
        Transition.class,
        flowTypeRecord.flowTypeName,
        functionOrCallName);

    if (parameterOverrideFromCall == null && transitParameterOverride == null) {
      StepRef stepRefAnnotation = baseParameter.stepRefAnnotation;
      Terminal terminalAnnotation = baseParameter.terminalAnnotation;

      checkOneOf(
          stepRefAnnotation,
          terminalAnnotation,
          flowTypeRecord.flowTypeName,
          functionOrCallName,
          parameterName,
          "@StepRef",
          "@Terminal");

      if (stepRefAnnotation != null) {
        functionParameterType = ParameterType.STEP_REF;
        InternalTransition stepRef =
            getStepRefObject(
                flowTypeRecord.flowTypeName, stepRefAnnotation.stepName(), parameterName);
        stepRefPrms.add(stepRef);
        specialObject = stepRef;
      } else {
        functionParameterType = ParameterType.TERMINAL;
        InternalTransition terminalStepRef = getTerminalStepRef();
        stepRefPrms.add(terminalStepRef);
        specialObject = terminalStepRef;
      }
    } else if (parameterOverrideFromCall != null && transitParameterOverride == null) {
      StepRef stepRefOverrideAnnotation = parameterOverrideFromCall.stepRefAnnotation;
      Terminal terminalOverrideAnnotation = parameterOverrideFromCall.terminalAnnotation;

      checkOneOf(
          stepRefOverrideAnnotation,
          terminalOverrideAnnotation,
          flowTypeRecord.flowTypeName,
          functionOrCallName,
          parameterName,
          "@StepRef",
          "@Terminal");

      if (stepRefOverrideAnnotation != null) {
        functionParameterType = ParameterType.STEP_REF;
        InternalTransition stepRef =
            getStepRefObject(
                flowTypeRecord.flowTypeName, stepRefOverrideAnnotation.stepName(), parameterName);
        stepRefPrms.add(stepRef);
        specialObject = stepRef;
      } else {
        functionParameterType = ParameterType.TERMINAL;
        InternalTransition terminalStepRef = getTerminalStepRef();
        stepRefPrms.add(terminalStepRef);
        specialObject = terminalStepRef;
      }
    } else {
      Preconditions.checkNotNull(transitParameterOverride);

      TransitStepRefPrm transitStepRefPrmAnnotation =
          transitParameterOverride.transitStepRefPrmAnnotation;
      TransitTerminalPrm transitTerminalPrmAnnotation =
          transitParameterOverride.transitTerminalPrmAnnotation;

      checkOneOf(
          transitStepRefPrmAnnotation,
          transitTerminalPrmAnnotation,
          flowTypeRecord.flowTypeName,
          functionOrCallName,
          parameterName,
          "@TransitStepRefPrm",
          "@TransitTerminalPrm");

      if (transitStepRefPrmAnnotation != null) {
        functionParameterType = ParameterType.STEP_REF;
        InternalTransition stepRef =
            getStepRefObject(
                flowTypeRecord.flowTypeName, transitStepRefPrmAnnotation.stepName(), parameterName);
        stepRefPrms.add(stepRef);
        specialObject = stepRef;
      } else {
        functionParameterType = ParameterType.TERMINAL;
        InternalTransition terminalStepRef = getTerminalStepRef();
        stepRefPrms.add(terminalStepRef);
        specialObject = terminalStepRef;
      }
    }

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            baseParameter.genericParameterType,
            specialObject,
            baseParameter.nullableAnnotation != null,
            false),
        ImmutableList.of());
  }

  public InternalTransition getStepRef(String flowName, String stepName) {
    return TransitionImpl.getStepTransition(stepName);
  }

  public InternalTransition getTerminalStepRef() {
    return TransitionImpl.getTerminalTransition();
  }
}
