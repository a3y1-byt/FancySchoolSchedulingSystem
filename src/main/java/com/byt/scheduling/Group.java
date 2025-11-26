package com.byt.scheduling;

import com.byt.user_system.Student;
import lombok.Builder;
import lombok.Value;

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
}