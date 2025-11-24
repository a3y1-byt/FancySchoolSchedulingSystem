package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FreeListener extends Attendee {

    private String notes;

    public FreeListener(String firstName, String lastName,
                        String familyName, LocalDate dateOfBirth,
                        String phoneNumber, String email,
                        List<StudyLanguage> languagesOfStudies, String notes) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, languagesOfStudies);
        this.notes = notes;
    }

    public FreeListener() {
        super();
    }
}
