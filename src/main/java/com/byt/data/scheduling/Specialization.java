package com.byt.data.scheduling;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Specialization {
    String id;
    String name;
    String description;
    String studyProgramId;
    List<Subject> subjects;

    public static Specialization copy(Specialization specialization, List<Subject> subjects) {
        return Specialization.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .description(specialization.getDescription())
                .studyProgramId(specialization.getStudyProgramId())
                .subjects(subjects)
                .build();
    }

    public static Specialization copy(Specialization specialization) {
        return Specialization.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .description(specialization.getDescription())
                .studyProgramId(specialization.getStudyProgramId())
                .subjects(null)
                .build();
    }
}
