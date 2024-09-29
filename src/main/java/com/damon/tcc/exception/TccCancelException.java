package com.damon.tcc.exception;

public class TccCancelException extends RuntimeException {
    public TccCancelException(Throwable cause) {
        super(cause);
    }

    public TccCancelException(String message) {
        super(message);
    }

    public TccCancelException(String message, Throwable cause) {
        super(message, cause);
    }
}
