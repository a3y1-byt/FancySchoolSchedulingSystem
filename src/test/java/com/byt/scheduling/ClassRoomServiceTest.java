package com.byt.scheduling;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;

class ClassRoomServiceTest extends CRUDServiceTest<ClassRoom> {
    protected ClassRoomServiceTest() {
        super(DataSaveKeys.CLASSROOMS, ClassRoomService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "CR-101";
    }

    @Override
    protected ClassRoom getSampleObject() {
        return ClassRoom.builder()
                .id("CR-101").name("Lecture Hall").floor(1).capacity(100).buildingId("B-001")
                .build();
    }

    @Override
    protected void alterEntity(ClassRoom classRoom) {
        classRoom.setCapacity(classRoom.getCapacity() + 1);
    }
}