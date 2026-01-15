package com.byt.data.scheduling;

import com.byt.data.user_system.Student;
import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.StudentValidator;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Specialization {
    private String name;
    private String description;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<StudyProgram> studyPrograms = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    public static Specialization copy(Specialization specialization) {
        var copy = Specialization.builder()
                .name(specialization.getName())
                .description(specialization.getDescription())
                .build();

        copy.subjects = new HashSet<>(specialization.getSubjects());
        copy.studyPrograms = new HashSet<>(specialization.getStudyPrograms());
        copy.students = new HashSet<>(specialization.getStudents());
        return copy;
    }

    public Set<Subject> getSubjects() {
        return new HashSet<>(subjects);
    }

    public Set<StudyProgram> getStudyPrograms() {
        return new HashSet<>(studyPrograms);
    }

    public Set<Student> getStudents() {
        return new HashSet<>(students);
    }

    public void addStudyProgram(StudyProgram studyProgram) {
        Validator.validateStudyProgram(studyProgram);

        if (studyPrograms.contains(studyProgram))
            return;

        studyPrograms.add(studyProgram);
        studyProgram.addSpecialization(this);
    }

    public void removeStudyProgram(StudyProgram studyProgram) {
        Validator.validateStudyProgram(studyProgram);

        if (!studyPrograms.contains(studyProgram))
            return;

        studyPrograms.remove(studyProgram);
        studyProgram.removeSpecialization(this);
    }

    public void addStudent(Student student) {
        StudentValidator.validateClass(student);

        if (students.contains(student))
            return;

        students.add(student);
        student.addSpecialization(this);
    }

    public void removeStudent(Student student) {
        StudentValidator.validateClass(student);

        if (!students.contains(student))
            return;

        students.remove(student);
        student.removeSpecialization(this);
    }

    public void addSubject(Subject subject) {
        Validator.validateSubject(subject);

        if (subjects.contains(subject))
            return;

        subjects.add(subject);
        subject.addSpecialization(this);
    }

    public void removeSubject(Subject subject) {
        Validator.validateSubject(subject);
        System.out.println("Removing subject: " + subject.getName());
        if (!subjects.contains(subject))
            return;

        subjects.remove(subject);
        subject.removeSpecialization(this);
    }
}
