package com.damon.tcc.exception;

public class TccTryException extends RuntimeException {
    public TccTryException(Throwable cause) {
        super(cause);
    }

    public TccTryException(String message, Throwable cause) {
        super(message, cause);
    }

    public TccTryException(String message) {
        super(message);
    }
}
