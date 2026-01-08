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
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Lesson> lessons = new HashSet<>();

    public static Semester copy(Semester semester) {
        var copy = Semester.builder()
                .name(semester.getName())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .academicYear(semester.getAcademicYear())
//                .lessons(new HashSet<>(semester.lessons))
                .build();

        copy.lessons = new HashSet<>(semester.lessons);
        return copy;
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
