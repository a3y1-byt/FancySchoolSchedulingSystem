package com.byt.user_system;

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
@AllArgsConstructor
@NoArgsConstructor
public class Student extends Attendee {

    private StudyStatus studiesStatus;

}
