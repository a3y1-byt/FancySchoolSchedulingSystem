package com.byt.scheduling;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class Semester {
    String id;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    int academicYear;
    List<Lesson> lessons;

    public static Semester copy(Semester semester, List<Lesson> lessons) {
        return Semester.builder()
                .id(semester.getId())
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .academicYear(semester.getAcademicYear())
                .lessons(lessons)
                .build();
    }

    public static Semester copy(Semester semester) {
        return Semester.builder()
                .id(semester.getId())
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .academicYear(semester.getAcademicYear())
                .lessons(null)
                .build();
    }
}
