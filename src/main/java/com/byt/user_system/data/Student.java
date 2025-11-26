package com.byt.user_system.data;

import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Student extends Attendee {

    private StudyStatus studiesStatus;

    public Student(String firstName, String lastName, String familyName,
                   Instant dateOfBirth, String phoneNumber, String email,
                   List<StudyLanguage> languagesOfStudies,
                   StudyStatus studiesStatus) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies);

        this.studiesStatus = studiesStatus;
    }
}
