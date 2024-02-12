package com.damon.tcc.exception;

public class BizIdInvalidException extends RuntimeException {

    private static final long serialVersionUID = -5083721379484318038L;

    public BizIdInvalidException(String message) {
        super(message);
    }

    public BizIdInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizIdInvalidException(Throwable cause) {
        super(cause);
    }
}
