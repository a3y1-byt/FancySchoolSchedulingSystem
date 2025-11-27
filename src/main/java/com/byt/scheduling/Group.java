package com.byt.scheduling;

import com.byt.user_system.Student;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class Group {
    String id;
    String name;
    String language;
    int maxCapacity;
    int minCapacity;
    int yearOfStudy;
    List<String> notes;
    List<Lesson> lessons;
    List<Student> students;

    public static Group copy(Group group, List<Lesson> lessons, List<Student> students) {
        return Group.builder()
                .id(group.getId())
                .name(group.getName())
                .language(group.getLanguage())
                .maxCapacity(group.getMaxCapacity())
                .minCapacity(group.getMinCapacity())
                .yearOfStudy(group.getYearOfStudy())
                .notes(group.getNotes() != null
                        ? new ArrayList<>(group.getNotes())
                        : new ArrayList<>())
                .lessons(lessons)
                .students(students)
                .build();
    }

    public static Group copy(Group group) {
        return Group.builder()
                .id(group.getId())
                .name(group.getName())
                .language(group.getLanguage())
                .maxCapacity(group.getMaxCapacity())
                .minCapacity(group.getMinCapacity())
                .yearOfStudy(group.getYearOfStudy())
                .notes(group.getNotes() != null
                        ? new ArrayList<>(group.getNotes())
                        : new ArrayList<>())
                .lessons(null)
                .students(null)
                .build();
    }
}
