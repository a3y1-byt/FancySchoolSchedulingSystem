package com.byt.data.scheduling;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ClassRoom {
    String name;
    int floor;
    int capacity;

    public static ClassRoom copy(ClassRoom original) {
        return ClassRoom.builder()
                .name(original.getName())
                .floor(original.getFloor())
                .capacity(original.getCapacity())
                .build();
    }
}
