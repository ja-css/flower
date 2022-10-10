package com.flower.engine.configuration;

import com.flower.anno.params.step.transitOverride.TransitInOutPrm;
import com.flower.anno.params.step.transitOverride.TransitInPrm;
import com.flower.anno.params.step.transitOverride.TransitInRetPrm;
import com.flower.anno.params.step.transitOverride.TransitOutPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
import com.flower.anno.params.step.transitOverride.TransitStepRefPrm;
import com.flower.anno.params.step.transitOverride.TransitTerminalPrm;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class StepFunctionRecord extends FunctionRecord {
  List<TransitParameterOverrideRecord> loadTransitParameterOverrides(Method method) {
    List<TransitParameterOverrideRecord> transitParameterOverrides = new ArrayList<>();

    TransitInPrm transitInPrmAnnotation = method.getAnnotation(TransitInPrm.class);
    TransitInRetPrm transitInRetPrmAnnotation = method.getAnnotation(TransitInRetPrm.class);
    TransitOutPrm transitOutPrmAnnotation = method.getAnnotation(TransitOutPrm.class);
    TransitInOutPrm transitInOutPrmAnnotation = method.getAnnotation(TransitInOutPrm.class);
    TransitStepRefPrm transitStepRefPrmAnnotation = method.getAnnotation(TransitStepRefPrm.class);
    TransitTerminalPrm transitTerminalPrmAnnotation =
        method.getAnnotation(TransitTerminalPrm.class);
    TransitParametersOverride transitParametersOverrideAnnotation =
        method.getAnnotation(TransitParametersOverride.class);

    if (transitInPrmAnnotation != null)
      transitParameterOverrides.add(loadTransitInOverrideRecord(transitInPrmAnnotation, null));
    if (transitInRetPrmAnnotation != null)
      transitParameterOverrides.add(
          loadTransitInRetOverrideRecord(transitInRetPrmAnnotation, null));
    if (transitOutPrmAnnotation != null)
      transitParameterOverrides.add(loadTransitOutOverrideRecord(transitOutPrmAnnotation, null));
    if (transitInOutPrmAnnotation != null)
      transitParameterOverrides.add(
          loadTransitInOutOverrideRecord(transitInOutPrmAnnotation, null));
    if (transitStepRefPrmAnnotation != null)
      transitParameterOverrides.add(
          loadTransitStepRefOverrideRecord(transitStepRefPrmAnnotation, null));
    if (transitTerminalPrmAnnotation != null)
      transitParameterOverrides.add(
          loadTransitTerminalOverrideRecord(transitTerminalPrmAnnotation, null));
    if (transitParametersOverrideAnnotation != null)
      transitParameterOverrides.addAll(
          loadRecordsFromTransitParameterOverride(transitParametersOverrideAnnotation));

    return transitParameterOverrides;
  }

  TransitParameterOverrideRecord loadTransitInOverrideRecord(
      TransitInPrm transitInPrmAnnotation, @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitInPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  TransitParameterOverrideRecord loadTransitInRetOverrideRecord(
      TransitInRetPrm transitInRetPrmAnnotation,
      @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitInRetPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  TransitParameterOverrideRecord loadTransitOutOverrideRecord(
      TransitOutPrm transitOutPrmAnnotation, @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitOutPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  TransitParameterOverrideRecord loadTransitInOutOverrideRecord(
      TransitInOutPrm transitInOutPrmAnnotation,
      @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitInOutPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  TransitParameterOverrideRecord loadTransitStepRefOverrideRecord(
      TransitStepRefPrm transitStepRefPrmAnnotation,
      @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitStepRefPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  TransitParameterOverrideRecord loadTransitTerminalOverrideRecord(
      TransitTerminalPrm transitTerminalPrmAnnotation,
      @Nullable TransitParametersOverride parentAnnotation) {
    TransitParameterOverrideRecord record =
        new TransitParameterOverrideRecord(transitTerminalPrmAnnotation);
    if (parentAnnotation != null) record.parentAnnotation = parentAnnotation;
    return record;
  }

  List<TransitParameterOverrideRecord> loadRecordsFromTransitParameterOverride(
      TransitParametersOverride transitParametersOverrideAnnotation) {
    List<TransitParameterOverrideRecord> transitParameterOverrides = new ArrayList<>();

    for (TransitInPrm transitInPrmAnnotation : transitParametersOverrideAnnotation.in())
      transitParameterOverrides.add(
          loadTransitInOverrideRecord(transitInPrmAnnotation, transitParametersOverrideAnnotation));
    for (TransitInRetPrm transitInRetPrmAnnotation : transitParametersOverrideAnnotation.inRet())
      transitParameterOverrides.add(
          loadTransitInRetOverrideRecord(
              transitInRetPrmAnnotation, transitParametersOverrideAnnotation));
    for (TransitOutPrm transitOutPrmAnnotation : transitParametersOverrideAnnotation.out())
      transitParameterOverrides.add(
          loadTransitOutOverrideRecord(
              transitOutPrmAnnotation, transitParametersOverrideAnnotation));
    for (TransitInOutPrm transitInOutPrmAnnotation : transitParametersOverrideAnnotation.inOut())
      transitParameterOverrides.add(
          loadTransitInOutOverrideRecord(
              transitInOutPrmAnnotation, transitParametersOverrideAnnotation));
    for (TransitStepRefPrm transitStepRefPrmAnnotation :
        transitParametersOverrideAnnotation.stepRef())
      transitParameterOverrides.add(
          loadTransitStepRefOverrideRecord(
              transitStepRefPrmAnnotation, transitParametersOverrideAnnotation));
    for (TransitTerminalPrm transitTerminalPrmAnnotation :
        transitParametersOverrideAnnotation.terminal())
      transitParameterOverrides.add(
          loadTransitTerminalOverrideRecord(
              transitTerminalPrmAnnotation, transitParametersOverrideAnnotation));

    return transitParameterOverrides;
  }
}
