package com.byt.scheduling;

import com.byt.scheduling.enums.SubjectType;
import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class Subject {
    String id;
    String name;
    List<SubjectType> types;
    int hours;
    int ects;
    List<Lesson> lessons;
}
