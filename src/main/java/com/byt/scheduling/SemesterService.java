package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SemesterService implements CRUDService<Semester> {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;
    private List<Semester> semesters;

    public SemesterService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = new  LessonService(saveLoadService);
        this.semesters = null;
    }

    @Override
    public void initialize() throws IOException {
        String cannotLoadMessage = "Error loading semesters";
        if (!saveLoadService.canLoad(DataSaveKeys.SEMESTERS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Semester>>(){}.getType();
        try {
            List<Semester> loadedSemesters = (List<Semester>) saveLoadService.load(DataSaveKeys.SEMESTERS, type);
            this.semesters = new ArrayList<>(loadedSemesters);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    @Override
    public void create(Semester prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) throw new IllegalArgumentException("Prototype is null");
        if (exists(prototype.getId())) throw new IllegalArgumentException("Semester already exists");

        semesters.add(Semester.copy(prototype));
        saveAllSemesters(semesters);
        loadSemesters();
    }

    @Override
    public Optional<Semester> get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Semester id is null or empty");

        Semester semester = findById(id);
        if (semester == null) return Optional.empty();

        List<Lesson> lessons = lessonService.listLessonsBySemesterId(id);
        Semester semesterCopy =  Semester.copy(semester, lessons);
        return Optional.of(semesterCopy);
    }

    @Override
    public List<Semester> getAll() throws IOException {
        return semesters.stream()
                .map(Semester::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Semester prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Semester id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Semester with id " + id + " does not exist");

        List<Semester> updatedList = semesters.stream()
                .map(s -> s.getId().equals(id) ? Semester.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSemesters(updatedList);
        loadSemesters();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Semester id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Semester with id " + id + " does not exist");

        int originalSize = semesters.size();

        List<Semester> updatedSemesters = semesters.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedSemesters.size() < originalSize) {
            saveAllSemesters(updatedSemesters);
        }
        loadSemesters();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadSemesters();
        return semesters.stream().anyMatch(s -> s.getId().equals(id));
    }

    private Semester findById(String id) {
        if(this.semesters == null) return null;

        return this.semesters.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSemesters(List<Semester> semesters) {
        try {
            saveLoadService.save(DataSaveKeys.SEMESTERS, semesters);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save semesters", e);
        }
    }

    private void loadSemesters() {
        String cannotLoadMessage = "Error loading semesters";
        if (!saveLoadService.canLoad(DataSaveKeys.SEMESTERS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Semester>>(){}.getType();
        try {
            List<Semester> loadedSemesters = (List<Semester>) saveLoadService.load(DataSaveKeys.SEMESTERS, type);
            this.semesters = new ArrayList<>(loadedSemesters);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }
}