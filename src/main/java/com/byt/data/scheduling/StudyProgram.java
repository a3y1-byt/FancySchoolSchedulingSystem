package com.byt.data.scheduling;

import com.byt.enums.scheduling.StudyProgramLevel;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StudyProgram {
    String name;
    StudyProgramLevel level;
    List<Specialization> specializations;

    public static StudyProgram copy(StudyProgram program) {
        return StudyProgram.builder()
                .name(program.getName())
                .level(program.getLevel())
                .build();
    }

}
