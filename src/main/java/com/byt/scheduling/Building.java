package com.byt.scheduling;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Building {
    private String name;
    private String address;
    private String description;
    private List<ClassRoom> classRooms;
}
