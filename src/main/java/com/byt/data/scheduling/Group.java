package com.byt.data.scheduling;

import com.byt.data.user_system.FreeListener;
import com.byt.data.user_system.Student;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;
import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.FreeListenerValidator;
import com.byt.validation.user_system.StudentValidator;
import com.byt.validation.user_system.UserValidator;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
public class Group {
    public static final int MAX_CAPACITY = 20;
    String name;
    StudyLanguage language;
    int maxCapacity;
    int yearOfStudy;
    List<String> notes;

    public static Group copy(Group group) {
        return Group.builder()
                .name(group.getName())
                .language(group.getLanguage())
                .maxCapacity(group.getMaxCapacity())
                .yearOfStudy(group.getYearOfStudy())
                .notes(group.getNotes())
                .build();
    }

    // GROUP -------- STUDENT
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Set<Student> students = new HashSet<>();

    public Set<Student> getStudents() {
        return Set.copyOf(students);
    }

    public void addStudent(Student student) {
        StudentValidator.validateStudent(student);

        if (students.contains(student))
            return;

        students.add(student);
        student.addGroup(this);
    }

    public void removeStudent(Student student) {
        StudentValidator.validateStudent(student);

        if (!students.contains(student)) {
            return;
        }

        students.remove(student);
        student.removeGroup(this);
    }

    // GROUP -------- LESSON
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Set<Lesson> lessons = new HashSet<>();

    public Set<Lesson> getLessons() {
        return new HashSet<>(lessons);
    }

    public void addLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (lessons.contains(lesson))
            return;

        if (lesson.getGroup() != null && lesson.getGroup() != this) {
            throw new ValidationException(
                    ExceptionCode.MULTIPLICITY_VIOLATION,
                    "Lesson already belongs to another group"
            );
        }

        lessons.add(lesson);
        lesson.addGroup(this);
    }

    public void removeLesson(Lesson lesson) {
        Validator.validateLesson(lesson);

        if (!lessons.contains(lesson)) {
            return;
        }

        lessons.remove(lesson);
        if (lesson.getGroup() == this) {
            lesson.removeGroup(this);
        }
    }

    // GROUP -------- LESSON
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Set<FreeListener> freeListeners = new HashSet<>();

    public Set<FreeListener> getFreeListeners() {
        return new HashSet<>(freeListeners);
    }


    public void addFreeListener(FreeListener freeListener) {
        FreeListenerValidator.validateFreeListener(freeListener);

        if (freeListeners.contains(freeListener))
            return;

        freeListeners.add(freeListener);
        freeListener.addGroup(this);
    }

    public void removeFreeListener(FreeListener freeListener) {
        FreeListenerValidator.validateFreeListener(freeListener);

        if (!freeListeners.contains(freeListener)) {
            return;
        }

        freeListeners.remove(freeListener);
        freeListener.removeGroup(this);
    }
}