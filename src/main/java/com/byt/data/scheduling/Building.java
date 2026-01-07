package com.byt.data.scheduling;

import com.byt.validation.scheduling.Validator;
import lombok.*;

import java.util.HashSet;

@Data
@Builder
public class Building {
    String name;
    String address;
    String description;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    HashSet<ClassRoom> classRooms;

    public void addClassRoom(ClassRoom classRoom) {
        Validator.validateClassRoom(classRoom);

        if (classRooms.contains(classRoom)) return;

        classRooms.add(classRoom);
        classRoom.addBuilding(this);
    }

    public void removeClassRoom(ClassRoom classRoom) {
        if(!classRooms.contains(classRoom)) return;

        classRooms.remove(classRoom);
        classRoom.removeBuilding(this);
    }

    public HashSet<ClassRoom> getClassRooms() {
        return new HashSet<>(classRooms);
    }

    public static Building copy(Building building) {
        return Building.builder()
                .name(building.getName())
                .address(building.getAddress())
                .description(building.getDescription())
                .build();
    }
}
