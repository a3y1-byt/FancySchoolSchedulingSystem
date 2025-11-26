package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.StudyProgram;
import com.byt.scheduling.Specialization;
import com.byt.scheduling.enums.StudyProgramLevel;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class StudyProgramService {
    private final SaveLoadService saveLoadService;

    public StudyProgramService(SaveLoadService saveLoadService, SpecializationService specializationService) {
        this.saveLoadService = saveLoadService;
    }

    public StudyProgram addStudyProgram(String id, String name, StudyProgramLevel level) {
        List<StudyProgram> programs = listStudyPrograms();

        boolean exists = programs.stream().anyMatch(p -> p.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("StudyProgram with id " + id + " already exists");
        }

        StudyProgram program = StudyProgram.builder()
                .id(id)
                .name(name)
                .level(level)
                .specializations(new ArrayList<>())
                .build();

        programs.add(program);
        saveAllStudyPrograms(programs);
        return program;
    }




    public Optional<StudyProgram> getStudyProgramById(String id) {
        return listStudyPrograms().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<StudyProgram> listStudyPrograms() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.STUDY_PROGRAMS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<StudyProgram>>(){}.getType();
            List<StudyProgram> programs = (List<StudyProgram>) saveLoadService.load(DataSaveKeys.STUDY_PROGRAMS, type);
            return new ArrayList<>(programs);
        } catch (IOException e) {
            System.err.println("Failed to load study programs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public StudyProgram updateStudyProgram(String id, String name, StudyProgramLevel level, List<Specialization> specializations) {
        List<StudyProgram> programs = listStudyPrograms();

        StudyProgram existingProgram = getStudyProgramById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudyProgram with id " + id + " not found"));

        StudyProgram updatedProgram = StudyProgram.builder()
                .id(id)
                .name(name)
                .level(level)
                .specializations(new ArrayList<>(specializations))
                .build();

        programs = programs.stream()
                .map(p -> p.getId().equals(id) ? updatedProgram : p)
                .collect(Collectors.toList());

        saveAllStudyPrograms(programs);
        return updatedProgram;
    }

    public boolean deleteStudyProgram(String id) {
        List<StudyProgram> programs = listStudyPrograms();

        boolean removed = programs.removeIf(p -> p.getId().equals(id));
        if (removed) {
            saveAllStudyPrograms(programs);
        }
        return removed;
    }

    private void saveAllStudyPrograms(List<StudyProgram> programs) {
        try {
            saveLoadService.save(DataSaveKeys.STUDY_PROGRAMS, programs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save study programs", e);
        }
    }
}
