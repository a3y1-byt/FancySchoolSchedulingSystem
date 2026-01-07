package com.byt.validation.scheduling;

import com.byt.data.scheduling.*;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

public class Validator {

    public static void validateLesson(Lesson lesson) {
        notNull(lesson);
        notEmpty(lesson.getName());
        notNull(lesson.getType());
        notNull(lesson.getMode());
        notNull(lesson.getStartTime());
        notNull(lesson.getEndTime());
        notNull(lesson.getLanguage());
        notNull(lesson.getDayOfWeek());
        notEmpty(lesson.getNote(), true);
    }

    public static void validateBuilding(Building building) throws ValidationException {
        Validator.notNull(building);
        Validator.notEmpty(building.getName());
        Validator.notEmpty(building.getAddress());
        Validator.notEmpty(building.getDescription(), true);
    }

    public static void validateClassRoom(ClassRoom classRoom) throws ValidationException {
        Validator.notNull(classRoom);
        Validator.notEmpty(classRoom.getName());
        Validator.notNull(classRoom.getFloor());
        Validator.checkMax(classRoom.getFloor(), 30);
    }


    public static void validateGroup(Group group) throws ValidationException {
        Validator.notNull(group);
        Validator.notEmpty(group.getName());
        Validator.checkMax(group.getMaxCapacity(), Group.MAX_CAPACITY);
        Validator.notNull(group.getLanguage());
        Validator.notNull(group.getYearOfStudy());
        if(group.getNotes() != null){
            group.getNotes().forEach(Validator::notEmpty);
        }
    }

    public static void validateSemester(Semester semester) throws ValidationException {
        Validator.notNull(semester);
        Validator.notEmpty(semester.getName());
        Validator.notNull(semester.getStartDate());
        Validator.notNull(semester.getEndDate());
        Validator.notNull(semester.getAcademicYear());
    }

    public static void validateSpecialization(Specialization specialization) {
        Validator.notNull(specialization);
        Validator.notEmpty(specialization.getName());
        Validator.notEmpty(specialization.getDescription(), true);
    }

    public static void validateStudyProgram(StudyProgram program) throws ValidationException {
        Validator.notNull(program);
        Validator.notEmpty(program.getName());
        Validator.notNull(program.getLevel());
    }

    public static void validateSubject(Subject subject) {
        Validator.notNull(subject);
        Validator.notEmpty(subject.getName());
        Validator.notNull(subject.getHours());
        Validator.checkMin(subject.getEcts(), 1);

        if(subject.getTypes() != null)
            subject.getTypes().forEach(Validator::notNull);
    }

    public static void notNull(Object object) throws ValidationException {
        if (object == null) {
            throw new ValidationException(ExceptionCode.NOT_NULL_VIOLATION);
        }
    }

    public static void notEmpty(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(ExceptionCode.NOT_EMPTY_VIOLATION);
        }
    }

    public static void notEmpty(String value, boolean isOptional) throws ValidationException {
        if(isOptional && value == null) return;
        if (value.trim().isEmpty()) {
            throw new ValidationException(ExceptionCode.NOT_EMPTY_VIOLATION);
        }
    }

    public static void checkMin(int value, int min) throws ValidationException {
        if (value < min) {
            throw new ValidationException(ExceptionCode.MIN_VALUE_VIOLATION);
        }
    }

    public static void checkMax(int value, int max) throws ValidationException {
        if (value > max) {
            throw new ValidationException(ExceptionCode.MAX_VALUE_VIOLATION);
        }
    }


    public static void notNullArgument(Object object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("Cannot pass null value!");
        }
    }

    public static void notEmptyArgument(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot pass empty value!");
        }
    }
}
