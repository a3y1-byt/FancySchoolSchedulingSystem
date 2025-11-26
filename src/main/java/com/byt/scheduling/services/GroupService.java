package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Group;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class GroupService {
    private final SaveLoadService saveLoadService;

    public GroupService(SaveLoadService saveLoadService, LessonService lessonService) {
        this.saveLoadService = saveLoadService;

    }

    public Group addGroup(String id, String name, String language, int maxCapacity, int minCapacity, int yearOfStudy) {
        List<Group> groups = listGroups();

        boolean exists = groups.stream().anyMatch(g -> g.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Group with id " + id + " already exists");
        }

        Group group = Group.builder()
                .id(id)
                .name(name)
                .language(language)
                .maxCapacity(maxCapacity)
                .minCapacity(minCapacity)
                .yearOfStudy(yearOfStudy)
                .notes(new ArrayList<>())
                .lessons(new ArrayList<>())
                .students(new ArrayList<>())
                .build();

        groups.add(group);
        saveAllGroups(groups);
        return group;
    }

    public Optional<Group> getGroupById(String id) {
        return listGroups().stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
    }

    public List<Group> listGroups() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.GROUPS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Group>>(){}.getType();
            List<Group> groups = (List<Group>) saveLoadService.load(DataSaveKeys.GROUPS, type);
            return new ArrayList<>(groups);
        } catch (IOException e) {
            System.err.println("Failed to load groups: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void updateGroup(String id, String name, String language, int maxCapacity, int minCapacity, int yearOfStudy, List<String> notes) {

        Group group = getGroupById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group with id " + id + " not found"));
        List<Group> groups = listGroups();

        Group updatedGroup = Group.builder()
                .id(id)
                .name(name)
                .language(language)
                .maxCapacity(maxCapacity)
                .minCapacity(minCapacity)
                .yearOfStudy(yearOfStudy)
                .notes(new ArrayList<>(notes))
                .build();

        groups = groups.stream()
                .map(g -> g.getId().equals(id) ? updatedGroup : g)
                .collect(Collectors.toList());

        saveAllGroups(groups);
    }

    public boolean deleteGroup(String id) {
        List<Group> groups = listGroups();

        boolean removed = groups.removeIf(g -> g.getId().equals(id));
        if (removed) {
            saveAllGroups(groups);
        }
        return removed;
    }

    private void saveAllGroups(List<Group> groups) {
        try {
            saveLoadService.save(DataSaveKeys.GROUPS, groups);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save groups", e);
        }
    }
}
