package com.byt.validation;

import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public final class CollectionValidator {

    private CollectionValidator() {
    }

    public static void assertHasNoDuplicates(Collection<?> collection) {
        if (new HashSet<>(collection).size() != collection.size())
            throw new ValidationException(ExceptionCode.NO_DUPLICATES_VIOLATION, "Duplicate elements found in collection!");
    }

    public static void assertHasNoDuplicates(Object[] array) {
        if (new HashSet<>(List.of(array)).size() != array.length)
            throw new ValidationException(ExceptionCode.NO_DUPLICATES_VIOLATION, "Duplicate elements found in collection!");
    }
}
