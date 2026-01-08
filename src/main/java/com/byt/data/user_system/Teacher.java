package com.byt.data.user_system;

import com.byt.data.scheduling.Lesson;
import com.byt.validation.scheduling.Validator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"lessons"})
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class Teacher extends Staff {

    private String title;
    private String position;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Lesson> lessons = new HashSet<>();

    public Teacher(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   LocalDate hireDate, String title,
                   String position) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, hireDate);
        this.title = title;
        this.position = position;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Set<Lesson> getLessons() {
        return new HashSet<>(lessons);
    }

    public void addLesson(Lesson lesson) {
        Validator.validateLesson(lesson);
        if (lessons.contains(lesson)) return;

        lessons.add(lesson);
        lesson.addTeacher(this);
    }

    public void removeLesson(Lesson lesson) {
        Validator.validateLesson(lesson);
        if (!lessons.contains(lesson)) return;

        lessons.remove(lesson);
        lesson.removeTeacher(this);
    }

    public static Teacher copy(Teacher teacher) {
        if (teacher == null) return null;

        Teacher t = new Teacher(
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getFamilyName(),
                teacher.getDateOfBirth(),
                teacher.getPhoneNumber(),
                teacher.getEmail(),
                teacher.getHireDate(),
                teacher.getTitle(),
                teacher.getPosition()
        );

        t.lessons = new HashSet<>(teacher.lessons);
        return t;
    }
}
