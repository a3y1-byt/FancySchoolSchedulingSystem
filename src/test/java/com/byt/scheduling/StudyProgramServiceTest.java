package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.enums.StudyProgramLevel;
import com.byt.services.CRUDService;
import com.byt.services.CRUDServiceTest;

import java.util.function.Function;

class StudyProgramServiceTest extends CRUDServiceTest<StudyProgram> {

    protected StudyProgramServiceTest() {
        super(DataSaveKeys.STUDY_PROGRAMS, StudyProgramService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "SP-CS";
    }

    @Override
    protected StudyProgram getSampleObject() {
        return StudyProgram.builder()
                .id("SP-CS")
                .name("Computer Science")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(null)
                .build();
    }

    @Override
    protected void alterEntity(StudyProgram studyProgram) {
        studyProgram.setId(studyProgram.getId() + "sth");
    }
}
