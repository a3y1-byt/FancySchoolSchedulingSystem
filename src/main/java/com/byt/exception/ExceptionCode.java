package com.byt.exception;

public enum ExceptionCode {
    NOT_NULL_VIOLATION("Field must not be null"),
    NOT_EMPTY_VIOLATION("Field must not be empty"),
    MIN_VALUE_VIOLATION("Field value is less than minimum allowed"),
    MAX_VALUE_VIOLATION("Field value is greater than maximum allowed"),
    VALIDATION_FAILED("Validation failed for request data"),
    VALUE_OUT_OF_RANGE("Field value is out of permitted range"),
    LENGTH_TOO_SHORT("Field length is shorter than allowed"),
    LENGTH_TOO_LONG("Field length is longer than allowed"),
    INVALID_FORMAT("Field format is invalid");

    private final String message;
    ExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
