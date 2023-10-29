package com.damon.tcc.exception;

public class BusinessException extends RuntimeException {
    private String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message, null, false, false);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
