package com.byt.services.scheduling;

import com.byt.data.scheduling.Semester;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SemesterServiceTest extends CRUDServiceTest<Semester> {

    protected SemesterServiceTest() {
        super(DataSaveKeys.SEMESTERS, SemesterService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Fall Semester 2025";
    }

    @Override
    protected Semester getSampleObject() {
        return Semester.builder()
                .name("Fall Semester 2025")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 20))
                .academicYear(2025)
                .lessons(null)
                .build();
    }

    @Override
    protected void alterEntity(Semester semester) {
        semester.setAcademicYear(semester.getAcademicYear() + 1);
    }

    @Test
    void testCreateStoresNewEntity() throws IOException, ValidationException {
        SemesterService service = (SemesterService) emptyService;
        Semester semester = Semester.builder()
                .name("Placeholder Semester")
                .startDate(LocalDate.of(2026, 2, 1))
                .endDate(LocalDate.of(2026, 6, 30))
                .academicYear(2026)
                .lessons(null)
                .build();
        service.create(semester);
        Optional<Semester> loaded = service.get("Placeholder Semester");
        assertTrue(loaded.isPresent());
        assertEquals("Placeholder Semester", loaded.get().getName());
    }

    @Test
    void testCreateThrowsOnNullPrototype() {
        SemesterService service = (SemesterService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testCreateThrowsOnDuplicateName() throws IOException, ValidationException {
        SemesterService service = (SemesterService) serviceWithData;
        Semester duplicate = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void testGetReturnsEmptyForNonExistent() {
        SemesterService service = (SemesterService) emptyService;
        Optional<Semester> result = service.get("Nonexistent Semester");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReturnsCopyForExisting() {
        SemesterService service = (SemesterService) serviceWithData;
        Optional<Semester> result = service.get(getSampleObjectId());
        assertTrue(result.isPresent());
        assertEquals(getSampleObjectId(), result.get().getName());
    }

    @Test
    void testGetAllDoesNotThrowWhenInitialized() {
        SemesterService service = (SemesterService) serviceWithData;
        assertNotNull(service.getAll());
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        SemesterService service = (SemesterService) serviceWithData;
        Semester updated = Semester.builder()
                .name(getSampleObjectId())
                .startDate(LocalDate.of(2025, 9, 15))
                .endDate(LocalDate.of(2025, 12, 31))
                .academicYear(2030)
                .lessons(null)
                .build();
        service.update(getSampleObjectId(), updated);
        Optional<Semester> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isPresent());
        assertEquals(2030, loaded.get().getAcademicYear());
        assertEquals(LocalDate.of(2025, 9, 15), loaded.get().getStartDate());
    }

    @Test
    void testUpdateThrowsOnNonExistentName() {
        SemesterService service = (SemesterService) emptyService;
        Semester updated = Semester.builder()
                .name("Nonexistent Semester")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .academicYear(2025)
                .lessons(null)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> service.update("Nonexistent Semester", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        SemesterService service = (SemesterService) serviceWithData;
        assertThrows(ValidationException.class,
                () -> service.update(getSampleObjectId(), null));
    }

    @Test
    void testDeleteRemovesExistingEntity() throws IOException, ValidationException {
        SemesterService service = (SemesterService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<Semester> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentName() {
        SemesterService service = (SemesterService) emptyService;
        assertThrows(IllegalArgumentException.class,
                () -> service.delete("Nonexistent Semester"));
    }

    @Test
    void testExistsReturnsFalseForNullOrEmpty() throws IOException {
        SemesterService service = (SemesterService) serviceWithData;
        assertFalse(service.exists(null));
        assertFalse(service.exists(""));
    }

    @Test
    void testExistsReturnsTrueForExistingName() throws IOException {
        SemesterService service = (SemesterService) serviceWithData;
        assertTrue(service.exists(getSampleObjectId()));
    }
}
