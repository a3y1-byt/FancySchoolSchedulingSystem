package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Attendee extends User {

    @Getter
    @Setter
    private List<StudyLanguage> languagesOfStudies = new ArrayList<>();


    public Attendee(String firstName, String lastName,
                    String familyName, LocalDate dateOfBirth,
                    String phoneNumber, String email,
                    List<StudyLanguage> languagesOfStudies) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);
        if (languagesOfStudies != null) {
            this.languagesOfStudies = languagesOfStudies;
        } else {
            this.languagesOfStudies = new ArrayList<>();
        }

    }

    public Attendee() {
        super();
    }

}
