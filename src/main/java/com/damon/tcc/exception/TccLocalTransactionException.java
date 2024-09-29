package com.damon.tcc.exception;

public class TccLocalTransactionException extends RuntimeException {
    public TccLocalTransactionException(Throwable cause) {
        super(cause);
    }

    public TccLocalTransactionException(String message) {
        super(message);
    }

    public TccLocalTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
