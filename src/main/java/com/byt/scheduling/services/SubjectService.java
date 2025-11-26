package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Subject;
import com.byt.scheduling.Lesson;
import com.byt.scheduling.enums.SubjectType;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SubjectService {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;

    public SubjectService(SaveLoadService saveLoadService, LessonService lessonService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = lessonService;
    }

    public Subject addSubject(String id, String name, List<SubjectType> types, int hours, int ects) {
        List<Subject> subjects = listSubjects();

        boolean exists = subjects.stream().anyMatch(s -> s.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Subject with id " + id + " already exists");
        }

        Subject subject = Subject.builder()
                .id(id)
                .name(name)
                .types(new ArrayList<>(types))
                .hours(hours)
                .ects(ects)
                .lessons(new ArrayList<>())
                .build();

        subjects.add(subject);
        saveAllSubjects(subjects);
        return subject;
    }

    public Subject addLessonToSubject(String subjectId, String lessonId) {
        Subject subject = getSubjectById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject with id " + subjectId + " not found"));

        Lesson lesson = lessonService.getLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson with id " + lessonId + " not found"));

        List<Lesson> lessons = new ArrayList<>(subject.getLessons());
        if (lessons.stream().noneMatch(l -> l.getId().equals(lessonId))) {
            lessons.add(lesson);
        }

        return updateSubject(subjectId, subject.getName(), subject.getTypes(),
                subject.getHours(), subject.getEcts(), lessons);
    }

    public Subject removeLessonFromSubject(String subjectId, String lessonId) {
        Subject subject = getSubjectById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject with id " + subjectId + " not found"));

        List<Lesson> lessons = subject.getLessons().stream()
                .filter(l -> !l.getId().equals(lessonId))
                .collect(Collectors.toList());

        return updateSubject(subjectId, subject.getName(), subject.getTypes(),
                subject.getHours(), subject.getEcts(), lessons);
    }

    public Optional<Subject> getSubjectById(String id) {
        return listSubjects().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public List<Subject> listSubjects() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.SUBJECTS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Subject>>(){}.getType();
            List<Subject> subjects = (List<Subject>) saveLoadService.load(DataSaveKeys.SUBJECTS, type);
            return new ArrayList<>(subjects);
        } catch (IOException e) {
            System.err.println("Failed to load subjects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Subject updateSubject(String id, String name, List<SubjectType> types, int hours, int ects, List<Lesson> lessons) {
        List<Subject> subjects = listSubjects();

        Subject existingSubject = getSubjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject with id " + id + " not found"));

        Subject updatedSubject = Subject.builder()
                .id(id)
                .name(name)
                .types(new ArrayList<>(types))
                .hours(hours)
                .ects(ects)
                .lessons(new ArrayList<>(lessons))
                .build();

        subjects = subjects.stream()
                .map(s -> s.getId().equals(id) ? updatedSubject : s)
                .collect(Collectors.toList());

        saveAllSubjects(subjects);
        return updatedSubject;
    }

    public boolean deleteSubject(String id) {
        List<Subject> subjects = listSubjects();

        boolean removed = subjects.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveAllSubjects(subjects);
        }
        return removed;
    }

    private void saveAllSubjects(List<Subject> subjects) {
        try {
            saveLoadService.save(DataSaveKeys.SUBJECTS, subjects);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save subjects", e);
        }
    }
}
