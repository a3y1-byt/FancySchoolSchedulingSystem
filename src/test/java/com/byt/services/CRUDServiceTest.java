package com.byt.services;

import com.byt.persistence.*;
import com.byt.persistence.util.DataSaveKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.Provider;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public abstract class CRUDServiceTest<TEntity> {
    protected static  String TEST_OBJECT_ID = "TestObject";

    protected final CRUDService<TEntity> emptyService;
    protected final CRUDService<TEntity> serviceWithData;

    private final DataSerializer serializer = new JsonDataSerializer();
    private final DataRepository emptyRepository;
    private final DataRepository oneElementRepository;

    private final DataSaveKeys dataRepositoryKey;

    protected CRUDServiceTest(DataSaveKeys dataKey, Function<SaveLoadService, CRUDService<TEntity>> constructor) {
        List<TEntity> emptyRepositoryContents = new ArrayList<>();

        Map<String, String> emptyRepositoryValues = new HashMap<>() {{
            put(dataKey.repositoryKey, serializer.serialize(emptyRepositoryContents));
        }};

        List<TEntity> oneElementRepositoryContents = new ArrayList<>() {{
           add(getSampleObject());
        }};

        Map<String, String> oneElementRepositoryValues = new HashMap<>() {{
            put(dataKey.repositoryKey, serializer.serialize(oneElementRepositoryContents));
        }};

        dataRepositoryKey = dataKey;
        emptyRepository = new InMemoryDataRepository(emptyRepositoryValues);
        oneElementRepository = new InMemoryDataRepository(oneElementRepositoryValues);

        SaveLoadService emptySaveLoadService = new SaveLoadService(serializer, emptyRepository);
        SaveLoadService oneElementSaveLoadService = new SaveLoadService(serializer, oneElementRepository);

        emptyService = constructor.apply(emptySaveLoadService);
        serviceWithData = constructor.apply(oneElementSaveLoadService);
    }

    @BeforeEach
    public void initializeServices() throws IOException {
        emptyService.initialize();
        serviceWithData.initialize();
    }

    @Test
    public void testReturnsFalseOnExistsByNonExistentId() {
        CRUDService<TEntity> service = emptyService;

        try {
            assertFalse(service.exists(getSampleObjectId()));
        } catch (IOException e) {
            fail(e);
        }
    }
    @Test
    public void testReturnsTrueOnExistsByExistentId() {
        CRUDService<TEntity> service = serviceWithData;

        try {
            assertTrue(service.exists(getSampleObjectId()));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testReturnsNullOnGetByNonExistentId() {
        try {
            CRUDService<TEntity> service = emptyService;
            Optional<TEntity> result = service.get(TEST_OBJECT_ID);
            assertTrue(result.isEmpty());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testReturnsObjectOnGetByExistentId() {
        try {
            CRUDService<TEntity> service = serviceWithData;
            Optional<TEntity> result = service.get(getSampleObjectId());

            if (result.isEmpty())
                fail();

            assertEquals(getSampleObject(), result.get());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testReturnsEmptyArrayOnGetAllWhenEmpty() {
        CRUDService<TEntity> service = emptyService;

        try {
            List<TEntity> allEntities = emptyService.getAll();
            assertTrue(allEntities.isEmpty());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testReturnsOneElementArrayOnGetAllWhenHasOneElement() {
        CRUDService<TEntity> service = serviceWithData;

        try {
            List<TEntity> allEntities = serviceWithData.getAll();
            assertEquals(1, allEntities.size());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testThrowsOnDeleteNonExistentObject() {
        CRUDService<TEntity> service = emptyService;
        assertThrows(IllegalArgumentException.class, () -> service.delete(getSampleObjectId()));
    }

    @Test
    public void testDeletesExistentObject() {
        CRUDService<TEntity> service = serviceWithData;

        try {
            service.delete(getSampleObjectId());

            String onDeletionRepositoryRecord = oneElementRepository.read(dataRepositoryKey.repositoryKey);
            String expectedRecord = emptyRepository.read(dataRepositoryKey.repositoryKey);

            assertEquals(expectedRecord, onDeletionRepositoryRecord);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @Disabled // Times changed, folks. Thank reverse connections for that.
    public void testNoReferenceLeakedOnGet() {
        CRUDService<TEntity> service = serviceWithData;

        try {
            Optional<TEntity> initialEntityOptional = service.get(getSampleObjectId());

            if (initialEntityOptional.isEmpty())
                fail("No entity received on test start");

            TEntity initialEntity = initialEntityOptional.get();

            alterEntity(initialEntity);
            Optional<TEntity> updatedEntityOptional = service.get(getSampleObjectId());

            if (updatedEntityOptional.isEmpty())
                fail("No entity found under the same id after alteration");

            TEntity updatedEntity = updatedEntityOptional.get();

            assertNotEquals(initialEntity, updatedEntity);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    public void testNoReferenceLeakedOnGetAll() {
        CRUDService<TEntity> service = serviceWithData;

        try {
            List<TEntity> initialEntities = service.getAll();

            if (initialEntities.isEmpty())
                fail("No entities received on test start");

            TEntity initialFirstEntity = initialEntities.getFirst();
            alterEntity(initialFirstEntity);

            List<TEntity> updatedEntities = service.getAll();

            if (updatedEntities.isEmpty())
                fail("No entities received after alteration");

            TEntity secondCallFirstEntity = updatedEntities.getFirst();

            assertNotEquals(initialFirstEntity, secondCallFirstEntity);
        } catch (IOException e) {
            fail(e);
        }
    }

    protected abstract String getSampleObjectId();
    protected abstract TEntity getSampleObject();
    protected abstract void alterEntity(TEntity entity);
 }
