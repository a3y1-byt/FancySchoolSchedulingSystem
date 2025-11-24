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
public class Teacher extends Staff {

    private String title;
    private String position;

    public Teacher(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   LocalDate hireDate,String title,
                   String position) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate);

        this.title = title;
        this.position = position;
    }

}
