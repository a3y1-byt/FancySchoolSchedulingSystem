package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SpecializationService implements CRUDService<Specialization> {
    private final SaveLoadService saveLoadService;
    private final SubjectService subjectService;
    private List<Specialization> specializations;

    public SpecializationService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.subjectService = new SubjectService(saveLoadService);
        this.specializations = null;
    }

    @Override
    public void initialize() throws IOException {
        CRUDService.super.initialize();
    }

    @Override
    public void create(Specialization prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) throw new IllegalArgumentException("Prototype is null");
        if (exists(prototype.getId())) throw new IllegalArgumentException("Specialization already exists");

        specializations.add(Specialization.copy(prototype));
        saveAllSpecializations(specializations);
        loadSpecializations();
    }

    @Override
    public Optional<Specialization> get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Specialization id is null or empty");

        Specialization specialization = findById(id);
        if (specialization == null) return Optional.empty();

        List<Subject> subjects = subjectService.listSubjectsBySpecializationId(id);
        Specialization specializationCopy = Specialization.copy(specialization, subjects);
        return Optional.of(specializationCopy);
    }

    @Override
    public List<Specialization> getAll() throws IOException {
        return specializations.stream()
                .map(Specialization::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Specialization prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Specialization id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Specialization with id " + id + " does not exist");

        List<Specialization> updatedList = specializations.stream()
                .map(s -> s.getId().equals(id) ? Specialization.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSpecializations(updatedList);
        loadSpecializations();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Specialization id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Specialization with id " + id + " does not exist");

        int originalSize = specializations.size();

        List<Specialization> updatedSpecializations = specializations.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedSpecializations.size() < originalSize) {
            saveAllSpecializations(updatedSpecializations);
        }
        loadSpecializations();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadSpecializations();
        return specializations.stream().anyMatch(s -> s.getId().equals(id));
    }

    public List<Specialization> listSpecializationsByStudyProgramId(String studyProgramId) {
        return specializations.stream()
                .filter(s -> s.getStudyProgramId() != null && s.getStudyProgramId().equals(studyProgramId))
                .map(Specialization::copy)
                .collect(Collectors.toList());
    }

    private Specialization findById(String id) {
        return this.specializations.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSpecializations(List<Specialization> specializations) {
        try {
            saveLoadService.save(DataSaveKeys.SPECIALIZATIONS, specializations);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save specializations", e);
        }
    }

    private void loadSpecializations() {
        String cannotLoadMessage = "Error loading specializations";
        if (!saveLoadService.canLoad(DataSaveKeys.SPECIALIZATIONS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Specialization>>(){}.getType();
        try {
            List<Specialization> loadedSpecializations = (List<Specialization>) saveLoadService.load(DataSaveKeys.SPECIALIZATIONS, type);
            this.specializations = new ArrayList<>(loadedSpecializations);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }
}
