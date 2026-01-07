package com.byt.validation.user_system;

import com.byt.data.user_system.Student;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

public class StudentValidator {

    public StudentValidator() {}


    // VALIDATION METHODS
    public static void validateStudent(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            List<StudyLanguage> languagesOfStudies,
            StudyStatus studiesStatus
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

        //  only Student validation
        if (languagesOfStudies == null || languagesOfStudies.isEmpty()) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Student must have at least one study language"
            );
        }

        // null and duplicate check
        for (int i = 0; i < languagesOfStudies.size(); i++) {
            StudyLanguage sl = languagesOfStudies.get(i);

            if (sl == null) {
                throw new ValidationException(
                        ExceptionCode.NOT_NULL_VIOLATION,
                        "Study language must not be null"
                );
            }

            for (int j = i + 1; j < languagesOfStudies.size(); j++) {
                if (sl == languagesOfStudies.get(j)) { // enum -> можна порівнювати через ==
                    throw new ValidationException(
                            ExceptionCode.INVALID_FORMAT,
                            "Study languages must be unique"
                    );
                }
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
