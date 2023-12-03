package com.damon.tcc.exception;

public class TccSubLogInvalidException extends RuntimeException {

    public TccSubLogInvalidException(String message) {
        super(message);
    }

    public TccSubLogInvalidException(String message, Throwable cause) {
        super(message, cause);
    }


}
