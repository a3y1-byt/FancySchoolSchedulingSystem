package com.byt.validation.scheduling;

import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

public class Validation {

    public static void notNull(Object object) throws ValidationException {
        if (object == null) {
            throw new ValidationException(ExceptionCode.NOT_NULL_VIOLATION);
        }
    }

    public static void notEmpty(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(ExceptionCode.NOT_EMPTY_VIOLATION);
        }
    }

    public static void checkMin(int value, int min) throws ValidationException {
        if (value < min) {
            throw new ValidationException(ExceptionCode.MIN_VALUE_VIOLATION);
        }
    }

    public static void checkMax(int value, int max) throws ValidationException {
        if (value > max) {
            throw new ValidationException(ExceptionCode.MAX_VALUE_VIOLATION);
        }
    }

    public static void notNullArgument(Object object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("Cannot pass null value!");
        }
    }

    public static void notEmptyArgument(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot pass empty value!");
        }
    }
}
