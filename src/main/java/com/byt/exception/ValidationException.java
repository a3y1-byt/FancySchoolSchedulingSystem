package com.byt.exception;

public class ValidationException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ValidationException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ValidationException(ExceptionCode exceptionCode, String message) {
        super(message != null ? message : exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
