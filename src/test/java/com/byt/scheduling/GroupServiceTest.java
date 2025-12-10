package com.byt.scheduling;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.enums.user_system.StudyLanguage;



class GroupServiceTest extends CRUDServiceTest<Group> {

    protected GroupServiceTest() {
        super(DataSaveKeys.GROUPS, GroupService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "G-2025";
    }

    @Override
    protected Group getSampleObject() {
        return Group.builder()
                .id("G-2025")
                .name("G-2025-Fall")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(25)
                .minCapacity(15)
                .yearOfStudy(3)
                .notes(null)
                .lessons(null)
                .students(null)
                .build();
    }

    @Override
    protected void alterEntity(Group group) {
        group.setId("G-2027");
    }
}