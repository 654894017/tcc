package com.damon.tcc.exception;

public class TccPrepareException extends RuntimeException {
    public TccPrepareException(Throwable cause) {
        super(cause);
    }

    public TccPrepareException(String message, Throwable cause) {
        super(message, cause);
    }

    public TccPrepareException(String message) {
        super(message);
    }
}
