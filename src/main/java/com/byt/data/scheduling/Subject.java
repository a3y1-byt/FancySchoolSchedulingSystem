package com.byt.data.scheduling;

import com.byt.enums.scheduling.SubjectType;
import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Subject {
    public static final int HOURS_PER_ECTS = 25;
    String name;
    int hours;
    List<SubjectType> types;
    List<Lesson> lessons;

    public int getEcts() {
        return hours / HOURS_PER_ECTS;
    }

    public static Subject copy(Subject subject) {
        return Subject.builder()
                .name(subject.getName())
                .types(subject.getTypes() != null
                        ? new ArrayList<>(subject.getTypes())
                        : new ArrayList<>())
                .hours(subject.getHours())
                .build();
    }
}
