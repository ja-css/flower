package com.flower.engine;

import com.flower.conf.FlowId;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

public class FlowerId implements FlowId {
  private final UUID id;
  @Nullable private final FlowerId parentId;

  public FlowerId(UUID id) {
    this.id = id;
    parentId = null;
  }

  public FlowerId(UUID id, @Nullable FlowerId parentId) {
    this.id = id;
    this.parentId = parentId;
  }

  public FlowerId() {
    this(UUID.randomUUID());
  }

  public FlowerId(FlowerId parentId) {
    this(UUID.randomUUID(), parentId);
  }

  @Override
  public String id() {
    return id.toString();
  }

  @Override
  @Nullable
  public FlowId parentId() {
    return parentId;
  }

  @Nullable
  public FlowerId parentFlowerId() {
    return parentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FlowerId)) return false;
    FlowerId flowerId = (FlowerId) o;
    return Objects.equals(id, flowerId.id) && Objects.equals(parentId, flowerId.parentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parentId);
  }

  @Override
  public String toString() {
    return FlowerIdSerializer.serialize(this);
  }
}
