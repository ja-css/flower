package com.flower.engine.configuration;

import com.flower.anno.params.step.transitOverride.TransitInOutPrm;
import com.flower.anno.params.step.transitOverride.TransitInPrm;
import com.flower.anno.params.step.transitOverride.TransitInRetPrm;
import com.flower.anno.params.step.transitOverride.TransitOutPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
import com.flower.anno.params.step.transitOverride.TransitStepRefPrm;
import com.flower.anno.params.step.transitOverride.TransitTerminalPrm;
import javax.annotation.Nullable;

public class TransitParameterOverrideRecord {
  @Nullable public TransitInPrm transitInPrmAnnotation = null;
  @Nullable public TransitOutPrm transitOutPrmAnnotation = null;
  @Nullable public TransitInOutPrm transitInOutPrmAnnotation = null;
  @Nullable public TransitInRetPrm transitInRetPrmAnnotation = null;
  @Nullable public TransitStepRefPrm transitStepRefPrmAnnotation = null;
  @Nullable public TransitTerminalPrm transitTerminalPrmAnnotation = null;

  @Nullable TransitParametersOverride parentAnnotation = null;

  public final TransitParameterOverrideType type;
  public final String paramName;
  @Nullable String flowStateField = null;
  @Nullable String stepName = null;

  public TransitParameterOverrideRecord(TransitInPrm transitInPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_IN;
    this.transitInPrmAnnotation = transitInPrmAnnotation;

    this.paramName = transitInPrmAnnotation.paramName();
    this.flowStateField = transitInPrmAnnotation.from();
  }

  public TransitParameterOverrideRecord(TransitOutPrm transitOutPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_OUT;
    this.transitOutPrmAnnotation = transitOutPrmAnnotation;

    this.paramName = transitOutPrmAnnotation.paramName();
    this.flowStateField = transitOutPrmAnnotation.to();
  }

  public TransitParameterOverrideRecord(TransitInOutPrm transitInOutPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_IN_OUT;
    this.transitInOutPrmAnnotation = transitInOutPrmAnnotation;

    this.paramName = transitInOutPrmAnnotation.paramName();
    this.flowStateField = transitInOutPrmAnnotation.fromAndTo();
  }

  public TransitParameterOverrideRecord(TransitInRetPrm transitInRetPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_IN_RET;
    this.transitInRetPrmAnnotation = transitInRetPrmAnnotation;

    this.paramName = transitInRetPrmAnnotation.paramName();
  }

  public TransitParameterOverrideRecord(TransitStepRefPrm transitStepRefPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_STEP_REF;
    this.transitStepRefPrmAnnotation = transitStepRefPrmAnnotation;

    this.paramName = transitStepRefPrmAnnotation.paramName();
    this.stepName = transitStepRefPrmAnnotation.stepName();
  }

  public TransitParameterOverrideRecord(TransitTerminalPrm transitTerminalPrmAnnotation) {
    this.type = TransitParameterOverrideType.TRANSIT_TERMINAL;
    this.transitTerminalPrmAnnotation = transitTerminalPrmAnnotation;

    this.paramName = transitTerminalPrmAnnotation.paramName();
  }
}
