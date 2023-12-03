package com.damon.tcc.exception;

public class BizIdConflictException extends RuntimeException {

    public BizIdConflictException(String message) {
        super(message);
    }

    public BizIdConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizIdConflictException(Throwable cause) {
        super(cause);
    }
}
