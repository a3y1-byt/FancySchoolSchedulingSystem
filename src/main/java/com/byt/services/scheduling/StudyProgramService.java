package com.byt.services.scheduling;

import com.byt.data.scheduling.StudyProgram;
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

public class StudyProgramService implements CRUDService<StudyProgram> {
    private final SaveLoadService saveLoadService;
    private List<StudyProgram> studyPrograms;

    public StudyProgramService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.studyPrograms = null;
    }

    @Override
    public void initialize() throws IOException {
        loadStudyPrograms();
    }


    @Override
    public void create(StudyProgram prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("StudyProgram already exists");

        studyPrograms.add(StudyProgram.copy(prototype));
        saveAllStudyPrograms(studyPrograms);
    }

    @Override
    public Optional<StudyProgram> get(String id) {
        StudyProgram program = findOne(id);
        if (program == null) return Optional.empty();

        StudyProgram studyProgramCopy = StudyProgram.copy(program);
        return Optional.of(studyProgramCopy);
    }

    @Override
    public List<StudyProgram> getAll(){
        if(studyPrograms == null) return null;

        return studyPrograms.stream()
                .map(StudyProgram::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, StudyProgram prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (!exists(name)) throw new IllegalArgumentException("StudyProgram not found");

        List<StudyProgram> updatedList = studyPrograms.stream()
                .map(p -> p.getName().equals(name) ? StudyProgram.copy(prototype) : p)
                .collect(Collectors.toList());

        saveAllStudyPrograms(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException {
        Validation.notEmptyArgument(name);

        if (!exists(name)) throw new IllegalArgumentException("StudyProgram not found");

        int originalSize = studyPrograms.size();

        List<StudyProgram> updatedPrograms = studyPrograms.stream()
                .filter(p -> !p.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedPrograms.size() < originalSize) {
            saveAllStudyPrograms(updatedPrograms);
        }
    }

    @Override
    public boolean exists(String name) throws IOException {
        if(studyPrograms == null|| name == null || name.isEmpty()) return false;

        return studyPrograms.stream()
                .anyMatch(p -> p.getName()
                .equals(name));
    }

    private StudyProgram findOne(String name) {
        if(studyPrograms == null|| name == null || name.isEmpty()) return null;

        return this.studyPrograms.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllStudyPrograms(List<StudyProgram> programs) throws IOException {
        saveLoadService.save(DataSaveKeys.STUDY_PROGRAMS, programs);
        loadStudyPrograms();
    }

    private void loadStudyPrograms() throws IOException{
        String cannotLoadMessage = "Error loading study programs";
        if (!saveLoadService.canLoad(DataSaveKeys.STUDY_PROGRAMS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<StudyProgram>>(){}.getType();
        List<StudyProgram> loadedPrograms =
                (List<StudyProgram>) saveLoadService.load(DataSaveKeys.STUDY_PROGRAMS, type);

        this.studyPrograms = new ArrayList<>(loadedPrograms);
    }

    private void validate(StudyProgram program) throws ValidationException {
        Validation.notNull(program);
        Validation.notEmpty(program.getName());
        Validation.notNull(program.getLevel());
    }
}
