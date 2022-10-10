package com.flower.conf;

import java.time.Duration;
import javax.annotation.Nullable;

public interface Transition {
  Transition setDelay(@Nullable Duration delay);
}
