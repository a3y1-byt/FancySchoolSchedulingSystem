package com.byt.data.scheduling;

import com.byt.enums.scheduling.DayOfWeek;
import com.byt.enums.scheduling.LessonMode;
import com.byt.enums.scheduling.LessonType;
import com.byt.enums.scheduling.WeekPattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
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
