package com.byt.validation;

import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionValidatorTest {
    @Test
    public void testNoDuplicatesDoesntThrowOnEmptyCollections() {
        List<?> emptyList = new ArrayList<>();
        Object[] emptyArray = {};

        assertAll(
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(emptyList)),
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(emptyArray))
        );
    }

    @Test
    public void testNoDuplicatesDoesntThrowOnOneElementCollections() {
        List<?> listWithOneElement = new ArrayList<>() {{ add(new Object()); }};
        Object[] arrayWithOneElement = {new Object()};

        assertAll(
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(listWithOneElement)),
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(arrayWithOneElement))
        );
    }

    @Test
    public void testNoDuplicatesDoesntThrowOnManyElementsCollections() {
        List<?> list = new ArrayList<>() {{
            add(new Object());
            add(new Object());
            add(new Object());
        }};
        Object[] array = { new Object(), new Object(), new Object() };

        assertAll(
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(list)),
                () -> assertDoesNotThrow(() -> CollectionValidator.assertHasNoDuplicates(array))
        );
    }

    @Test
    public void testNoDuplicatesThrowsOnCollectionWithDoubleElements() {
        Object sameObject = new Object();

        List<?> list = new ArrayList<>() {{
            add(sameObject);
            add(sameObject);
        }};
        Object[] array = { sameObject, sameObject };

        assertAll(
                () -> {
                    try {
                        CollectionValidator.assertHasNoDuplicates(list);
                    } catch (ValidationException ex) {
                        if (ex.getExceptionCode() == ExceptionCode.NO_DUPLICATES_VIOLATION)
                            return; // pass
                    }

                    fail();
                },

                () -> {
                    try {
                        CollectionValidator.assertHasNoDuplicates(array);
                    } catch (ValidationException ex) {
                        if (ex.getExceptionCode() == ExceptionCode.NO_DUPLICATES_VIOLATION)
                            return; // pass
                    }

                    fail();
                });
    }

    @Test
    public void testAllValidDoesntThrowOnEmptyCollection() {
        List<?> emptyList = new ArrayList<>();
        Object[] emptyArray = {};

        assertAll( () -> {
                    assertDoesNotThrow(() -> CollectionValidator.assertAllElementsAreValid(emptyList, element -> false));
                    assertDoesNotThrow(() -> CollectionValidator.assertAllElementsAreValid(emptyList, element -> false));
                });
    }

    @Test
    public void testAllValidDoesntThrowWhenAllAreValid() {
        List<?> list = new ArrayList<>() {{
            add(new Object());
            add(new Object());
            add(new Object());
        }};
        Object[] array = { new Object(), new Object(), new Object() };

        assertAll( () -> {
            assertDoesNotThrow(() -> CollectionValidator.assertAllElementsAreValid(list, element -> true));
        });
    }

    @Test
    public void testAllValidThrowsWhenInvalidElementsAreThere() {
        Object invalidObject = new Object();

        List<?> list = new ArrayList<>() {{
            add(new Object());
            add(new Object());
            add(invalidObject);
        }};
        Object[] array = { new Object(), new Object(), invalidObject };

        assertAll( () -> {
            assertThrows(ValidationException.class, () -> CollectionValidator.assertAllElementsAreValid(list, element -> element == invalidObject));
            assertThrows(ValidationException.class, () -> CollectionValidator.assertAllElementsAreValid(array, element -> element == invalidObject));
        });
    }
}
