package com.byt.user_system;

import com.byt.user_system.enums.StudyLanguage;
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
public class FreeListener extends Attendee {

    private String notes;

}
