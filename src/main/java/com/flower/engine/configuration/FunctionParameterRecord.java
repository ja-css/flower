package com.flower.engine.configuration;

import com.flower.anno.params.common.Exec;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowException;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.events.InFromFlow;
import com.flower.anno.params.events.StepInfo;
import com.flower.anno.params.events.TransitionInfo;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.step.FlowRepo;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FunctionParameterRecord {
  @Nullable public In inAnnotation = null;
  @Nullable public Out outAnnotation = null;
  @Nullable public InOut inOutAnnotation = null;
  @Nullable public Exec execAnnotation = null;

  @Nullable public InFromFlow inFromFlowAnnotation = null;

  @Nullable public StepRef stepRefAnnotation = null;
  @Nullable public Terminal terminalAnnotation = null;
  @Nullable public InRet inRetAnnotation = null;
  @Nullable public InRetOrException inRetOrExceptionAnnotation = null;
  @Nullable public FlowFactory flowFactoryAnnotation = null;
  @Nullable public FlowRepo flowRepoAnnotation = null;

  @Nullable public EventInfo eventInfoAnnotation = null;
  @Nullable public FlowInfo flowInfoAnnotation = null;
  @Nullable public StepInfo stepInfoAnnotation = null;
  @Nullable public TransitionInfo transitionInfoAnnotation = null;
  @Nullable public FlowException flowExceptionAnnotation = null;

  @Nullable public final Nullable nullableAnnotation;

  @Nullable public final Class<?> flowType;
  public final FunctionParameterType type;
  public final String name;
  @Nullable public final String fieldName;
  public final Parameter prm;
  public final Type genericParameterType;

  FunctionParameterRecord(
      Parameter prm,
      Type genericParameterType,
      FunctionParameterType type,
      String name,
      @Nullable Class<?> flowType,
      @Nullable String fieldName,
      Nullable nullableAnnotation) {
    this.flowType = flowType;
    this.prm = prm;
    this.genericParameterType = genericParameterType;
    this.type = type;
    this.name = StringUtils.defaultIfBlank(name, prm.getName());
    this.fieldName = fieldName;
    this.nullableAnnotation = nullableAnnotation;
  }

  FunctionParameterRecord(
      In inAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.IN,
        inAnnotation.name(),
        flowType,
        StringUtils.defaultIfBlank(
            inAnnotation.from(), StringUtils.defaultIfBlank(inAnnotation.name(), prm.getName())),
        nullableAnnotation);
    this.inAnnotation = inAnnotation;
  }

  FunctionParameterRecord(
      Out outAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.OUT,
        outAnnotation.name(),
        flowType,
        StringUtils.defaultIfBlank(
            outAnnotation.to(), StringUtils.defaultIfBlank(outAnnotation.name(), prm.getName())),
        nullableAnnotation);
    this.outAnnotation = outAnnotation;
  }

  FunctionParameterRecord(
      Exec execAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.STEP_INFO,
        execAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.execAnnotation = execAnnotation;
  }

  FunctionParameterRecord(
      InOut inOutAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.IN_OUT,
        inOutAnnotation.name(),
        flowType,
        StringUtils.defaultIfBlank(
            inOutAnnotation.fromAndTo(),
            StringUtils.defaultIfBlank(inOutAnnotation.name(), prm.getName())),
        nullableAnnotation);
    this.inOutAnnotation = inOutAnnotation;
  }

  FunctionParameterRecord(
      StepRef stepRefAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.STEP_REF,
        stepRefAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.stepRefAnnotation = stepRefAnnotation;
  }

  FunctionParameterRecord(
      Terminal terminalAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.TERMINAL,
        terminalAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.terminalAnnotation = terminalAnnotation;
  }

  FunctionParameterRecord(
      InRet inRetAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.IN_RET,
        inRetAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.inRetAnnotation = inRetAnnotation;
  }

  FunctionParameterRecord(
      InRetOrException inRetOrExceptionAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.IN_RET_OR_EXCEPTION,
        inRetOrExceptionAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.inRetOrExceptionAnnotation = inRetOrExceptionAnnotation;
  }

  FunctionParameterRecord(
      FlowFactory flowFactoryAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.FLOW_TYPE_FACTORY,
        flowFactoryAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.flowFactoryAnnotation = flowFactoryAnnotation;
  }

  FunctionParameterRecord(
      FlowRepo flowRepoAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.FLOW_TYPE_FACTORY,
        flowRepoAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.flowRepoAnnotation = flowRepoAnnotation;
  }

  FunctionParameterRecord(
      InFromFlow inFromFlowAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.IN_FROM_FLOW,
        inFromFlowAnnotation.name(),
        flowType,
        StringUtils.defaultIfBlank(
            inFromFlowAnnotation.from(),
            StringUtils.defaultIfBlank(inFromFlowAnnotation.name(), prm.getName())),
        nullableAnnotation);
    this.inFromFlowAnnotation = inFromFlowAnnotation;
  }

  FunctionParameterRecord(
      EventInfo eventInfoAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.EVENT_INFO,
        eventInfoAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.eventInfoAnnotation = eventInfoAnnotation;
  }

  FunctionParameterRecord(
      FlowInfo flowInfoAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.FLOW_INFO,
        flowInfoAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.flowInfoAnnotation = flowInfoAnnotation;
  }

  FunctionParameterRecord(
      StepInfo stepInfoAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.STEP_INFO,
        stepInfoAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.stepInfoAnnotation = stepInfoAnnotation;
  }

  FunctionParameterRecord(
      TransitionInfo transitionInfoAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.TRANSITION_INFO,
        transitionInfoAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.transitionInfoAnnotation = transitionInfoAnnotation;
  }

  FunctionParameterRecord(
      FlowException flowExceptionAnnotation,
      Nullable nullableAnnotation,
      Parameter prm,
      Type genericParameterType,
      @Nullable Class<?> flowType) {
    this(
        prm,
        genericParameterType,
        FunctionParameterType.FLOW_EXCEPTION,
        flowExceptionAnnotation.name(),
        flowType,
        null,
        nullableAnnotation);
    this.flowExceptionAnnotation = flowExceptionAnnotation;
  }
}
