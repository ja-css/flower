package com.flower.utilities;

public class FlowerException extends RuntimeException {
    public FlowerException() {
    }

    public FlowerException(String message) {
        super(message);
    }

    public FlowerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowerException(Throwable cause) {
        super(cause);
    }

    public FlowerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
