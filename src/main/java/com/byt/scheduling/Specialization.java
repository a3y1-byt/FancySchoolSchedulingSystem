package com.byt.scheduling;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Specialization {
    private String name;
    private String description;
    private List<Subject> subjects;
}
