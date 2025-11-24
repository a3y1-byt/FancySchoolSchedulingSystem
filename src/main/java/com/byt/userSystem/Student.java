package com.byt.userSystem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Student extends Attendee {

    private StudyStatus studiesStatus;

    public Student(String firstName, String lastName,
                   String familyName, LocalDate dateOfBirth,
                   String phoneNumber, String email,
                   List<StudyLanguage> languagesOfStudies, StudyStatus studiesStatus) {
        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, languagesOfStudies);
        this.studiesStatus = studiesStatus;
    }

    public Student() {
        super();
    }

    public void manageScheduling() {
    }
}
