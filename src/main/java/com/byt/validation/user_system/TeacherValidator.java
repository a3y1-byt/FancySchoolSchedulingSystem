package com.byt.validation.user_system;

import com.byt.data.user_system.Teacher;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.time.LocalDate;

public class TeacherValidator {

    public TeacherValidator() {}

    public static void validateTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Teacher must not be null"
            );
        }

        validateTeacher(
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
    }

    // VALIDATION METHODS
    public static void validateTeacher(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            LocalDate hireDate,
            String title,
            String position
    ) {
        // general USER class validation
        UserValidator.validateUserFields(
                firstName,
                lastName,
                familyName,
                dateOfBirth,
                phoneNumber,
                email
        );

        //  only Teacher validation
        if (hireDate == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Hire date must not be null"
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate earliest_hire_date = LocalDate.of(2000, 1, 1);
        int min_age_at_hire = 18;

        if (dateOfBirth != null) {
            LocalDate minHireDateByDob = dateOfBirth.plusYears(min_age_at_hire);
            if (hireDate.isBefore(minHireDateByDob)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Person must be at least " + min_age_at_hire + " years old at hire date"
                );
            }
        }

        if (hireDate.isAfter(today)) {
            throw new ValidationException(
                    ExceptionCode.VALUE_OUT_OF_RANGE,
                    "Hire date must not be in the future"
            );
        }

        if (dateOfBirth != null && hireDate.isBefore(dateOfBirth)) {
            throw new ValidationException(
                    ExceptionCode.VALUE_OUT_OF_RANGE,
                    "Hire date cannot be before date of birth"
            );
        }

        if (title == null || title.isBlank()) {
            throw new ValidationException(
                    ExceptionCode.NOT_EMPTY_VIOLATION,
                    "Title must not be empty"
            );
        }

        if (position == null || position.isBlank()) {
            throw new ValidationException(
                    ExceptionCode.NOT_EMPTY_VIOLATION,
                    "Position must not be empty"
            );
        }
    }

    public static void validateClass(Teacher prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Teacher prototype must not be null"
            );
        }

        validateTeacher(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getHireDate(),
                prototype.getTitle(),
                prototype.getPosition()
        );
    }

}
