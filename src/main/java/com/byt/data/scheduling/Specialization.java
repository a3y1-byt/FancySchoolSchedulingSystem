package com.byt.data.scheduling;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Specialization {
    String name;
    String description;
    List<Subject> subjects;


    public static Specialization copy(Specialization specialization) {
        return Specialization.builder()
                .name(specialization.getName())
                .description(specialization.getDescription())
                .build();
    }
}
