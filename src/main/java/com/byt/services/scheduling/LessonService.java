package com.byt.services.scheduling;

import com.byt.data.scheduling.Lesson;
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

public class LessonService implements CRUDService<Lesson> {
    private final SaveLoadService saveLoadService;
    private List<Lesson> lessons;

    public LessonService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.lessons = null;
    }

    @Override
    public void initialize() throws IOException {
        loadLessons();
    }

    @Override
    public void create(Lesson prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("Lesson already exists");

        lessons.add(Lesson.copy(prototype));
        saveAllLessons(lessons);
    }

    @Override
    public Optional<Lesson> get(String name) {
        Lesson lesson = findOne(name);
        if (lesson == null) return Optional.empty();

        Lesson lessonCopy = Lesson.copy(lesson);
        return Optional.of(lessonCopy);
    }

    @Override
    public List<Lesson> getAll() {
        if (lessons == null) return null;

        return lessons.stream()
                .map(Lesson::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, Lesson prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (!exists(name)) throw new IllegalArgumentException("Lesson not found");

        List<Lesson> updatedList = lessons.stream()
                .map(l -> l.getName().equals(name) ? Lesson.copy(prototype) : l)
                .collect(Collectors.toList());

        saveAllLessons(updatedList);
    }

    @Override
    public void delete(String name)
            throws IllegalArgumentException, IOException, ValidationException
    {
        Validation.notEmptyArgument(name);

        if (!exists(name)) throw new IllegalArgumentException("Lesson not found");

        int originalSize = lessons.size();

        List<Lesson> updatedLessons = lessons.stream()
                .filter(l -> !l.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedLessons.size() < originalSize) {
            saveAllLessons(updatedLessons);
        }
    }

    @Override
    public boolean exists(String name) throws IOException {
        if(this.lessons == null || name == null || name.isEmpty()) return false;

        return lessons.stream().anyMatch(l -> l.getName().equals(name));
    }


    private Lesson findOne(String name) {
        if(this.lessons == null || name == null || name.isEmpty()) return null;

        return this.lessons.stream()
                .filter(l -> l.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllLessons(List<Lesson> lessons) throws IOException {
        saveLoadService.save(DataSaveKeys.LESSONS, lessons);
    }

    private void loadLessons() throws IOException {
        String cannotLoadMessage = "Error loading lessons";
        if (!saveLoadService.canLoad(DataSaveKeys.LESSONS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Lesson>>(){}.getType();

        List<Lesson> loadedLessons = (List<Lesson>) saveLoadService.load(DataSaveKeys.LESSONS, type);
        this.lessons = new ArrayList<>(loadedLessons);
    }

    private void validate(Lesson lesson) {
        Validation.notNull(lesson);
        Validation.notEmpty(lesson.getName());
        Validation.notNull(lesson.getType());
        Validation.notNull(lesson.getMode());
        Validation.notNull(lesson.getStartTime());
        Validation.notNull(lesson.getEndTime());
        Validation.notNull(lesson.getLanguage());
        Validation.notNull(lesson.getDayOfWeek());
        Validation.notEmpty(lesson.getNote(), true);
    }
}
