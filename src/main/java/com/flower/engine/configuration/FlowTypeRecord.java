package com.flower.engine.configuration;

import com.flower.anno.event.DisableEventProfiles;
import com.flower.anno.event.EventCall;
import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepCall;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FlowTypeRecord extends ContainerRecord {
  private boolean initialized = false;

  public final Class<?> flowType;
  final FlowType annotation;
  public final String flowTypeName;
  String firstStepName;
  public final Optional<Class<?>> parentFlowType;

  public Map<String, StateFieldRecord> state;

  public Map<String, StepRecord> steps;
  public Map<String, StepCallRecord> stepCalls;

  public Map<String, TransitionerRecord> transitioners;
  public Map<String, TransitionerCallRecord> transitionerCalls;

  public Map<String, StepAndTransitRecord> stepAndTransits;
  public Map<String, StepAndTransitCallRecord> stepAndTransitCalls;

  public @Nullable
  DisableEventProfiles disableEventProfilesAnno;
  public @Nullable
  EventProfiles eventProfilesAnno;
  public Set<Class<?>> eventProfiles;
  public Set<Class<?>> disableEventProfiles;
  public boolean disableAllExternal;

  public FlowGenericParametersRecord genericParameters;
  final Map<Class<?>, String> eventProfileContainerNameByClass;

  public FlowTypeRecord(
      Class<?> flowType,
      FlowType annotation,
      String flowTypeName,
      @Nullable Class<?> parentFlowType,
      final Map<Class<?>, String> eventProfileContainerNameByClass) {
    this.flowType = flowType;
    this.eventProfileContainerNameByClass = eventProfileContainerNameByClass;

    genericParameters = new FlowGenericParametersRecord(flowType);

    this.parentFlowType = Optional.ofNullable(parentFlowType);
    this.annotation = annotation;
    firstStepName = annotation.firstStep();

    this.flowTypeName = flowTypeName;

    state = new HashMap<>();

    steps = new HashMap<>();
    stepCalls = new HashMap<>();

    transitioners = new HashMap<>();
    transitionerCalls = new HashMap<>();

    stepAndTransits = new HashMap<>();
    stepAndTransitCalls = new HashMap<>();

    eventProfiles = new HashSet<>();
    disableEventProfiles = new HashSet<>();
    disableAllExternal = false;
  }

  @Nullable
  String testUniqueFlowFunction(
      String stepName,
      boolean isFirstStep,
      @Nullable String firstStep,
      List<Set<String>> stepNames) {
    for (Set<String> stepNameSet : stepNames) {
      if (stepNameSet.contains(stepName))
        throw new IllegalStateException(
            "Conflicting Function name for a Flow. FunctionName: ["
                + stepName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
    }

    if (isFirstStep) {
      if (firstStep != null)
        throw new IllegalStateException(
            "Conflicting First Step declarations. StepName1: ["
                + firstStep
                + "] StepName2["
                + stepName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      else firstStep = stepName;
    }
    return firstStep;
  }

  void validateFlow() {
    String firstStep = null;
    // Validate unique flow function names for steps; Validate unique first step declaration
    for (Map.Entry<String, StepRecord> step : steps.entrySet()) {
      String stepName = step.getKey();
      firstStep =
          testUniqueFlowFunction(
              stepName,
              step.getValue().isFirstStep,
              firstStep,
              ImmutableList.of(
                  stepCalls.keySet(),
                  stepAndTransits.keySet(),
                  stepAndTransitCalls.keySet(),
                  transitioners.keySet(),
                  transitionerCalls.keySet()));
    }
    // Validate unique flow function names for step calls; Validate unique first step declaration
    for (Map.Entry<String, StepCallRecord> stepCall : stepCalls.entrySet()) {
      String stepCallName = stepCall.getKey();
      firstStep =
          testUniqueFlowFunction(
              stepCallName,
              stepCall.getValue().isFirstStep,
              firstStep,
              ImmutableList.of(
                  steps.keySet(),
                  stepAndTransits.keySet(),
                  stepAndTransitCalls.keySet(),
                  transitioners.keySet(),
                  transitionerCalls.keySet()));
    }
    // Validate unique flow function names for step&transits; Validate unique first step declaration
    for (Map.Entry<String, StepAndTransitRecord> stepAndTransit : stepAndTransits.entrySet()) {
      String stepAndTransitName = stepAndTransit.getKey();
      firstStep =
          testUniqueFlowFunction(
              stepAndTransitName,
              stepAndTransit.getValue().isFirstStep,
              firstStep,
              ImmutableList.of(
                  steps.keySet(),
                  stepCalls.keySet(),
                  stepAndTransitCalls.keySet(),
                  transitioners.keySet(),
                  transitionerCalls.keySet()));
    }
    // Validate unique flow function names for step&transit calls; Validate unique first step
    // declaration
    for (Map.Entry<String, StepAndTransitCallRecord> stepAndTransitCall :
        stepAndTransitCalls.entrySet()) {
      String stepAndTransitCallName = stepAndTransitCall.getKey();
      firstStep =
          testUniqueFlowFunction(
              stepAndTransitCallName,
              stepAndTransitCall.getValue().isFirstStep,
              firstStep,
              ImmutableList.of(
                  steps.keySet(),
                  stepCalls.keySet(),
                  stepAndTransits.keySet(),
                  transitioners.keySet(),
                  transitionerCalls.keySet()));
    }
    // Validate that first step is declared on this flow
    if (firstStep == null)
      throw new IllegalStateException(
          "FirstStep declaration not found. Flow: [" + flowTypeName + "] class [" + flowType + "]");
    // Validate unique flow function name for transitioners
    for (String transitionerName : transitioners.keySet()) {
      testUniqueFlowFunction(
          transitionerName,
          false,
          null,
          ImmutableList.of(
              steps.keySet(),
              stepCalls.keySet(),
              stepAndTransits.keySet(),
              stepAndTransitCalls.keySet(),
              transitionerCalls.keySet()));
    }
    // Validate unique flow function name for transitioners
    for (String transitionerCallName : transitionerCalls.keySet()) {
      testUniqueFlowFunction(
          transitionerCallName,
          false,
          null,
          ImmutableList.of(
              steps.keySet(),
              stepCalls.keySet(),
              stepAndTransits.keySet(),
              stepAndTransitCalls.keySet(),
              transitioners.keySet()));
    }
  }

  public void mergeWithParent(FlowTypeRecord parent) {
    if (!flowType.getSuperclass().equals(parent.flowType))
      throw new IllegalStateException(
          String.format(
              "ParentFlow must be an immediate superclass of a Flow. Flow: %s; ParentFlow: %s.",
              flowTypeName, parentFlowType));

    genericParameters.mergeWithParentGenericParametersRecord(parent.genericParameters);

    Set<String> flowFunctionNames = new HashSet<>();
    flowFunctionNames.addAll(steps.keySet());
    flowFunctionNames.addAll(stepCalls.keySet());
    flowFunctionNames.addAll(transitioners.keySet());
    flowFunctionNames.addAll(transitionerCalls.keySet());
    flowFunctionNames.addAll(stepAndTransits.keySet());
    flowFunctionNames.addAll(stepAndTransitCalls.keySet());

    // Inherit Flow state fields that are not overridden
    for (Map.Entry<String, StateFieldRecord> entry : parent.state.entrySet()) {
      String parentStateFieldName = entry.getKey();

      if (!state.containsKey(parentStateFieldName))
        state.put(parentStateFieldName, entry.getValue());
    }

    // Inherit functions and calls that are not overridden
    // 1. Steps
    for (Map.Entry<String, StepRecord> step : parent.steps.entrySet()) {
      String parentFunctionName = step.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        steps.put(step.getKey(), step.getValue());
    }

    // 2. Step calls
    for (Map.Entry<String, StepCallRecord> stepCall : parent.stepCalls.entrySet()) {
      String parentFunctionName = stepCall.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        stepCalls.put(stepCall.getKey(), stepCall.getValue());
    }

    // 3. Transitioners
    for (Map.Entry<String, TransitionerRecord> transitioner : parent.transitioners.entrySet()) {
      String parentFunctionName = transitioner.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        transitioners.put(transitioner.getKey(), transitioner.getValue());
    }

    // 4. TransitionerCalls
    for (Map.Entry<String, TransitionerCallRecord> transitionerCall :
        parent.transitionerCalls.entrySet()) {
      String parentFunctionName = transitionerCall.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        transitionerCalls.put(transitionerCall.getKey(), transitionerCall.getValue());
    }

    // 5. StepAndTransits
    for (Map.Entry<String, StepAndTransitRecord> stepAndTransit :
        parent.stepAndTransits.entrySet()) {
      String parentFunctionName = stepAndTransit.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        stepAndTransits.put(stepAndTransit.getKey(), stepAndTransit.getValue());
    }

    // 6. StepAndTransitCalls
    for (Map.Entry<String, StepAndTransitCallRecord> stepAndTransitCall :
        parent.stepAndTransitCalls.entrySet()) {
      String parentFunctionName = stepAndTransitCall.getKey();
      if (!flowFunctionNames.contains(parentFunctionName))
        stepAndTransitCalls.put(stepAndTransitCall.getKey(), stepAndTransitCall.getValue());
    }

    // 7. EventProfiles
    if (!disableAllExternal) {
      // Merge disabled profiles
      disableEventProfiles.addAll(parent.disableEventProfiles);
      // In case parent disables a profile but a child has it in its bindings, child bindings take
      // precedence and a profile is enabled, so we remove it from disableEventProfiles
      for (Class<?> eventProfile : eventProfiles)
        if (parent.disableEventProfiles.contains(eventProfile))
          disableEventProfiles.remove(eventProfile);
      // Merge event profiles
      eventProfiles.addAll(parent.eventProfiles);
    }
  }

  @Nullable
  static <A extends Annotation> A getAnnotationOrNull(Class<?> type, Class<A> anno) {
    return type.isAnnotationPresent(anno) ? type.getAnnotation(anno) : null;
  }

  public void initialize(@Nullable FlowTypeRecord parent) {
    if (StringUtils.isBlank(firstStepName)) {
      if (parent != null) {
        firstStepName = parent.firstStepName;
      } else {
        //TODO: what's this?
        throw new IllegalStateException(
            "Root level flow (parent in inheritance) must have firstStep defined");
      }
    }

    testMethodAnnotations(flowType, flowTypeName);

    state = loadStateParameters(flowType, "Flow", flowTypeName);

    stepCalls = loadStepCalls(flowType, flowTypeName, firstStepName);
    steps = loadSteps(flowType, flowTypeName, firstStepName);

    transitionerCalls = loadTransitionerCalls(flowType, flowTypeName);
    transitioners = loadTransitioners(flowType, flowTypeName);

    stepAndTransitCalls = loadStepAndTransitCalls(flowType, flowTypeName, firstStepName);
    stepAndTransits = loadStepAndTransits(flowType, flowTypeName, firstStepName);

    disableEventProfilesAnno = getAnnotationOrNull(flowType, DisableEventProfiles.class);
    eventProfilesAnno = getAnnotationOrNull(flowType, EventProfiles.class);

    if (disableEventProfilesAnno != null) {
      disableEventProfiles.addAll(Arrays.asList(disableEventProfilesAnno.value()));
      disableAllExternal = disableEventProfilesAnno.disableAllExternal();
    }
    if (eventProfilesAnno != null) eventProfiles.addAll(Arrays.asList(eventProfilesAnno.value()));

    if (parent != null) mergeWithParent(parent);

    validateFlow();

    initialized = true;
  }

  static boolean areTypeBoundsAMatch(
      Type[] functionGenericTypeBounds,
      Type[] flowGenericTypeBounds,
      FlowGenericParametersRecord genericParameters,
      Method method) {
    if (functionGenericTypeBounds == flowGenericTypeBounds) return true;
    if (functionGenericTypeBounds == null || flowGenericTypeBounds == null) return false;

    int length = functionGenericTypeBounds.length;
    if (flowGenericTypeBounds.length != length) return false;

    for (int i = 0; i < length; i++) {
      Type t1 = functionGenericTypeBounds[i];
      Type t2 = flowGenericTypeBounds[i];

      if (!t1.equals(t2)) {
        if (t1 instanceof TypeVariable) {
          if (t2 instanceof TypeVariable) {
            if (!((TypeVariable<?>) t1).getName().equals(((TypeVariable<?>) t2).getName()))
              return false;

            validateFunctionParameter((TypeVariable) t1, genericParameters, method);
          } else {
            return false;
          }
        } else if (t1 instanceof ParameterizedType) {
          if (!(t2 instanceof ParameterizedType)) {
            return false;
          }
          ParameterizedType p1 = (ParameterizedType) t1;
          ParameterizedType p2 = (ParameterizedType) t2;

          if (!p1.getRawType().equals(p2.getRawType())) {
            return false;
          }

          return areTypeBoundsAMatch(
              p1.getActualTypeArguments(), p2.getActualTypeArguments(), genericParameters, method);
        } else {
          return false;
        }
      }
    }

    return true;
  }

  public static void validateFunctionParameter(
      TypeVariable functionGenericType,
      FlowGenericParametersRecord genericParameters,
      Method method) {
    String functionGenericName = functionGenericType.getName();
    Type flowGenericType =
        genericParameters.getCorrespondingType(method.getDeclaringClass(), functionGenericName);

    if (!(flowGenericType instanceof TypeVariable)) {
      throw new IllegalStateException(
          String.format(
              "Flower Function generics must have corresponding Flow generics: type [%s] method [%s] generic name [%s]",
              method.getDeclaringClass(), method.getName(), functionGenericName));
    }

    Type[] functionGenericTypeBounds = functionGenericType.getBounds();
    Type[] flowGenericTypeBounds = ((TypeVariable) flowGenericType).getBounds();

    if (!areTypeBoundsAMatch(
        functionGenericTypeBounds, flowGenericTypeBounds, genericParameters, method)) {
      throw new IllegalStateException(
          String.format(
              "Flower Function generic bounds must equal to corresponding Flow generic bounds: type [%s] method [%s] generic name [%s]",
              method.getDeclaringClass(), method.getName(), functionGenericName));
    }
  }

  public static void validateFunction(
      FlowGenericParametersRecord genericParameters, Method method) {
    for (Type paramType : method.getTypeParameters()) {
      if (paramType instanceof TypeVariable) {
        validateFunctionParameter((TypeVariable) paramType, genericParameters, method);
      }
    }

    // Validation: Flower function must be static
    if ((method.getModifiers() & Modifier.STATIC) == 0)
      throw new AnnotationFormatError(
          String.format(
              "Flower Function must be static: type [%s] method [%s]",
              method.getDeclaringClass(), method.getName()));
  }

  Map<String, StepAndTransitCallRecord> loadStepAndTransitCalls(
      final Class<?> flowType, String flowTypeName, String firstStepName) {
    Map<String, StepAndTransitCallRecord> stepAndTransitCallMap = new HashMap<>();
    List<Method> stepAndTransitCallFunctions =
        getMethodsAnnotatedWith(flowType, SimpleStepCall.class);
    for (Method method : stepAndTransitCallFunctions) {
      validateFunction(genericParameters, method);
      SimpleStepCall annotation = method.getAnnotation(SimpleStepCall.class);
      // TODO: make sure it's not annotated with @TransitParameterOverride etc.
      String stepAndTransitCallName = annotation.name();
      if (stepAndTransitCallName.trim().equals("")) stepAndTransitCallName = method.getName();
      StepAndTransitCallRecord stepAndTransitRecord =
          new StepAndTransitCallRecord(
              flowType,
              method,
              annotation,
              stepAndTransitCallName,
              firstStepName.equals(stepAndTransitCallName));
      if (stepAndTransitCallMap.containsKey(stepAndTransitCallName))
        throw new IllegalStateException(
            "Duplicate TransitionerCall name for a Flow. TransitionerName: ["
                + stepAndTransitCallName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      stepAndTransitRecord.initialize();
      stepAndTransitCallMap.put(stepAndTransitCallName, stepAndTransitRecord);
    }
    return stepAndTransitCallMap;
  }

  Map<String, StepAndTransitRecord> loadStepAndTransits(
      final Class<?> flowType, String flowTypeName, String firstStepName) {
    Map<String, StepAndTransitRecord> stepAndTransitMap = new HashMap<>();
    List<Method> stepAndTransitFunctions =
        getMethodsAnnotatedWith(flowType, SimpleStepFunction.class);
    for (Method method : stepAndTransitFunctions) {
      validateFunction(genericParameters, method);
      SimpleStepFunction annotation = method.getAnnotation(SimpleStepFunction.class);
      // TODO: make sure it's not annotated with @TransitParameterOverride etc.
      String stepAndTransitName = annotation.name();
      if (stepAndTransitName.trim().equals("")) stepAndTransitName = method.getName();
      StepAndTransitRecord stepAndTransitRecord =
          new StepAndTransitRecord(
              flowType,
              method,
              annotation,
              stepAndTransitName,
              firstStepName.equals(stepAndTransitName));
      if (stepAndTransitMap.containsKey(stepAndTransitName))
        throw new IllegalStateException(
            "Duplicate Transitioner name for a Flow. TransitionerName: ["
                + stepAndTransitName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      stepAndTransitRecord.initialize();
      stepAndTransitMap.put(stepAndTransitName, stepAndTransitRecord);
    }
    return stepAndTransitMap;
  }

  static Map<String, StateFieldRecord> loadStateParameters(
      final Class<?> flowType, String entity, String entityName) {
    Map<String, StateFieldRecord> stateFieldMap = new HashMap<>();
    for (Field field : flowType.getDeclaredFields()) {
      if (field.isAnnotationPresent(State.class)) {
        State annotation = field.getAnnotation(State.class);
        Nullable nullable = field.getAnnotation(Nullable.class);
        String stateFieldName = annotation.name();
        boolean isFinal = Modifier.isFinal(field.getModifiers());

/*        reverted. I'm not sure why's that. Setter initialization of static field could be required for event profiles
          that must have default constructors.

          if (Modifier.isStatic(field.getModifiers()) && !isFinal) {
          throw new IllegalStateException(
              "Static state field must be final. Field: ["
                  + stateFieldName
                  + "] "
                  + entity
                  + ": ["
                  + entityName
                  + "] class ["
                  + flowType
                  + "]");
        }*/

        if (stateFieldName.trim().equals("")) stateFieldName = field.getName();
        StateFieldRecord record =
            new StateFieldRecord(flowType, field, annotation, stateFieldName, isFinal, nullable != null);

        if (stateFieldMap.containsKey(stateFieldName))
          throw new IllegalStateException(
              "Duplicate state field name for a Flow. Field: ["
                  + stateFieldName
                  + "] "
                  + entity
                  + ": ["
                  + entityName
                  + "] class ["
                  + flowType
                  + "]");

        stateFieldMap.put(stateFieldName, record);
      }
    }
    return stateFieldMap;
  }

  Map<String, StepRecord> loadSteps(
      final Class<?> flowType, String flowTypeName, String firstStepName) {
    Map<String, StepRecord> stepMap = new HashMap<>();
    List<Method> stepFunctions = getMethodsAnnotatedWith(flowType, StepFunction.class);
    for (Method method : stepFunctions) {
      validateFunction(genericParameters, method);
      StepFunction annotation = method.getAnnotation(StepFunction.class);
      String stepName = annotation.name();
      if (stepName.trim().equals("")) stepName = method.getName();
      StepRecord stepRecord =
          new StepRecord(flowType, method, annotation, stepName, firstStepName.equals(stepName));
      if (stepMap.containsKey(stepName))
        throw new IllegalStateException(
            "Duplicate Step name for a Flow. StepName: ["
                + stepName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      stepRecord.initialize();
      stepMap.put(stepName, stepRecord);
    }
    return stepMap;
  }

  Map<String, StepCallRecord> loadStepCalls(
      final Class<?> flowType, String flowTypeName, String firstStepName) {
    Map<String, StepCallRecord> stepCallsMap = new HashMap<>();
    List<Method> stepCallFunctions = getMethodsAnnotatedWith(flowType, StepCall.class);
    for (Method method : stepCallFunctions) {
      validateFunction(genericParameters, method);
      StepCall annotation = method.getAnnotation(StepCall.class);
      String stepName = annotation.name();
      if (stepName.trim().equals("")) stepName = method.getName();
      StepCallRecord stepCallRecord =
          new StepCallRecord(
              flowType, method, annotation, stepName, firstStepName.equals(stepName));
      if (stepCallsMap.containsKey(stepName))
        throw new IllegalStateException(
            "Duplicate StepCall name for a Flow. StepName: ["
                + stepName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      stepCallRecord.initialize();
      stepCallsMap.put(stepName, stepCallRecord);
    }
    return stepCallsMap;
  }

  Map<String, TransitionerCallRecord> loadTransitionerCalls(
      final Class<?> flowType, String flowTypeName) {
    Map<String, TransitionerCallRecord> transitionerMap = new HashMap<>();
    List<Method> transitCallFunctions = getMethodsAnnotatedWith(flowType, TransitCall.class);
    for (Method method : transitCallFunctions) {
      validateFunction(genericParameters, method);
      TransitCall annotation = method.getAnnotation(TransitCall.class);
      // TODO: make sure it's not annotated with @TransitParameterOverride etc.
      String transitionerName = annotation.name();
      if (transitionerName.trim().equals("")) transitionerName = method.getName();
      TransitionerCallRecord transitionerRecord =
          new TransitionerCallRecord(flowType, method, annotation, transitionerName);
      if (transitionerMap.containsKey(transitionerName))
        throw new IllegalStateException(
            "Duplicate TransitionerCall name for a Flow. TransitionerName: ["
                + transitionerName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      transitionerRecord.initialize();
      transitionerMap.put(transitionerName, transitionerRecord);
    }
    return transitionerMap;
  }

  Map<String, TransitionerRecord> loadTransitioners(final Class<?> flowType, String flowTypeName) {
    Map<String, TransitionerRecord> transitionerMap = new HashMap<>();
    List<Method> transitFunctions = getMethodsAnnotatedWith(flowType, TransitFunction.class);
    for (Method method : transitFunctions) {
      validateFunction(genericParameters, method);
      TransitFunction annotation = method.getAnnotation(TransitFunction.class);
      // TODO: make sure it's not annotated with @TransitParameterOverride etc.
      String transitionerName = annotation.name();
      if (transitionerName.trim().equals("")) transitionerName = method.getName();
      TransitionerRecord transitionerRecord =
          new TransitionerRecord(flowType, method, annotation, transitionerName);
      if (transitionerMap.containsKey(transitionerName))
        throw new IllegalStateException(
            "Duplicate Transitioner name for a Flow. TransitionerName: ["
                + transitionerName
                + "] Flow: ["
                + flowTypeName
                + "] class ["
                + flowType
                + "]");
      transitionerRecord.initialize();
      transitionerMap.put(transitionerName, transitionerRecord);
    }
    return transitionerMap;
  }

  void testMethodAnnotations(final Class<?> flowType, String flowTypeName) {
    for (Method method : flowType.getDeclaredMethods()) {
      GlobalFunction inGlobalFunction = method.getAnnotation(GlobalFunction.class);
      SimpleStepCall inSimpleStepCall = method.getAnnotation(SimpleStepCall.class);
      SimpleStepFunction inSimpleStepFunction = method.getAnnotation(SimpleStepFunction.class);
      StepCall inStepCall = method.getAnnotation(StepCall.class);
      StepFunction inStepFunction = method.getAnnotation(StepFunction.class);
      TransitCall inTransitCall = method.getAnnotation(TransitCall.class);
      TransitFunction inTransitFunction = method.getAnnotation(TransitFunction.class);
      EventFunction inEventFunction = method.getAnnotation(EventFunction.class);
      EventCall inEventCall = method.getAnnotation(EventCall.class);

      // Validation: at most one Flower annotation is allowed for a function
      Annotation[] annotations =
          new Annotation[] {
            inGlobalFunction,
            inSimpleStepCall,
            inSimpleStepFunction,
            inStepCall,
            inStepFunction,
            inTransitCall,
            inTransitFunction,
            inEventFunction,
            inEventCall
          };

      String physicalFunctionName =
          String.format("%s.%s", method.getDeclaringClass().getCanonicalName(), method.getName());

      Annotation first = null;
      for (Annotation annotation : annotations)
        if (annotation != null)
          if (first == null) first = annotation;
          else
            throw new AnnotationFormatError(
                String.format(
                    "FlowType %s / Function %s is annotated as both: %s, %s.",
                    flowTypeName, physicalFunctionName, first, annotation));

      // Validation: Flower function must be static
      if (first != null)
        if ((method.getModifiers() & Modifier.STATIC) == 0)
          throw new AnnotationFormatError(
              String.format(
                  "FlowType %s / Function %s is annotated as: %s, must be static.",
                  flowTypeName, physicalFunctionName, first));
    }
  }

  public boolean isInitialized() {
    return initialized;
  }

  public String getFirstStepName() {
    return firstStepName;
  }
}
