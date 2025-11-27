package com.byt.scheduling;


import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Building {
    String id;
    String name;
    String address;
    String description;
    List<ClassRoom> classRooms;
    List<String> classRoomIds;

    public static Building copy(Building building, List<ClassRoom> classRooms) {
        return Building.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .description(building.getDescription())
                .classRoomIds(building.getClassRoomIds() != null
                        ? new ArrayList<>(building.getClassRoomIds())
                        : null)
                .classRooms(classRooms)
                .build();
    }

    public static Building copy(Building building) {
        return Building.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .description(building.getDescription())
                .classRoomIds(building.getClassRoomIds() != null
                        ? new ArrayList<>(building.getClassRoomIds())
                        : null)
                .classRooms(null)
                .build();
    }
}
