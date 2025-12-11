package com.byt.services.scheduling;
import com.byt.data.scheduling.Building;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;

import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BuildingServiceTest extends CRUDServiceTest<Building> {

    protected BuildingServiceTest() {
        super(DataSaveKeys.BUILDINGS, BuildingService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "Main Academic Hall";
    }

    @Override
    protected Building getSampleObject() {
        return Building.builder()
                .name("Main Academic Hall")
                .address("123 Warsaw")
                .description("Primary building for lectures.")
                .build();
    }

    @Override
    protected void alterEntity(Building building) {
        building.setName(building.getName() + "X");
    }

    @Test
    void testCreateStoresNewEntity() throws IOException {
        BuildingService service = (BuildingService) emptyService;
        Building building = Building.builder()
                .name("Created Building")
                .address("Created Address")
                .description("Created description")
                .build();
        service.initialize();
        service.create(building);
        Optional<Building> loaded = service.get(building.getName());
        assertTrue(loaded.isPresent());
    }

    @Test
    void testCreateThrowsOnNull() {
        BuildingService service = (BuildingService) emptyService;
        assertThrows(ValidationException.class, () -> service.create(null));
    }

    @Test
    void testUpdateChangesExistingEntity() throws IOException, ValidationException {
        BuildingService service = (BuildingService) serviceWithData;
        Building updated = getSampleObject();
        updated.setName("Updated Name");
        service.update(getSampleObjectId(), updated);
        Optional<Building> loaded = service.get("Updated Name");
        assertTrue(loaded.isPresent());
        assertEquals("Updated Name", loaded.get().getName());
    }


    @Test
    void testUpdateThrowsOnNonExistentId() throws IOException {
        BuildingService service = (BuildingService) emptyService;
        service.initialize();
        Building updated = getSampleObject();
        assertThrows(IllegalArgumentException.class, () -> service.update("NON-EXISTENT", updated));
    }

    @Test
    void testUpdateThrowsOnNullPrototype() {
        BuildingService service = (BuildingService) serviceWithData;
        assertThrows(ValidationException.class, () -> service.update(getSampleObjectId(), null));
    }


    @Test
    void testDeleteRemovesExistingEntity() throws IOException {
        BuildingService service = (BuildingService) serviceWithData;
        service.delete(getSampleObjectId());
        Optional<Building> loaded = service.get(getSampleObjectId());
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testDeleteThrowsOnNonExistentId() throws IOException {
        BuildingService service = (BuildingService) emptyService;
        service.initialize();
        assertThrows(IllegalArgumentException.class, () -> service.delete("NON-EXISTENT"));
    }
}
