package com.byt.scheduling;

import com.byt.scheduling.enums.DayOfWeek;
import com.byt.scheduling.enums.LessonMode;
import com.byt.scheduling.enums.LessonType;
import com.byt.scheduling.enums.WeekPattern;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;


@Value
@Builder
public class Lesson {
    String id;
    LessonType type;
    LessonMode mode;
    String note;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String language;
    WeekPattern weekPattern;
    String classRoomId;
    String subjectId;
}
