package com.damon.tcc.exception;

public class TccSubLogInvalidException extends RuntimeException {

    private static final long serialVersionUID = -6226393810491174354L;

    public TccSubLogInvalidException(String message) {
        super(message);
    }

    public TccSubLogInvalidException(String message, Throwable cause) {
        super(message, cause);
    }


}
