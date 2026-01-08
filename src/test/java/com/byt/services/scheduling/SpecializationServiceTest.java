package com.byt.services.scheduling;

import com.byt.data.scheduling.Specialization;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpecializationServiceTest extends CRUDServiceTest<Specialization> {

    protected SpecializationServiceTest() {
        super(DataSaveKeys.SPECIALIZATIONS, SpecializationService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Software Engineering";
    }

    @Override
    protected Specialization getSampleObject() {
        return Specialization.builder()
                .name("Software Engineering")
                .subjects(new HashSet<>())
                .build();
    }

    @Override
    protected void alterEntity(Specialization specialization) {
        specialization.setName(specialization.getName() + " Updated");
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization specialization = Specialization.builder()
                .name("Placeholder Specialization")
                .subjects(new HashSet<>())
                .build();
        service.create(specialization);
        Optional<Specialization> loaded = service.get("Placeholder Specialization");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Specialization", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        SpecializationService service = (SpecializationService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        SpecializationService service = (SpecializationService) serviceWithData;
        Specialization duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        SpecializationService service = (SpecializationService) emptyService;
        Optional<Specialization> result = service.get("Nonexistent Specialization");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        SpecializationService service = (SpecializationService) serviceWithData;
        Optional<Specialization> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllDoesNotThrowWhenInitialized() {
        SpecializationService service = (SpecializationService) serviceWithData;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        SpecializationService service = (SpecializationService) serviceWithData;
        Specialization updated = Specialization.builder()
                .name(getSampleObjectId())
                .subjects(new HashSet<>())
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<Specialization> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(getSampleObjectId(), loaded.get().getName());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization updated = Specialization.builder()
                .name("Nonexistent Specialization")
                .subjects(null)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> service.update("Nonexistent Specialization", updated));
    }


    @Test
    void testUpdateThrowsOnNullPrototype() {
        SpecializationService service = (SpecializationService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException {
        SpecializationService service = (SpecializationService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<Specialization> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        SpecializationService service = (SpecializationService) emptyService;
        assertThrows(IllegalArgumentException.class,
                () -> service.delete("Nonexistent Specialization"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() {
        SpecializationService service = (SpecializationService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() {
        SpecializationService service = (SpecializationService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }

    @Test
    void testCreateThrowsOnNullName() {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization specialization = Specialization.builder()
                .name(null)
                .description("Description")
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(specialization));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnEmptyName() {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization specialization = Specialization.builder()
                .name("")
                .description("Description")
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(specialization));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateAcceptsNullDescription() throws IOException {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization specialization = Specialization.builder()
                .name("Spec Without Description")
                .description(null)
                .build();
        assertDoesNotThrow(() -> service.create(specialization));
        assertTrue(service.exists("Spec Without Description"));
    }

    @Test
    void testCreateThrowsOnEmptyDescription() {
        SpecializationService service = (SpecializationService) emptyService;
        Specialization specialization = Specialization.builder()
                .name("Specialization")
                .description("  ")
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(specialization));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }
}
