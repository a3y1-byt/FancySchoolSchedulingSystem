package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Lesson;
import com.byt.scheduling.enums.DayOfWeek;
import com.byt.scheduling.enums.LessonMode;
import com.byt.scheduling.enums.LessonType;
import com.byt.scheduling.enums.WeekPattern;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class LessonService {
    private final SaveLoadService saveLoadService;

    public LessonService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
    }

    public Lesson addLesson(String id, LessonType type, LessonMode mode, String note, DayOfWeek dayOfWeek,
                            LocalTime startTime, LocalTime endTime, String language, WeekPattern weekPattern,
                            String classRoomId, String subjectId) {
        List<Lesson> lessons = listLessons();

        boolean exists = lessons.stream().anyMatch(l -> l.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Lesson with id " + id + " already exists");
        }

        Lesson lesson = Lesson.builder()
                .id(id)
                .type(type)
                .mode(mode)
                .note(note)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .language(language)
                .weekPattern(weekPattern)
                .classRoomId(classRoomId)
                .subjectId(subjectId)
                .build();

        lessons.add(lesson);
        saveAllLessons(lessons);
        return lesson;
    }

    public Optional<Lesson> getLessonById(String id) {
        return listLessons().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }

    public List<Lesson> listLessons() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.LESSONS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Lesson>>(){}.getType();
            List<Lesson> lessons = (List<Lesson>) saveLoadService.load(DataSaveKeys.LESSONS, type);
            return new ArrayList<>(lessons);
        } catch (IOException e) {
            System.err.println("Failed to load lessons: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Lesson updateLesson(String id, LessonType type, LessonMode mode, String note, DayOfWeek dayOfWeek,
                               LocalTime startTime, LocalTime endTime, String language, WeekPattern weekPattern,
                               String classRoomId, String subjectId) {
        List<Lesson> lessons = listLessons();

        Lesson existingLesson = getLessonById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson with id " + id + " not found"));

        Lesson updatedLesson = Lesson.builder()
                .id(id)
                .type(type)
                .mode(mode)
                .note(note)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .language(language)
                .weekPattern(weekPattern)
                .classRoomId(classRoomId)
                .subjectId(subjectId)
                .build();

        lessons = lessons.stream()
                .map(l -> l.getId().equals(id) ? updatedLesson : l)
                .collect(Collectors.toList());

        saveAllLessons(lessons);
        return updatedLesson;
    }

    public boolean deleteLesson(String id) {
        List<Lesson> lessons = listLessons();

        boolean removed = lessons.removeIf(l -> l.getId().equals(id));
        if (removed) {
            saveAllLessons(lessons);
        }
        return removed;
    }

    private void saveAllLessons(List<Lesson> lessons) {
        try {
            saveLoadService.save(DataSaveKeys.LESSONS, lessons);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save lessons", e);
        }
    }
}
