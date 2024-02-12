package com.damon.tcc.exception;

public class TccMainLogInvalidException extends RuntimeException {

    private static final long serialVersionUID = -6226393810491174354L;

    public TccMainLogInvalidException(String message) {
        super(message);
    }

    public TccMainLogInvalidException(String message, Throwable cause) {
        super(message, cause);
    }


}
