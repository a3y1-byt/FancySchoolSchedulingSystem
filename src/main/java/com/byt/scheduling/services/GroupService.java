package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Group;
import com.byt.scheduling.Lesson;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class GroupService implements CRUDService<Group> {
    private final SaveLoadService saveLoadService;
    private final LessonService lessonService;
    private List<Group> groups;

    public GroupService(SaveLoadService saveLoadService, LessonService lessonService) {
        this.saveLoadService = saveLoadService;
        this.lessonService = lessonService;
        this.groups = null;
        loadGroups();
    }

    @Override
    public void create(Group prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) throw new IllegalArgumentException("Prototype is null");
        if (exists(prototype.getId())) throw new IllegalArgumentException("Group already exists");

        groups.add(Group.copy(prototype));
        saveAllGroups(groups);
        loadGroups();
    }

    @Override
    public Group get(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Group id is null or empty");

        Group group = findById(id);
        if (group == null) return null;

        List<Lesson> lessons = lessonService.listLessonsByGroupId(id);

        return Group.copy(group);
    }

    @Override
    public void update(String id, Group prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Group id is null or empty");
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
}
