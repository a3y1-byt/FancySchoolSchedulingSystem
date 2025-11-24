package com.byt.scheduling;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Group {
    private String name;
    private String language;
    private int maxCapacity;
    private int minCapacity;
    private int yearOfStudy;
    private List<String> notes;
    private List<Lesson> lessons;
}
