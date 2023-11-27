package com.flower.recipes.batch;

import javax.annotation.Nullable;

public interface BatchActionProgressCallback<MSG> {
    void progressCallback(boolean isFinal, @Nullable MSG message, @Nullable Throwable exception);
}
