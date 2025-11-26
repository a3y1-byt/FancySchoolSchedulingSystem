package com.byt.scheduling;

import com.byt.scheduling.enums.StudyProgramLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StudyProgram {
    String id;
    String name;
    StudyProgramLevel level;
    List<Specialization> specializations;
}
