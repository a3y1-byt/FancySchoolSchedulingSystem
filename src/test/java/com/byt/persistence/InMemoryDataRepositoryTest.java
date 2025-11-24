package com.byt.persistence;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnnecessaryLocalVariable")
public class InMemoryDataRepositoryTest {
    private final DataRepository emptyRepository = new InMemoryDataRepository();
    private final DataRepository repositoryWithNumber = new InMemoryDataRepository(new HashMap<>() {{
        put(TEST_NUMBER_KEY, "1");
    }});

    private static final String TEST_NUMBER_KEY = "Number";

    @Test
    public void testExistsWithNoValue() {
        DataRepository repository = emptyRepository;

        assertFalse(repository.exists(TEST_NUMBER_KEY));
    }

    @Test
    public void testExistsWithValue() {
        DataRepository repository = repositoryWithNumber;

        assertTrue(repository.exists(TEST_NUMBER_KEY));
    }

    @Test
    public void testReadThrowsWithNonExistentKey() {
        DataRepository repository = emptyRepository;

        assertThrows(IOException.class, () -> repository.read(TEST_NUMBER_KEY));
    }

    @Test
    public void testReadReturnsValueWithExistingKey() {
        DataRepository repository = repositoryWithNumber;
        String expected = "1";

        try {
            assertEquals(expected, repositoryWithNumber.read(TEST_NUMBER_KEY));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testWriteWithNoFormerValue() {
        DataRepository repository = emptyRepository;
        String value = "1";

        try {
            repository.write(TEST_NUMBER_KEY, value);

            assertEquals(value, repository.read(TEST_NUMBER_KEY));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testWriteWithValueToOverwrite() {
        DataRepository repository = emptyRepository;
        String newValue = "2";

        try {
            repository.write(TEST_NUMBER_KEY, newValue);

            assertEquals(newValue, repository.read(TEST_NUMBER_KEY));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testRemoveThrowsWhenKeyNotExists() {
        DataRepository repository = emptyRepository;

        assertThrows(IOException.class, () -> repository.remove(TEST_NUMBER_KEY));
    }

    @Test
    public void testRemoveDeletesValueWhenKeyExists() {
        DataRepository repository = repositoryWithNumber;

        try {
            repository.remove(TEST_NUMBER_KEY);

            assertFalse(repository.exists(TEST_NUMBER_KEY));
        } catch (IOException e) {
            fail(e);
        }
    }
}
