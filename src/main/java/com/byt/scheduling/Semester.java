package com.byt.scheduling;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Semester {
    private String semester;
    private Date startDate;
    private Date endDate;
    private int academicYear;
    private List<Lesson> lessons;
}
