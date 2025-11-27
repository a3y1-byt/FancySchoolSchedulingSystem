package com.byt.scheduling;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;

import java.util.ArrayList;
import java.util.Arrays;



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
                .language("English")
                .maxCapacity(25)
                .minCapacity(15)
                .yearOfStudy(3)
                .notes(new ArrayList<>())
                .lessons(new ArrayList<>())
                .students(new ArrayList<>())
                .build();
    }

    @Override
    protected void alterEntity(Group group) {
        group.setLanguage("English");
    }
}