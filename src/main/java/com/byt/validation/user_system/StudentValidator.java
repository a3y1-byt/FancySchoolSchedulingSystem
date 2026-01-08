package com.byt.validation.user_system;

import com.byt.data.user_system.Student;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

public class StudentValidator {

    private StudentValidator() {}

    public static void validateStudent(Student student) {
        if (student == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Student must not be null"
            );
        }

        validateStudent(
                student.getFirstName(),
                student.getLastName(),
                student.getFamilyName(),
                student.getDateOfBirth(),
                student.getPhoneNumber(),
                student.getEmail(),
                student.getLanguagesOfStudies(),
                student.getStudiesStatus()
        );
    }

    public static void validateStudent(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            Set<StudyLanguage> languagesOfStudies,
            StudyStatus studiesStatus
    ) {
        UserValidator.validateUserFields(
                firstName,
                lastName,
                familyName,
                dateOfBirth,
                phoneNumber,
                email
        );

        if (languagesOfStudies == null || languagesOfStudies.isEmpty()) {
            throw new ValidationException(
                    ExceptionCode.NOT_EMPTY_VIOLATION,
                    "Student must have at least one study language"
            );
        }

        for (StudyLanguage sl : languagesOfStudies) {
            if (sl == null) {
                throw new ValidationException(
                        ExceptionCode.NOT_NULL_VIOLATION,
                        "Study language must not be null"
                );
            }
        }

        if (studiesStatus == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Study status must not be null"
            );
        }
    }

    public static void validateClass(Student prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Student prototype must not be null"
            );
        }

        validateStudent(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getLanguagesOfStudies(),
                prototype.getStudiesStatus()
        );
    }
}
