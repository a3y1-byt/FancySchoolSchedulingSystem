package com.byt.scheduling;

import lombok.Value;
import lombok.Builder;

@Value
@Builder
public class ClassRoom {
    String id;
    String name;
    int floor;
    int capacity;
    String buildingId;
}
