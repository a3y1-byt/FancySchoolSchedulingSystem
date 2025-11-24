package com.byt.persistence;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnnecessaryLocalVariable")
public class SaveLoadServiceTest {
    private final DataSerializer serializer = new JsonDataSerializer();

    private final MockEmptyDataRepository emptyRepository = new MockEmptyDataRepository();
    private final MockRepositoryWithData repositoryWithData = new MockRepositoryWithData();

    private final SaveLoadService emptyDataService = new SaveLoadService(serializer, emptyRepository);
    private final SaveLoadService dataServiceWithData = new SaveLoadService(serializer, repositoryWithData);

    private static final String TEST_KEY = "test";

    @Test
    public void testCanLoadFromEmptyRepository() {
        SaveLoadService dataService = emptyDataService;

        assertFalse(dataService.canLoad(TEST_KEY));
    }

    @Test
    public void testCanLoadFromRepositoryWithData() {
        SaveLoadService dataService = dataServiceWithData;

        assertTrue(dataService.canLoad(TEST_KEY));
    }

    @Test
    public void testLoadThrowsOnEmptyRepository() {
        SaveLoadService dataService = emptyDataService;

        assertThrows(IOException.class, () -> dataService.load(TEST_KEY, Object.class));
    }

    @Test
    public void testLoadOnRepositoryWithData() {
        SaveLoadService dataService = dataServiceWithData;

        String loadedValue;

        try {
            loadedValue = (String)dataService.load(TEST_KEY, String.class);
        } catch (IOException e) {
            fail(e);
            return;
        }

        assertEquals(MockRepositoryWithData.CONTAINED_VALUE, loadedValue);
    }

    @Test
    public void testTrySaveOnEmptyRepository() {
        SaveLoadService dataService = emptyDataService;

        assertTrue(dataService.trySave(TEST_KEY, new Object()));
    }

    @Test
    public void testTrySaveOnFullRepository() {
        SaveLoadService dataService = dataServiceWithData;

        assertTrue(dataService.trySave(TEST_KEY, new Object()));
    }

    @Test
    public void testSaveWritesToRepository() {
        SaveLoadService dataService = emptyDataService;

        try {
            dataService.save(TEST_KEY, new Object());
        } catch (IOException e) {
            fail(e);
        }

        assertTrue(emptyRepository.wasWritingOperationPerformed());
    }
}

class MockRepositoryWithData implements DataRepository {
    public static final String CONTAINED_VALUE = "Hello, world!";

    private boolean wasRemoveOperationPerformed = false;

    public boolean wasRemoveOperationPerformed() {
        return wasRemoveOperationPerformed;
    }

    @Override
    public String read(String key) throws IOException {
        return '"' + CONTAINED_VALUE + '"';
    }

    @Override
    public void write(String key, String serializedData) throws IOException {

    }

    @Override
    public void remove(String key) throws IOException {
        wasRemoveOperationPerformed = true;
    }

    @Override
    public boolean exists(String key) {
        return true;
    }
}

class MockEmptyDataRepository implements DataRepository {
    private boolean wasWritingOperationPerformed = false;

    public boolean wasWritingOperationPerformed() {
        return wasWritingOperationPerformed;
    }

    @Override
    public String read(String key) throws IOException {
        throw new IOException();
    }

    @Override
    public void write(String key, String serializedData) throws IOException {
        wasWritingOperationPerformed = true;
    }

    @Override
    public void remove(String key) throws IOException {
        throw new IOException();
    }

    @Override
    public boolean exists(String key) {
        return false;
    }
}