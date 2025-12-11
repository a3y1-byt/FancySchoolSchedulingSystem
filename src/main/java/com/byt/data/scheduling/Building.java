package com.byt.data.scheduling;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Building {
    String name;
    String address;
    String description;
    List<ClassRoom> classRooms;


    public static Building copy(Building building) {
        return Building.builder()
                .name(building.getName())
                .address(building.getAddress())
                .description(building.getDescription())
                .build();
    }
}
