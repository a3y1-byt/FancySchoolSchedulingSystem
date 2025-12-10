package com.byt.services.scheduling;

import com.byt.data.scheduling.Lesson;
import com.byt.data.scheduling.Subject;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class SubjectService implements CRUDService<Subject> {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;
    private List<Subject> subjects;

    public SubjectService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = new LessonService(saveLoadService);
        this.subjects = null;
    }

    @Override
    public void initialize() throws IOException {
        String cannotLoadMessage = "Error loading subjects";
        if (!saveLoadService.canLoad(DataSaveKeys.SUBJECTS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Subject>>(){}.getType();
        try {
            List<Subject> loadedSubjects = (List<Subject>) saveLoadService.load(DataSaveKeys.SUBJECTS, type);
            this.subjects = new ArrayList<>(loadedSubjects);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    @Override
    public void create(Subject prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (exists(prototype.getId())) throw new IllegalArgumentException("Subject already exists");

        subjects.add(Subject.copy(prototype));
        saveAllSubjects(subjects);
        loadSubjects();
    }

    @Override
    public Optional<Subject> get(String id) throws IllegalArgumentException, IOException {
        Subject subject = findById(id);
        if (subject == null) return Optional.empty();

        List<Lesson> lessons = lessonService.listLessonsBySubjectId(id);
        Subject subjectCopy = Subject.copy(subject);
        return Optional.of(subjectCopy);
    }

    @Override
    public List<Subject> getAll() throws IOException {
        return subjects.stream()
                .map(Subject::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Subject prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (!exists(id)) throw new IllegalArgumentException("Subject with id " + id + " does not exist");

        List<Subject> updatedList = subjects.stream()
                .map(s -> s.getId().equals(id) ? Subject.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSubjects(updatedList);
        loadSubjects();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Subject id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Subject with id " + id + " does not exist");

        int originalSize = subjects.size();

        List<Subject> updatedSubjects = subjects.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedSubjects.size() < originalSize) {
            saveAllSubjects(updatedSubjects);
        }
        loadSubjects();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadSubjects();
        return subjects.stream().anyMatch(s -> s.getId().equals(id));
    }

    public List<Subject> listSubjectsBySpecializationId(String specializationId) {
        if(this.subjects == null || this.subjects.isEmpty()) return null;

        return this.subjects.stream()
                .filter(s -> s.getSpecializationId().equals(specializationId))
                .map(Subject::copy)
                .collect(Collectors.toList());

    }

    private Subject findById(String id) {
        if(this.subjects == null || this.subjects.isEmpty()) return null;
        return this.subjects.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSubjects(List<Subject> subjects) {
        try {
            saveLoadService.save(DataSaveKeys.SUBJECTS, subjects);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save subjects", e);
        }
    }

    private void loadSubjects() {
        String cannotLoadMessage = "Error loading subjects";
        if (!saveLoadService.canLoad(DataSaveKeys.SUBJECTS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Subject>>(){}.getType();
        try {
            List<Subject> loadedSubjects = (List<Subject>) saveLoadService.load(DataSaveKeys.SUBJECTS, type);
            this.subjects = new ArrayList<>(loadedSubjects);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    private void validate(Subject subject) {
        List<String> errors = new ArrayList<>();

        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }

        if (subject.getId() == null || subject.getId().trim().isEmpty()) {
            errors.add("Subject ID is required");
        }

        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            errors.add("Subject name is required");
        }

        if (subject.getHours() <= 0) {
            errors.add("Subject hours must be positive");
        }

        if (subject.getSpecializationId() == null || subject.getSpecializationId().trim().isEmpty()) {
            errors.add("Specialization ID is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }
}
