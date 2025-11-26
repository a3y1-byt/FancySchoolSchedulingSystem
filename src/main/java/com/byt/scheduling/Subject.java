package com.byt.scheduling;

import com.byt.scheduling.enums.SubjectType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Subject {
    private  List<SubjectType> type;
    private List<Lesson> lessons;//todo: reverse relationship
    private int hours;
    private int ects;
}
