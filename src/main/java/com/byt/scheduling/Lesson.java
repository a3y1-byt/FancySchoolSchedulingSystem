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
    String semesterId;
    String groupId;

    public static Lesson copy(Lesson lesson) {
        return Lesson.builder()
                .id(lesson.getId())
                .type(lesson.getType())
                .mode(lesson.getMode())
                .note(lesson.getNote())
                .dayOfWeek(lesson.getDayOfWeek())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .language(lesson.getLanguage())
                .weekPattern(lesson.getWeekPattern())
                .classRoomId(lesson.getClassRoomId())
                .subjectId(lesson.getSubjectId())
                .semesterId(lesson.getSemesterId())
                .groupId(lesson.getGroupId())
                .build();
    }
}
