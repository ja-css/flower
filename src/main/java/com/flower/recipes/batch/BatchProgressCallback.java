package com.flower.recipes.batch;

import javax.annotation.Nullable;

public interface BatchProgressCallback<ID> {
    void progressCallback(ID id, boolean isFinal, @Nullable Throwable exception);
}
