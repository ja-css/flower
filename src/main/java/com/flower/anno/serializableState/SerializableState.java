package com.flower.anno.serializableState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO: refine the abstraction and possible implementation
/** Marks FlowType's or EventProfile's state as serializable.
 * State serialization is disabled for all FlowType-s and EventProfile-s NOT annotated with @SerializableState.
 * Engine-level State serializer override fails for FlowType-s and EventProfile-s NOT annotated with @SerializableState.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface SerializableState {
  /** Default state serializer class.
   * This field is optional, if not set - engine-level state serializer override is required for serialization, see:
   * - Flower.overrideFlowStateSerializer
   * - Flower.overrideEventProfileStateSerializer
   */
  Class<?> serializer() default void.class;
}
