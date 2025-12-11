package com.byt.services.scheduling;

import com.byt.data.scheduling.Lesson;
import com.byt.data.scheduling.Subject;
import com.byt.exception.ValidationException;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.scheduling.Validation;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SubjectService implements CRUDService<Subject> {
    private final SaveLoadService saveLoadService;
    private List<Subject> subjects;

    public SubjectService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.subjects = null;
    }

    @Override
    public void initialize() throws IOException {
        loadSubjects();
    }

    @Override
    public void create(Subject prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("Subject already exists");

        subjects.add(Subject.copy(prototype));
        saveAllSubjects(subjects);
    }

    @Override
    public Optional<Subject> get(String id) throws IllegalArgumentException, IOException {
        Subject subject = findOne(id);
        if (subject == null) return Optional.empty();

        Subject subjectCopy = Subject.copy(subject);
        return Optional.of(subjectCopy);
    }

    @Override
    public List<Subject> getAll() throws IOException {
        if (subjects == null) return null;

        return subjects.stream()
                .map(Subject::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, Subject prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (!exists(name)) throw new IllegalArgumentException("Subject not found");

        List<Subject> updatedList = subjects.stream()
                .map(s -> s.getName().equals(name) ? Subject.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSubjects(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException {
        Validation.notEmptyArgument(name);
        if (!exists(name)) throw new IllegalArgumentException("Subject not found");

        int originalSize = subjects.size();

        List<Subject> updatedSubjects = subjects.stream()
                .filter(s -> !s.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedSubjects.size() < originalSize) {
            saveAllSubjects(updatedSubjects);
        }
    }

    @Override
    public boolean exists(String name) throws IOException {
        if(subjects == null|| name == null || name.isEmpty()) return false;

        return subjects.stream()
                .anyMatch(s -> s.getName()
                .equals(name));
    }

    private Subject findOne(String name) {
        if(subjects == null|| name == null || name.isEmpty()) return null;

        return this.subjects.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSubjects(List<Subject> subjects) throws IOException {
        saveLoadService.save(DataSaveKeys.SUBJECTS, subjects);
        loadSubjects();
    }

    private void loadSubjects() throws IOException {
        String cannotLoadMessage = "Error loading subjects";
        if (!saveLoadService.canLoad(DataSaveKeys.SUBJECTS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Subject>>(){}.getType();

        List<Subject> loadedSubjects =
                (List<Subject>) saveLoadService.load(DataSaveKeys.SUBJECTS, type);

        this.subjects = new ArrayList<>(loadedSubjects);

    }

    private void validate(Subject subject) {
        Validation.notNull(subject);
        Validation.notEmpty(subject.getName());
        Validation.notNull(subject.getHours());

    }
}
