package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Specialization;
import com.byt.scheduling.Subject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SpecializationService {
    private final SaveLoadService saveLoadService;

    public SpecializationService(SaveLoadService saveLoadService, SubjectService subjectService) {
        this.saveLoadService = saveLoadService;
    }

    public Specialization addSpecialization(String id, String name, String description, String studyProgramId) {
        List<Specialization> specializations = listSpecializations();

        boolean exists = specializations.stream().anyMatch(s -> s.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Specialization with id " + id + " already exists");
        }

        Specialization specialization = Specialization.builder()
                .id(id)
                .name(name)
                .description(description)
                .studyProgramId(studyProgramId)
                .subjects(new ArrayList<>())
                .build();

        specializations.add(specialization);
        saveAllSpecializations(specializations);
        return specialization;
    }

    public Optional<Specialization> getSpecializationById(String id) {
        return listSpecializations().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public List<Specialization> listSpecializations() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.SPECIALIZATIONS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Specialization>>(){}.getType();
            List<Specialization> specializations = (List<Specialization>) saveLoadService.load(DataSaveKeys.SPECIALIZATIONS, type);
            return new ArrayList<>(specializations);
        } catch (IOException e) {
            System.err.println("Failed to load specializations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Specialization updateSpecialization(String id, String name, String description, String studyProgramId, List<Subject> subjects) {
        List<Specialization> specializations = listSpecializations();

        Specialization existingSpecialization = getSpecializationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialization with id " + id + " not found"));

        Specialization updatedSpecialization = Specialization.builder()
                .id(id)
                .name(name)
                .description(description)
                .studyProgramId(studyProgramId)
                .subjects(new ArrayList<>(subjects))
                .build();

        specializations = specializations.stream()
                .map(s -> s.getId().equals(id) ? updatedSpecialization : s)
                .collect(Collectors.toList());

        saveAllSpecializations(specializations);
        return updatedSpecialization;
    }

    public boolean deleteSpecialization(String id) {
        List<Specialization> specializations = listSpecializations();

        boolean removed = specializations.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveAllSpecializations(specializations);
        }
        return removed;
    }

    private void saveAllSpecializations(List<Specialization> specializations) {
        try {
            saveLoadService.save(DataSaveKeys.SPECIALIZATIONS, specializations);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save specializations", e);
        }
    }
}
