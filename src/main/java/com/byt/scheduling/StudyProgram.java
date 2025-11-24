package com.byt.scheduling;

import com.byt.scheduling.enums.StudyProgramLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudyProgram {
    private String name;
    private StudyProgramLevel level;
    private List<Specialization> specializations;
}
