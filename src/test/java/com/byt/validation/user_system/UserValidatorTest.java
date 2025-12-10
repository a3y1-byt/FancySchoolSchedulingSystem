package com.byt.validation.user_system;

import com.byt.validation.user_system.UserValidator;
import com.byt.validation.user_system.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    private static final LocalDate TODAY = LocalDate.now();

    private static LocalDate validDob() {
        return TODAY.minusYears(25);
    }

    // valid user fields
    @Test
    public void validUserFields() {
        assertDoesNotThrow(() ->
                UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        "Mykhailivna",
                        validDob(),
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // first name = null
    @Test
    public void nullFirstName() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        null,
                        "Hnatiuk",
                        "Mykhailivna",
                        validDob(),
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("first name"));
    }

    // last name = ""
    @Test
    public void blankLastName() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "   ",
                        "Mykhailivna",
                        validDob(),
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
        assertTrue(ex.getMessage().toLowerCase().contains("last name"));
    }

    // family name = null
    @Test
    public void familyNameCanBeNull() {
        assertDoesNotThrow(() ->
                UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // first name contains ukrainian characters
    @Test
    public void nameWithNonLatinCharacters() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Юмі",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // DoB = null
    @Test
    public void nullDateOfBirth() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        null,
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // DoB is in future
    @Test
    public void dateOfBirthInFuture() {
        LocalDate future = TODAY.plusDays(1);
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        future,
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // user is too young
    @Test
    public void tooYoungUser() {
        LocalDate tooYoung = TODAY.minusYears(10);

        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        tooYoung,
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // user is too old
    @Test
    public void tooOldUser() {
        LocalDate tooOld = TODAY.minusYears(141);

        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        tooOld,
                        "48505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // phone = null
    @Test
    public void nullPhone() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        null,
                        "yumipies@gmail.com"
                )
        );
    }

    // phone contains letters
    @Test
    public void phoneWithLetters() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505hellow5",
                        "yumipies@gmail.com"
                )
        );
    }

    // phone is too short
    @Test
    public void tooShortPhone() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "4850",
                        "yumipies@gmail.com"
                )
        );
    }

    // phone is too long
    @Test
    public void tooLongPhone() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505505505505505",
                        "yumipies@gmail.com"
                )
        );
    }

    // phone is " "
    @Test
    public void blankPhone() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        " ",
                        "yumipies@gmail.com"
                )
        );
    }

    // email = null
    @Test
    public void nullEmail() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        null
                )
        );
    }

    // email = " "
    @Test
    public void blankEmail() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        " "
                )
        );
    }

    // email format is wrong
    @Test
    public void invalidEmailFormat() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        "yumipies_gmail.com"
                )
        );
    }

    // email format is wrong
    @Test
    public void emailWithoutDomain() {
        assertThrows(
                ValidationException.class,
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        "yumipies@"
                )
        );
    }

    // email is valid w a lot of chars
    @Test
    public void validEmail() {
        assertDoesNotThrow(
                () -> UserValidator.validateUserFields(
                        "Yumi",
                        "Hnatiuk",
                        null,
                        validDob(),
                        "48505505505",
                        "yumi.pies_31072023@gmail.com"
                )
        );
    }
}
