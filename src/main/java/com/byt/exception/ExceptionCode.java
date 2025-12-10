package com.byt.exception;

public enum ExceptionCode {
    NOT_NULL_VIOLATION("Field must not be null"),
    NOT_EMPTY_VIOLATION("Field must not be empty"),
    MIN_VALUE_VIOLATION("Field value is less than minimum allowed"),
    MAX_VALUE_VIOLATION("Field value is greater than maximum allowed"),
    VALIDATION_FAILED("Validation failed for request data");

    private final String message;
    ExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
