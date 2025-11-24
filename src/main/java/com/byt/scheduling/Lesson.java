package com.byt.scheduling;

import com.byt.scheduling.enums.DayOfWeek;
import com.byt.scheduling.enums.LessonMode;
import com.byt.scheduling.enums.LessonType;
import com.byt.scheduling.enums.WeekPattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Lesson {
    private LessonType type;
    private LessonMode mode;
    private String note;
    private DayOfWeek dayOfWeek;
    private Date startTime;
    private Date endTime;
    private String language;
    private WeekPattern weekPattern;
    private ClassRoom classRoom;
}
