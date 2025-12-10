package com.byt.services.scheduling;

import com.byt.data.scheduling.Specialization;
import com.byt.data.scheduling.Subject;
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

    @Override
    public void create(Specialization prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

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
        if (specializations == null) loadSpecializations();
        return specializations.stream()
                .map(Specialization::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Specialization prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

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
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Specialization id is null or empty");

        loadSpecializations();
        return specializations.stream().anyMatch(s -> s.getId().equals(id));
    }

    public List<Specialization> listSpecializationsByStudyProgramId(String studyProgramId) {
        if (specializations == null) return null;
        return specializations.stream()
                .filter(s -> s.getStudyProgramId() != null && s.getStudyProgramId().equals(studyProgramId))
                .map(Specialization::copy)
                .collect(Collectors.toList());
    }

    private Specialization findById(String id) {
        if(specializations == null) return null;

        return this.specializations.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSpecializations(List<Specialization> specializations) {
        if (specializations == null) return;
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

    private void validate(Specialization specialization) {
        List<String> errors = new ArrayList<>();

        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        if (specialization.getId() == null || specialization.getId().trim().isEmpty()) {
            errors.add("Specialization ID is required");
        }

        if (specialization.getName() == null || specialization.getName().trim().isEmpty()) {
            errors.add("Specialization name is required");
        }

        if (specialization.getStudyProgramId() == null || specialization.getStudyProgramId().trim().isEmpty()) {
            errors.add("Study Program ID is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }
}
