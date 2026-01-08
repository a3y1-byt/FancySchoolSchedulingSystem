package com.byt.services.scheduling;

import com.byt.data.scheduling.Semester;
import com.byt.exception.ValidationException;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.scheduling.Validator;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SemesterService implements CRUDService<Semester> {
    private final SaveLoadService saveLoadService;
    private List<Semester> semesters;

    public SemesterService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.semesters = null;
    }

    @Override
    public void initialize() throws IOException {
        loadSemesters();
    }

    @Override
    public void create(Semester prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        Validator.validateSemester(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("Semester already exists");

        semesters.add(Semester.copy(prototype));
        saveAllSemesters(semesters);
    }

    @Override
    public Optional<Semester> get(String name) {
        Semester semester = findOne(name);
        if (semester == null) return Optional.empty();

        Semester semesterCopy =  Semester.copy(semester);
        return Optional.of(semesterCopy);
    }

    @Override
    public List<Semester> getAll() {
        if (semesters == null) return null;
        return semesters.stream()
                .map(Semester::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, Semester prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        Validator.validateSemester(prototype);

        if (!exists(name)) throw new IllegalArgumentException("Semester not found");

        List<Semester> updatedList = semesters.stream()
                .map(s -> s.getName().equals(name) ? Semester.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSemesters(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException, IllegalArgumentException {
        Validator.notEmptyArgument(name);

        if (!exists(name)) throw new IllegalArgumentException("Semester not found");

        int originalSize = semesters.size();

        List<Semester> updatedSemesters = semesters.stream()
                .filter(s -> !s.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedSemesters.size() < originalSize) {
            saveAllSemesters(updatedSemesters);
        }
        loadSemesters();
    }

    @Override
    public boolean exists(String name) throws IOException {
        if(this.semesters == null || name == null || name.isEmpty()) return false;

        return semesters.stream()
                .anyMatch(s -> s.getName()
                .equals(name));
    }

    private Semester findOne(String name) {
        if(this.semesters == null || name == null || name.isEmpty()) return null;

        return this.semesters.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSemesters(List<Semester> semesters) throws IOException {
        saveLoadService.save(DataSaveKeys.SEMESTERS, semesters);
        loadSemesters();
    }

    private void loadSemesters() throws IOException {
        String cannotLoadMessage = "Error loading semesters";
        if (!saveLoadService.canLoad(DataSaveKeys.SEMESTERS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Semester>>(){}.getType();

        List<Semester> loadedSemesters =
                (List<Semester>) saveLoadService.load(DataSaveKeys.SEMESTERS, type);

        this.semesters = new ArrayList<>(loadedSemesters);

    }


}