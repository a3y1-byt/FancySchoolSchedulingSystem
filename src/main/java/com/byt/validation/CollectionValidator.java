package com.byt.validation;

import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

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

    public static <TElement> void assertAllElementsAreValid(Iterable<TElement> collection, Predicate<TElement> validityCondition) {
        for (TElement element : collection) {
            if (!validityCondition.test(element))
                throw new ValidationException(ExceptionCode.VALIDATION_FAILED, "Invalid element found: " + element.toString());
        }
    }

    public static <TElement> void assertAllElementsAreValid(TElement[] collection, Predicate<TElement> validityCondition) {
        for (TElement element : collection) {
            if (!validityCondition.test(element))
                throw new ValidationException(ExceptionCode.VALIDATION_FAILED, "Invalid element found: " + element.toString());
        }
    }
}
