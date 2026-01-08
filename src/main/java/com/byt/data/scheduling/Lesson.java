package com.byt.data.scheduling;

import com.byt.data.user_system.Teacher;
import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.TeacherValidator;
import lombok.*;

import java.time.LocalTime;
import java.util.HashSet;

@Data
@Builder
public class Lesson {
    String name;
    LessonType type;
    LessonMode mode;
    String note;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    StudyLanguage language;
    WeekPattern weekPattern;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    Subject subject;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    Group group;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    Teacher teacher;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    HashSet<ClassRoom> classRooms = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    HashSet<Semester> semesters = new HashSet<>();

    public void addSubject(Subject subject) {
        if(this.subject == subject) return;
        Validator.validateSubject(subject);

        if(this.subject != null){
            Subject oldSubject = this.subject;
            this.subject = null;
            oldSubject.removeLesson(this);
        }

        this.subject = subject;
        subject.addLesson(this);
    }

    public void removeSubject(Subject subject) {
        if(this.subject == null || !this.subject.equals(subject)) return;

        this.subject = null;
        subject.removeLesson(this);
    }

    public void addGroup(Group group) {
        if(this.group == group) return;
        Validator.validateGroup(group);

        if(this.group != null){
            Group oldGroup = this.group;
            this.group = null;
            oldGroup.removeLesson(this);
        }

        this.group = group;
         group.addLesson(this);
    }
    public void removeGroup(Group group) {
        if(this.group == null || !this.group.equals(group)) return;

        this.group = null;
        group.removeLesson(this);
    }

    public void addTeacher(Teacher teacher) {
        TeacherValidator.validateTeacher(teacher);
        this.teacher = teacher;
        //TODO
//         teacher.addLesson(this);
    }
    public void removeTeacher(Teacher teacher) {
        if(!this.teacher.equals(teacher)) return;
        this.teacher = null;
        //TODO
        // teacher.removeLesson(this);
    }

    public void addSemester(Semester semester) {
       Validator.validateSemester(semester);

        semesters.add(semester);
        semester.addLesson(this);
    }

    public void removeSemester(Semester semester) {
        if(!this.semesters.contains(semester)) return;

        this.semesters.remove(semester);
        semester.removeLesson(this);
    }

    public void addClassRoom(ClassRoom classRoom) {
        Validator.validateClassRoom(classRoom);

        if (classRooms.contains(classRoom)) return;

        classRooms.add(classRoom);
        classRoom.addLesson(this);
    }

    public void removeClassRoom(ClassRoom classRoom) {
        if (!classRooms.contains(classRoom)) return;

        classRooms.remove(classRoom);
        classRoom.removeLesson(this);
    }

    public HashSet<ClassRoom> getClassRooms() {
        return new HashSet<>(classRooms);
    }

    public HashSet<Semester> getSemesters() {
        return new HashSet<>(semesters);
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Group getGroup() {
        return group;
    }

    public static Lesson copy(Lesson lesson) {
        return Lesson.builder()
                .name(lesson.getName())
                .type(lesson.getType())
                .mode(lesson.getMode())
                .note(lesson.getNote())
                .dayOfWeek(lesson.getDayOfWeek())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .language(lesson.getLanguage())
                .weekPattern(lesson.getWeekPattern())
                .build();
    }
}
