package com.byt.scheduling;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Specialization {
    String id;
    String name;
    String description;
    String studyProgramId;
    List<Subject> subjects;
}
