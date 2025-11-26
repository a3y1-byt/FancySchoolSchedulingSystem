package com.byt.user_system.data;

import com.byt.user_system.enums.StudyLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public abstract class Attendee extends User {

    private List<StudyLanguage> languagesOfStudies;


    public Attendee(String firstName, String lastName,
                    String familyName, LocalDate dateOfBirth,
                    String phoneNumber, String email,
                    List<StudyLanguage> languagesOfStudies) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);
        this.languagesOfStudies = Objects.requireNonNullElseGet(languagesOfStudies, ArrayList::new);

    }

}
