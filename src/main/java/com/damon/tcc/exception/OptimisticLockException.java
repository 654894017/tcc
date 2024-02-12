package com.damon.tcc.exception;

public class OptimisticLockException extends RuntimeException {

    private static final long serialVersionUID = -5599032057026491568L;

    public OptimisticLockException(String message) {
        super(message);
    }

    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }


}
