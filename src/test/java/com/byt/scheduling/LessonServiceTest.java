package com.byt.scheduling;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.enums.DayOfWeek;
import com.byt.scheduling.enums.LessonMode;
import com.byt.scheduling.enums.LessonType;
import com.byt.scheduling.enums.WeekPattern;
import com.byt.services.CRUDServiceTest;

import java.time.LocalTime;

class LessonServiceTest extends CRUDServiceTest<Lesson> {

    protected LessonServiceTest() {
        super(DataSaveKeys.LESSONS, LessonService::new);
    }

    @Override
    protected String getSampleObjectId() {
        return "L-001";
    }

    @Override
    protected Lesson getSampleObject() {
        return Lesson.builder()
                .id("L-001")
                .type(LessonType.LECTURE)
                .mode(LessonMode.OFFLINE)
                .note("")
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 30))
                .language("English")
                .weekPattern(WeekPattern.EVEN)
                .classRoomId("CR-101")
                .subjectId("SUB-CS101")
                .semesterId("SEM-2025-FALL")
                .groupId("G-SE-2025")
                .build();
    }

    @Override
    protected void alterEntity(Lesson lesson) {
        lesson.setId("L-002");
    }
}
