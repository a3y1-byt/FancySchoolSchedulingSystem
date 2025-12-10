package com.byt.services.scheduling;

import com.byt.data.scheduling.StudyProgram;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.services.CRUDServiceTest;

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
