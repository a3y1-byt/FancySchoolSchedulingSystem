package com.byt.data.scheduling;

import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import com.byt.enums.user_system.StudyLanguage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class Lesson {
    String name;
    LessonType type;
    LessonMode mode;
    String note;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    StudyLanguage language;
    WeekPattern weekPattern;


    public static Lesson copy(Lesson lesson) {
        return Lesson.builder()
                .name(lesson.getName())
                .type(lesson.getType())
                .mode(lesson.getMode())
                .note(lesson.getNote())
                .dayOfWeek(lesson.getDayOfWeek())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .language(lesson.getLanguage())
                .weekPattern(lesson.getWeekPattern())
                .build();
    }
}
