package com.byt.validation.user_system;

import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

import static com.byt.data.user_system.FreeListener.MAX_NOTES_LENGTH;

public class FreeListenerValidator {

    private FreeListenerValidator() {}

    public static void validateFreeListener(FreeListener freeListener) {
        if (freeListener == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "FreeListener must not be null"
            );
        }

        validateFreeListener(
                freeListener.getFirstName(),
                freeListener.getLastName(),
                freeListener.getFamilyName(),
                freeListener.getDateOfBirth(),
                freeListener.getPhoneNumber(),
                freeListener.getEmail(),
                freeListener.getLanguagesOfStudies(),
                freeListener.getNotes()
        );
    }

    public static void validateFreeListener(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            Set<StudyLanguage> languagesOfStudies,
            String notes
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
                    "FreeListener must have at least one study language"
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

        if (notes != null) {
            String trimmed = notes.trim();

            if (trimmed.isEmpty()) {
                throw new ValidationException(
                        ExceptionCode.NOT_EMPTY_VIOLATION,
                        "Notes must not be empty"
                );
            }

            if (trimmed.length() > MAX_NOTES_LENGTH) {
                throw new ValidationException(
                        ExceptionCode.LENGTH_TOO_LONG,
                        "Notes are too long"
                );
            }
        }
    }

    public static void validateClass(FreeListener prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "FreeListener prototype must not be null"
            );
        }

        validateFreeListener(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getLanguagesOfStudies(),
                prototype.getNotes()
        );
    }
}
