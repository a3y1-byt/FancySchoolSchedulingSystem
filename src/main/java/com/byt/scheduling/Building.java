package com.byt.scheduling;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Building {
    String id;
    String name;
    String address;
    String description;
    List<ClassRoom> classRooms;
}
