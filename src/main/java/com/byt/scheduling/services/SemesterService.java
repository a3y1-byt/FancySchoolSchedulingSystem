package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Semester;
import com.byt.scheduling.Lesson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SemesterService {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;

    public SemesterService(SaveLoadService saveLoadService, LessonService lessonService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = lessonService;
    }

    public Semester addSemester(String id, String name, LocalDate startDate, LocalDate endDate, int academicYear) {
        List<Semester> semesters = listSemesters();

        boolean exists = semesters.stream().anyMatch(s -> s.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Semester with id " + id + " already exists");
        }

        Semester semester = Semester.builder()
                .id(id)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .academicYear(academicYear)
                .lessons(new ArrayList<>())
                .build();

        semesters.add(semester);
        saveAllSemesters(semesters);
        return semester;
    }

    public Optional<Semester> getSemesterById(String id) {
        return listSemesters().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public List<Semester> listSemesters() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.SEMESTERS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Semester>>(){}.getType();
            List<Semester> semesters = (List<Semester>) saveLoadService.load(DataSaveKeys.SEMESTERS, type);
            return new ArrayList<>(semesters);
        } catch (IOException e) {
            System.err.println("Failed to load semesters: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Semester updateSemester(String id, String name, LocalDate startDate, LocalDate endDate,
                                   int academicYear, List<Lesson> lessons) {
        List<Semester> semesters = listSemesters();

        Semester existingSemester = getSemesterById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semester with id " + id + " not found"));

        Semester updatedSemester = Semester.builder()
                .id(id)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .academicYear(academicYear)
                .lessons(new ArrayList<>(lessons))
                .build();

        semesters = semesters.stream()
                .map(s -> s.getId().equals(id) ? updatedSemester : s)
                .collect(Collectors.toList());

        saveAllSemesters(semesters);
        return updatedSemester;
    }

    public boolean deleteSemester(String id) {
        List<Semester> semesters = listSemesters();

        boolean removed = semesters.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveAllSemesters(semesters);
        }
        return removed;
    }

    private void saveAllSemesters(List<Semester> semesters) {
        try {
            saveLoadService.save(DataSaveKeys.SEMESTERS, semesters);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save semesters", e);
        }
    }
}
