package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.enums.SubjectType;
import com.byt.services.CRUDService;
import com.byt.services.CRUDServiceTest;

import java.util.Arrays;
import java.util.function.Function;

class SubjectServiceTest extends CRUDServiceTest<Subject> {

    protected SubjectServiceTest() {
        super(DataSaveKeys.SUBJECTS, SubjectService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "SUB-CS101";
    }

    @Override
    protected Subject getSampleObject() {
        return Subject.builder()
                .id("SUB-CS101")
                .name("Introduction to Programming")
                .types(Arrays.asList(SubjectType.NORMAL_SUBJECT, SubjectType.EXAM_SUBJECT))
                .hours(60)
                .ects(6)
                .specializationId("SPEC-SE")
                .lessons(null)
                .build();
    }

    @Override
    protected void alterEntity(Subject subject) {
        subject.setId(subject.getId() + "sth");
    }
}