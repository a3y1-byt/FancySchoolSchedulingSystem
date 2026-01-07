package com.byt.data.user_system;

import com.byt.enums.user_system.StudyLanguage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FreeListener extends Attendee {
    public static final int MAX_NOTES_LENGTH = 1000;
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
