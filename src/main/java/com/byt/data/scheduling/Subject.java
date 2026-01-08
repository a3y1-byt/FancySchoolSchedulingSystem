package com.byt.data.scheduling;

import com.byt.enums.scheduling.SubjectType;
import com.byt.validation.scheduling.Validator;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Subject {
    public static final int HOURS_PER_ECTS = 25;

    private String name;
    private int hours;

    private List<SubjectType> types;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Lesson> lessons = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Specialization> specializations = new HashSet<>();

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
                .lessons(new HashSet<>(subject.getLessons()))
                .specializations(new HashSet<>(subject.getSpecializations()))
                .build();
    }

    public Set<Lesson> getLessons() {
        return new HashSet<>(lessons);
    }

    public Set<Specialization> getSpecializations() {
        return new HashSet<>(specializations);
    }

    public void addSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (specializations.contains(specialization))
            return;

        specializations.add(specialization);
        specialization.addSubject(this);
    }

    public void removeSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (!specializations.contains(specialization))
            return;

        specializations.remove(specialization);
        specialization.removeSubject(this);
    }

    public void addLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (lessons.contains(lesson))
            return;

        lessons.add(lesson);
        lesson.addSubject(this);
    }

    public void removeLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (!lessons.contains(lesson))
            return;

        lessons.remove(lesson);
        lesson.removeSubject(this);
    }
}
