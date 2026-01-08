package com.byt.data.scheduling;



import static org.junit.jupiter.api.Assertions.*;

import com.byt.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class BuildingTest {

    private Building building;
    private ClassRoom classRoom1;
    private ClassRoom classRoom2;

    @BeforeEach
    void setUp() {
        building = Building.builder()
                .name("Main Building")
                .address("123 Main St")
                .description("Main academic building")
                .classRooms(new HashSet<>())
                .build();

        classRoom1 = ClassRoom.builder()
                .name("Room 101")
                .floor(1)
                .capacity(30)
                .lessons(new HashSet<>())
                .build();

        classRoom2 = ClassRoom.builder()
                .name("Room 202")
                .floor(2)
                .capacity(50)
                .lessons(new HashSet<>())
                .build();
    }

    @Test
    void shouldCreateBidirectionalConnectionWhenAddingClassRoom() {
        building.addClassRoom(classRoom1);

        assertTrue(building.getClassRooms().contains(classRoom1));
        assertEquals(building, classRoom1.getBuilding());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullClassRoom() {
        assertThrows(ValidationException.class, () -> building.addClassRoom(null));
    }

    @Test
    void shouldReturnEarlyWhenAddingDuplicateClassRoom() {
        building.addClassRoom(classRoom1);
        building.addClassRoom(classRoom1);

        assertEquals(1, building.getClassRooms().size());
    }

    @Test
    void shouldAddMultipleClassRooms() {
        building.addClassRoom(classRoom1);
        building.addClassRoom(classRoom2);

        assertEquals(2, building.getClassRooms().size());
        assertTrue(building.getClassRooms().contains(classRoom1));
        assertTrue(building.getClassRooms().contains(classRoom2));
        assertEquals(building, classRoom1.getBuilding());
        assertEquals(building, classRoom2.getBuilding());
    }

    @Test
    void shouldRemoveBidirectionalConnectionWhenRemovingClassRoom() {
        building.addClassRoom(classRoom1);
        building.addClassRoom(classRoom2);

        building.removeClassRoom(classRoom1);

        assertFalse(building.getClassRooms().contains(classRoom1));
        assertNull(classRoom1.getBuilding());
        assertTrue(building.getClassRooms().contains(classRoom2));
    }

    @Test
    void shouldThrowExceptionWhenRemovingNullClassRoom() {
        assertEquals(0, building.getClassRooms().size());
    }

    @Test
    void shouldReturnEarlyWhenRemovingNonExistentClassRoom() {
        building.addClassRoom(classRoom1);

        assertDoesNotThrow(() -> building.removeClassRoom(classRoom2));
        assertEquals(1, building.getClassRooms().size());
    }
}