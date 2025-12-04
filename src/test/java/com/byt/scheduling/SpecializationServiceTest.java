package com.byt.scheduling;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;

class SpecializationServiceTest extends CRUDServiceTest<Specialization> {

    protected SpecializationServiceTest() {

        super(DataSaveKeys.SPECIALIZATIONS, SpecializationService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "SPEC-SE";
    }

    @Override
    protected Specialization getSampleObject() {
        return Specialization.builder()
                .id("SPEC-SE")
                .name("Software Engineering")
                .description("Specialization focused on software development and data science")
                .studyProgramId("SP-CS")
                .subjects(null)
                .build();
    }

    @Override
    protected void alterEntity(Specialization specialization) {
        specialization.setDescription(specialization.getDescription() + " - Updated");
    }
}
