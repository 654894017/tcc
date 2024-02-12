package com.damon.tcc.exception;

public class BizIdConflictException extends RuntimeException {

    private static final long serialVersionUID = -5083721379484318038L;

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
