package com.flower.recipes.batch;

import javax.annotation.Nullable;

public interface BatchProgressCallback<ID, MSG> {
    void progressCallback(ID id, boolean isFinal, @Nullable MSG message, @Nullable Throwable exception);
}
