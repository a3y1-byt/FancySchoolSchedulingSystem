package com.byt.scheduling;

import com.byt.scheduling.enums.StudyProgramLevel;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StudyProgram {
    String id;
    String name;
    StudyProgramLevel level;
    List<Specialization> specializations;

    public static StudyProgram copy(StudyProgram program, List<Specialization> specializations) {
        return StudyProgram.builder()
                .id(program.getId())
                .name(program.getName())
                .level(program.getLevel())
                .specializations(specializations)
                .build();
    }

    public static StudyProgram copy(StudyProgram program) {
        return StudyProgram.builder()
                .id(program.getId())
                .name(program.getName())
                .level(program.getLevel())
                .specializations(null)
                .build();
    }
}
