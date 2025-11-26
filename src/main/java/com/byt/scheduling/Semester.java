package com.byt.scheduling;

import lombok.Builder;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;


@Value
@Builder
public class Semester {
    String id;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    int academicYear;
    List<Lesson> lessons;
}
