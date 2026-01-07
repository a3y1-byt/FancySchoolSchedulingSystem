package com.byt.validation.user_system;

import com.byt.data.user_system.Admin;
import com.byt.exception.ExceptionCode;
import com.byt.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminValidator {

    private AdminValidator() {
    }

    public static void validateAdmin(Admin admin) {
        if (admin == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Admin must not be null"
            );
        }

        validateAdmin(
                admin.getFirstName(),
                admin.getLastName(),
                admin.getFamilyName(),
                admin.getDateOfBirth(),
                admin.getPhoneNumber(),
                admin.getEmail(),
                admin.getHireDate(),
                admin.getLastLoginTime()
        );
    }

    // VALIDATION METHODS
    public static void validateAdmin(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            LocalDate hireDate,
            LocalDateTime lastLoginTime
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

        //  only Admin validation
        if (hireDate == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Hire date must not be null"
            );
        }

        LocalDate today = LocalDate.now();
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

        if (lastLoginTime != null) {
            LocalDateTime now = LocalDateTime.now();

            if (lastLoginTime.isAfter(now)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Last login time cannot be in the future"
                );
            }

            LocalDateTime hireStartLocalDateTime = hireDate.atStartOfDay(java.time.ZoneOffset.UTC).toLocalDateTime();
            if (lastLoginTime.isBefore(hireStartLocalDateTime)) {
                throw new ValidationException(
                        ExceptionCode.VALUE_OUT_OF_RANGE,
                        "Last login time cannot be before hire date"
                );
            }
        }
    }

    public static void validateClass(Admin prototype) {
        if (prototype == null) {
            throw new ValidationException(
                    ExceptionCode.NOT_NULL_VIOLATION,
                    "Admin prototype must not be null"
            );
        }

        validateAdmin(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getHireDate(),
                prototype.getLastLoginTime()
        );
    }
}
