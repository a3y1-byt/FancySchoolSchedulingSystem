package com.byt.data.scheduling;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Semester {
    String name;
    LocalDate startDate;
    LocalDate endDate;
    int academicYear;
    List<Lesson> lessons;

    public static Semester copy(Semester semester) {
        return Semester.builder()
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .academicYear(semester.getAcademicYear())
                .build();
    }
}
