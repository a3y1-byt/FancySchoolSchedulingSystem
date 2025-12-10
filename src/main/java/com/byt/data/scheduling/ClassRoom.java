package com.byt.data.scheduling;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ClassRoom {
    String id;
    String name;
    int floor;
    int capacity;
    String buildingId;

    public static ClassRoom copy(ClassRoom original) {
        return ClassRoom.builder()
                .id(original.getId())
                .name(original.getName())
                .floor(original.getFloor())
                .capacity(original.getCapacity())
                .buildingId(original.getBuildingId())
                .build();
    }
}
