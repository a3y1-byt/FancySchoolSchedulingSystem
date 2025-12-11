package com.byt.services.scheduling;


import com.byt.data.scheduling.Group;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceTest extends CRUDServiceTest<Group> {

    protected GroupServiceTest() {
        super(DataSaveKeys.GROUPS, GroupService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "G-2025-Fall";
    }

    @Override
    protected Group getSampleObject() {
        return Group.builder()
                .name("G-2025-Fall")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(20)
                .minCapacity(10)
                .yearOfStudy(3)
                .notes(null)
                .lessons(null)
                .students(null)
                .build();
    }

    @Override
    protected void alterEntity(Group group) {
        group.setMaxCapacity(group.getMaxCapacity() + 1);
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        GroupService service = (GroupService) emptyService;
        Group group = Group.builder()
                .name("Placeholder Group")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(15)
                .minCapacity(5)
                .yearOfStudy(1)
                .notes(null)
                .lessons(null)
                .students(null)
                .build();
        service.create(group);
        Optional<Group> loaded = service.get("Placeholder Group");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Group", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        GroupService service = (GroupService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        GroupService service = (GroupService) serviceWithData;
        Group duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        GroupService service = (GroupService) emptyService;
        Optional<Group> result = service.get("Nonexistent Group");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        GroupService service = (GroupService) serviceWithData;
        Optional<Group> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllDoesNotThrowWhenInitialized() {
        GroupService service = (GroupService) serviceWithData;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        GroupService service = (GroupService) serviceWithData;
        Group updated = Group.builder()
                .name(getSampleObjectId())
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(18)
                .minCapacity(9)
                .yearOfStudy(4)
                .notes(null)
                .lessons(null)
                .students(null)
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<Group> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(18, loaded.get().getMaxCapacity());
        assertEquals(4, loaded.get().getYearOfStudy());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        GroupService service = (GroupService) emptyService;
        Group updated = Group.builder()
                .name("Nonexistent Group")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(10)
                .minCapacity(5)
                .yearOfStudy(1)
                .notes(null)
                .lessons(null)
                .students(null)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> service.update("Nonexistent Group", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        GroupService service = (GroupService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException {
        GroupService service = (GroupService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<Group> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        GroupService service = (GroupService) emptyService;
        assertThrows(IllegalArgumentException.class, () -> service.delete("Nonexistent Group"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() throws IOException {
        GroupService service = (GroupService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() throws IOException {
        GroupService service = (GroupService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }
}
