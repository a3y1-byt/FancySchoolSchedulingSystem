package com.byt.user_system.data;

import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
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
public class Student extends Attendee {

    private StudyStatus studiesStatus;

    public Student(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   List<StudyLanguage> languagesOfStudies,
                   StudyStatus studiesStatus) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies);

        this.studiesStatus = studiesStatus;
    }
}
