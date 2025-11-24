package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class Attendee extends User {

    private List<StudyLanguage> languagesOfStudies = new ArrayList<>();


    public Attendee(String firstName, String lastName,
                    String familyName, LocalDate dateOfBirth,
                    String phoneNumber, String email,
                    List<StudyLanguage> languagesOfStudies) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);
        this.languagesOfStudies = Objects.requireNonNullElseGet(languagesOfStudies, ArrayList::new);

    }

    public Attendee() {
        super();
    }

}
