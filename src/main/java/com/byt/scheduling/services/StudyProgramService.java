package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.StudyProgram;
import com.byt.scheduling.Specialization;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class StudyProgramService implements CRUDService<StudyProgram> {
    private final SaveLoadService saveLoadService;
    private final SpecializationService specializationService;
    private List<StudyProgram> studyPrograms;

    public StudyProgramService(SaveLoadService saveLoadService, SpecializationService specializationService) {
        this.saveLoadService = saveLoadService;
        this.specializationService = specializationService;
        this.studyPrograms = null;
        loadStudyPrograms();
    }

    @Override
    public void create(StudyProgram prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) throw new IllegalArgumentException("Prototype is null");
        if (exists(prototype.getId())) throw new IllegalArgumentException("StudyProgram already exists");

        studyPrograms.add(StudyProgram.copy(prototype));
        saveAllStudyPrograms(studyPrograms);
        loadStudyPrograms();
    }

    @Override
    public StudyProgram get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("StudyProgram id is null or empty");

        StudyProgram program = findById(id);
        if (program == null) return null;

        List<Specialization> specializations = specializationService.listSpecializationsByStudyProgramId(id);
        return StudyProgram.copy(program, specializations);
    }

    @Override
    public void update(String id, StudyProgram prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("StudyProgram id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("StudyProgram with id " + id + " does not exist");

        List<StudyProgram> updatedList = studyPrograms.stream()
                .map(p -> p.getId().equals(id) ? StudyProgram.copy(prototype) : p)
                .collect(Collectors.toList());

        saveAllStudyPrograms(updatedList);
        loadStudyPrograms();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("StudyProgram id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("StudyProgram with id " + id + " does not exist");

        int originalSize = studyPrograms.size();

        List<StudyProgram> updatedPrograms = studyPrograms.stream()
                .filter(p -> !p.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedPrograms.size() < originalSize) {
            saveAllStudyPrograms(updatedPrograms);
        }
        loadStudyPrograms();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadStudyPrograms();
        return studyPrograms.stream().anyMatch(p -> p.getId().equals(id));
    }

    private StudyProgram findById(String id) {
        return this.studyPrograms.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllStudyPrograms(List<StudyProgram> programs) {
        try {
            saveLoadService.save(DataSaveKeys.STUDY_PROGRAMS, programs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save study programs", e);
        }
    }

    private void loadStudyPrograms() {
        String cannotLoadMessage = "Error loading study programs";
        if (!saveLoadService.canLoad(DataSaveKeys.STUDY_PROGRAMS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<StudyProgram>>(){}.getType();
        try {
            List<StudyProgram> loadedPrograms = (List<StudyProgram>) saveLoadService.load(DataSaveKeys.STUDY_PROGRAMS, type);
            this.studyPrograms = new ArrayList<>(loadedPrograms);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }
}
