package com.byt.services.scheduling;

import com.byt.data.scheduling.ClassRoom;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClassRoomServiceTest extends CRUDServiceTest<ClassRoom> {

    protected ClassRoomServiceTest() {
        super(DataSaveKeys.CLASSROOMS, ClassRoomService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Lecture Hall";
    }

    @Override
    protected ClassRoom getSampleObject() {
        return ClassRoom.builder()
                .name("Lecture Hall")
                .floor(1)
                .capacity(100)
                .build();
    }

    @Override
    protected void alterEntity(ClassRoom classRoom) {
        classRoom.setCapacity(classRoom.getCapacity() + 1);
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        ClassRoomService service = (ClassRoomService) emptyService;
        ClassRoom classRoom = ClassRoom.builder()
                .name("Placeholder Room")
                .floor(2)
                .capacity(50)
                .build();
        service.create(classRoom);
        Optional<ClassRoom> loaded = service.get("Placeholder Room");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Room", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        ClassRoomService service = (ClassRoomService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        ClassRoom duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        ClassRoomService service = (ClassRoomService) emptyService;
        Optional<ClassRoom> result = service.get("Nonexistent Room");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        Optional<ClassRoom> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllReturnsNullWhenListUninitialized() throws IOException {
        ClassRoomService service = (ClassRoomService) emptyService;

        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        ClassRoom updated = ClassRoom.builder()
                .name(getSampleObjectId())
                .floor(1)
                .capacity(200)
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<ClassRoom> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(200, loaded.get().getCapacity());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        ClassRoomService service = (ClassRoomService) emptyService;
        ClassRoom updated = ClassRoom.builder()
                .name("Nonexistent Room")
                .floor(1)
                .capacity(30)
                .build();
        assertThrows(IllegalArgumentException.class, () -> service.update("Nonexistent Room", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        assertThrows(ValidationException.class, () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException, ValidationException {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<ClassRoom> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        ClassRoomService service = (ClassRoomService) emptyService;
        assertThrows(IllegalArgumentException.class, () -> service.delete("Nonexistent Room"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() {
        ClassRoomService service = (ClassRoomService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }

    @Test
    void testCreateThrowsOnNullName() {
        ClassRoomService service = (ClassRoomService) emptyService;
        ClassRoom classRoom = ClassRoom.builder()
                .name(null)
                .floor(1)
                .capacity(50)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(classRoom));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

    @Test
    void testCreateThrowsOnEmptyName() {
        ClassRoomService service = (ClassRoomService) emptyService;
        ClassRoom classRoom = ClassRoom.builder()
                .name("  ")
                .floor(1)
                .capacity(50)
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(classRoom));
        assertEquals(ExceptionCode.NOT_EMPTY_VIOLATION, ex.getExceptionCode());
    }

}
