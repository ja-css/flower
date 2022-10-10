package com.flower.conf;

import javax.annotation.Nullable;

public interface FlowId {
  String id();

  @Nullable
  FlowId parentId();
}
