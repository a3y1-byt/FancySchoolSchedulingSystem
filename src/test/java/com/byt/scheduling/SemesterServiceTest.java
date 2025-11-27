package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.services.CRUDServiceTest;

import java.time.LocalDate;
import java.util.function.Function;

class SemesterServiceTest extends CRUDServiceTest<Semester> {

    protected SemesterServiceTest() {
        super(DataSaveKeys.SEMESTERS, SemesterService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "SEM-2025-FALL";
    }

    @Override
    protected Semester getSampleObject() {
        return Semester.builder()
                .id("SEM-2025-FALL")
                .name("Fall Semester 2025")
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 12, 20))
                .academicYear(2024)
                .lessons(null)
                .build();
    }

    @Override
    protected void alterEntity(Semester semester) {
        semester.setId("SEM-2025-WINTER");
    }
}

