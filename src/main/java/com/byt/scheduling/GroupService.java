package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class GroupService implements CRUDService<Group> {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;
    private List<Group> groups;

    public GroupService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = new LessonService(saveLoadService);
    }

    @Override
    public void initialize() throws IOException {
        String cannotLoadMessage = "Error loading groups";
        if (!saveLoadService.canLoad(DataSaveKeys.GROUPS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Group>>(){}.getType();
        try {
            List<Group> loadedGroups = (List<Group>) saveLoadService.load(DataSaveKeys.GROUPS, type);
            this.groups = new ArrayList<>(loadedGroups);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    @Override
    public void create(Group prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (exists(prototype.getId())) throw new IllegalArgumentException("Group already exists");

        groups.add(Group.copy(prototype));
        saveAllGroups(groups);
        loadGroups();
    }

    @Override
    public Optional<Group> get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Group id is null or empty");

        Group group = findById(id);
        if (group == null) return Optional.empty();

        List<Lesson> lessons = lessonService.listLessonsByGroupId(id);
        Group groupCopy = Group.copy(group, lessons);
        return Optional.of(groupCopy);
    }

    @Override
    public List<Group> getAll() throws IOException {
        return groups.stream()
                .map(Group::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Group prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (!exists(id)) throw new IllegalArgumentException("Group with id " + id + " does not exist");

        List<Group> updatedList = groups.stream()
                .map(g -> g.getId().equals(id) ? Group.copy(prototype) : g)
                .collect(Collectors.toList());

        saveAllGroups(updatedList);
        loadGroups();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Group id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("Group with id " + id + " does not exist");

        int originalSize = groups.size();

        List<Group> updatedGroups = groups.stream()
                .filter(g -> !g.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedGroups.size() < originalSize) {
            saveAllGroups(updatedGroups);
        }
        loadGroups();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadGroups();
        return groups.stream().anyMatch(g -> g.getId().equals(id));
    }

    private Group findById(String id) {
        if(this.groups == null || this.groups.isEmpty()) return null;

        return this.groups.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllGroups(List<Group> groups) {
        try {
            saveLoadService.save(DataSaveKeys.GROUPS, groups);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save groups", e);
        }
    }

    private void loadGroups() {
        String cannotLoadMessage = "Error loading groups";
        if (!saveLoadService.canLoad(DataSaveKeys.GROUPS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Group>>(){}.getType();
        try {
            List<Group> loadedGroups = (List<Group>) saveLoadService.load(DataSaveKeys.GROUPS, type);
            this.groups = new ArrayList<>(loadedGroups);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    private void validate(Group group) {
        List<String> errors = new ArrayList<>();

        if (group == null) {
            throw new IllegalArgumentException("Group cannot be null");
        }

        if (group.getId() == null || group.getId().trim().isEmpty()) {
            errors.add("Group ID is required");
        }

        if (group.getName() == null || group.getName().trim().isEmpty()) {
            errors.add("Group name is required");
        }

        if (group.getLanguage() == null || group.getLanguage().trim().isEmpty()) {
            errors.add("Group language is required");
        }

        if (group.getMaxCapacity() <= 0) {
            errors.add("Group max capacity must be positive");
        }

        if (group.getMinCapacity() <= 0) {
            errors.add("Group min capacity must be positive");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }

}
