package com.damon.tcc.exception;

public class TccCommitException extends RuntimeException {
    public TccCommitException(Throwable cause) {
        super(cause);
    }

    public TccCommitException(String message) {
        super(message);
    }

    public TccCommitException(String message, Throwable cause) {
        super(message, cause);
    }
}
