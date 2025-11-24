package com.byt.user_system.data;

import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FreeListener extends Attendee {

    private String notes;

    public FreeListener(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   List<StudyLanguage> languagesOfStudies,
                    String notes) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies);

        this.notes = notes;
    }

}
