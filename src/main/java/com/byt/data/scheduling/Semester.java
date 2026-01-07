package com.byt.data.scheduling;

import com.byt.validation.scheduling.Validator;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Semester {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int academicYear;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Lesson> lessons = new HashSet<>();

    public static Semester copy(Semester semester) {
        return Semester.builder()
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .academicYear(semester.getAcademicYear())
                .build();
    }

    public Set<Lesson> getLessons() {
        return new HashSet<>(lessons);
    }

    public void addLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (lessons.contains(lesson))
            return;

        lessons.add(lesson);
        lesson.addSemester(this);
    }

    public void removeLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (!lessons.contains(lesson))
            return;

        lessons.remove(lesson);
        lesson.removeSemester(this);
    }
}
