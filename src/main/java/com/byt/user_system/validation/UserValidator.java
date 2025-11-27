package com.byt.user_system.validation;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

// General class for validating User.
public final class UserValidator {

    // I know there are some names and surnames more than 50, but let's be real
    private static final int NAME_MIN = 1;
    private static final int NAME_MAX = 50;

    // I found this regex Pattern - it's useful to validate strings
    // All methods for validation here are really easy and simple

    // here I only allow latin alphabet capital and lowercase letters.
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z]+$");

    // pretty simple email validation,
    // We are not checking whether domain is valid.
    private static final int EMAIL_MAX = 254;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // idk what lengths exist, so just average
    // we are not doing blanks and "( )"
    private static final int PHONE_MIN = 7;
    private static final int PHONE_MAX = 13;
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{7,13}$");

    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 100;

    private UserValidator() {
    }

    public static void validateUserFields(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email
    ) {
        validateUserFields(firstName, lastName, null, dateOfBirth, phoneNumber, email);
    }

    public static void validateUserFields(
            String firstName,
            String lastName,
            String familyName,     // nullable
            LocalDate dateOfBirth,
            String phoneNumber,
            String email
    ) {
        validateName("First name", firstName, true);
        validateName("Last name", lastName, true);
        validateName("Family name", familyName, false); // optional
        validateDateOfBirth(dateOfBirth);
        validatePhoneNumber(phoneNumber);
        validateEmail(email);
    }

    private static void validateName(String fieldName, String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new ValidationException(fieldName + " must not be empty");
            } else {
                return;
            }
        }

        String trimmed = value.trim();

        if (trimmed.length() < NAME_MIN || trimmed.length() > NAME_MAX) {
            throw new ValidationException(fieldName + " length must be between " +
                    NAME_MIN + " and " + NAME_MAX);
        }

        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(fieldName + " can contain only latin letters");
        }
    }

    private static void validateDateOfBirth(LocalDate date) {
        if (date == null) {
            throw new ValidationException("Date of birth must not be null");
        }

        LocalDate today = LocalDate.now();

        if (!date.isBefore(today)) {
            throw new ValidationException("Date of birth must be in past");
        }

        LocalDate minAllowed = today.minusYears(MIN_AGE);
        LocalDate maxAllowed = today.minusYears(MAX_AGE);

        if (date.isAfter(minAllowed)) {
            throw new ValidationException("User must be at least " + MIN_AGE);
        }
        if (date.isBefore(maxAllowed)) {
            throw new ValidationException("User age must be less than " + MAX_AGE);
        }
    }

    private static void validatePhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("Phone number must not be empty");
        }

        String trimmed = phone.trim();

        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                    "Phone number can contain only digits");
        }

        long digitsCount = trimmed.chars().filter(Character::isDigit).count();

        if (digitsCount < PHONE_MIN || digitsCount > PHONE_MAX) {
            throw new ValidationException(
                    "Phone number must be between " + PHONE_MIN + " and " + PHONE_MAX);
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email must not be empty");
        }

        String trimmed = email.trim();

        if (trimmed.length() > EMAIL_MAX) {
            throw new ValidationException("Email is too long");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Invalid email address");
        }
    }
}
