package com.byt.services.scheduling;

import com.byt.data.scheduling.Lesson;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LessonService implements CRUDService<Lesson> {
    private final SaveLoadService saveLoadService;
    private List<Lesson> lessons;

    public LessonService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.lessons = null;
    }

    @Override
    public void initialize() throws IOException {
        String cannotLoadMessage = "Error loading lessons";
        if (!saveLoadService.canLoad(DataSaveKeys.LESSONS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Lesson>>(){}.getType();
        try {
            List<Lesson> loadedLessons = (List<Lesson>) saveLoadService.load(DataSaveKeys.LESSONS, type);
            this.lessons = new ArrayList<>(loadedLessons);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    @Override
    public void create(Lesson prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (exists(prototype.getId())) throw new IllegalArgumentException("Lesson already exists");

        lessons.add(Lesson.copy(prototype));
        saveAllLessons(lessons);
        loadLessons();
    }

    @Override
    public Optional<Lesson> get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Lesson id is null or empty");

        Lesson lesson = findById(id);
        if (lesson == null) return Optional.empty();

        Lesson lessonCopy = Lesson.copy(lesson);
        return Optional.of(lessonCopy);
    }

    @Override
    public List<Lesson> getAll() throws IOException {
        return lessons.stream()
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Lesson prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (!exists(id)) throw new IllegalArgumentException("Lesson with id " + id + " does not exist");

        List<Lesson> updatedList = lessons.stream()
                .map(l -> l.getId().equals(id) ? Lesson.copy(prototype) : l)
                .collect(Collectors.toList());

        saveAllLessons(updatedList);
        loadLessons();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Lesson id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Lesson with id " + id + " does not exist");

        int originalSize = lessons.size();

        List<Lesson> updatedLessons = lessons.stream()
                .filter(l -> !l.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedLessons.size() < originalSize) {
            saveAllLessons(updatedLessons);
        }
        loadLessons();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadLessons();
        return lessons.stream().anyMatch(l -> l.getId().equals(id));
    }

    public List<Lesson> listLessonsByGroupId(String groupId) {
        if(this.lessons == null || this.lessons.isEmpty()) return null;

        return lessons.stream()
                .filter(l -> l.getGroupId() != null && l.getClassRoomId().equals(groupId))
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    public List<Lesson> listLessonsBySemesterId(String groupId) {
        if(this.lessons == null || this.lessons.isEmpty()) return null;

        return lessons.stream()
                .filter(l -> l.getSemesterId() != null && l.getSemesterId().equals(groupId))
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    public List<Lesson> listLessonsByClassRoomId(String classRoomId) {
        if(this.lessons == null || this.lessons.isEmpty()) return null;

        return lessons.stream()
                .filter(l -> l.getClassRoomId() != null && l.getClassRoomId().equals(classRoomId))
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    public List<Lesson> listLessonsBySubjectId(String subjectId) {
        if(this.lessons == null || this.lessons.isEmpty()) return null;
        return lessons.stream()
                .filter(l -> l.getSubjectId() != null && l.getSubjectId().equals(subjectId))
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    private Lesson findById(String id) {
        if(this.lessons == null || this.lessons.isEmpty()) return null;

        return this.lessons.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllLessons(List<Lesson> lessons) {
        try {
            saveLoadService.save(DataSaveKeys.LESSONS, lessons);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save lessons", e);
        }
    }

    private void loadLessons() {
        String cannotLoadMessage = "Error loading lessons";
        if (!saveLoadService.canLoad(DataSaveKeys.LESSONS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Lesson>>(){}.getType();
        try {
            List<Lesson> loadedLessons = (List<Lesson>) saveLoadService.load(DataSaveKeys.LESSONS, type);
            this.lessons = new ArrayList<>(loadedLessons);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    private void validate(Lesson lesson) {
        List<String> errors = new ArrayList<>();

        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        if (lesson.getId() == null || lesson.getId().trim().isEmpty()) {
            errors.add("Lesson ID is required");
        }

        if (lesson.getType() == null) {
            errors.add("Lesson type is required");
        }

        if (lesson.getMode() == null) {
            errors.add("Lesson mode is required");
        }

        if (lesson.getDayOfWeek() == null) {
            errors.add("Day of week is required");
        }

        if (lesson.getStartTime() == null) {
            errors.add("Start time is required");
        }

        if (lesson.getEndTime() == null) {
            errors.add("End time is required");
        }

        if (lesson.getLanguage() == null || lesson.getLanguage().trim().isEmpty()) {
            errors.add("Lesson language is required");
        }

        if (lesson.getClassRoomId() == null || lesson.getClassRoomId().trim().isEmpty()) {
            errors.add("ClassRoom ID is required");
        }

        if (lesson.getSubjectId() == null || lesson.getSubjectId().trim().isEmpty()) {
            errors.add("Subject ID is required");
        }

        if (lesson.getSemesterId() == null || lesson.getSemesterId().trim().isEmpty()) {
            errors.add("Semester ID is required");
        }

        if (lesson.getGroupId() == null || lesson.getGroupId().trim().isEmpty()) {
            errors.add("Group ID is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }
}
