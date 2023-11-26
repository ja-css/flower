package com.flower.recipes.batch;

import javax.annotation.Nullable;

public interface BatchActionProgressCallback {
    void progressCallback(boolean isFinal, @Nullable Throwable exception);
}
