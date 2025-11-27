package com.byt.scheduling;

import com.byt.scheduling.enums.SubjectType;
import lombok.Builder;
import lombok.Value;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class Subject {
    String id;
    String name;
    List<SubjectType> types;
    int hours;
    int ects;
    String specializationId;
    List<Lesson> lessons;

    public static Subject copy(Subject subject, List<Lesson> lessons) {
        return Subject.builder()
                .id(subject.getId())
                .name(subject.getName())
                .types(subject.getTypes() != null
                        ? new ArrayList<>(subject.getTypes())
                        : new ArrayList<>())
                .hours(subject.getHours())
                .ects(subject.getEcts())
                .specializationId(subject.getSpecializationId())
                .lessons(lessons)
                .build();
    }

    public static Subject copy(Subject subject) {
        return Subject.builder()
                .id(subject.getId())
                .name(subject.getName())
                .types(subject.getTypes() != null
                        ? new ArrayList<>(subject.getTypes())
                        : new ArrayList<>())
                .hours(subject.getHours())
                .ects(subject.getEcts())
                .specializationId(subject.getSpecializationId())
                .lessons(null)
                .build();
    }
}
