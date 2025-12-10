package com.byt.services.scheduling;

import com.byt.data.scheduling.Building;
import com.byt.data.scheduling.ClassRoom;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.Arrays;

@Nested
class BuildingServiceTest extends CRUDServiceTest<Building> {

    public BuildingServiceTest() {
        super(DataSaveKeys.BUILDINGS, BuildingService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "B-001";
    }

    @Override
    protected Building getSampleObject() {
        ClassRoom roomA = ClassRoom.builder()
                .id("CR-101").name("Lecture Hall").floor(1).capacity(100).buildingId("B-001")
                .build();
        ClassRoom roomB = ClassRoom.builder()
                .id("CR-102").name("Lecture Hall").floor(1).capacity(100).buildingId("B-001")
                .build();

        return  Building.builder()
                .id("B-001")
                .name("Main Academic Hall")
                .address("123 Warsaw")
                .description("Primary building for lectures.")
                .classRooms(new ArrayList<>())
                .classRoomIds(Arrays.asList(roomA.getId(), roomB.getId()))
                .build();
    }

    @Override
    protected void alterEntity(Building building) {
        building.setId(building.getId() + "sth");
    }
}
