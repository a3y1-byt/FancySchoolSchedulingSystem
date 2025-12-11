package com.byt.services.scheduling;

import com.byt.data.scheduling.Group;
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

public class GroupService implements CRUDService<Group> {
    private final SaveLoadService saveLoadService;
    private List<Group> groups;

    public GroupService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
    }

    @Override
    public void initialize() throws IOException {
        loadGroups();
    }

    @Override
    public void create(Group prototype)
            throws IllegalArgumentException, ValidationException, IOException
    {
        validate(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("Group already exists");

        groups.add(Group.copy(prototype));
        saveAllGroups(groups);
    }

    @Override
    public Optional<Group> get(String name) {
        Group group = findOne(name);
        if (group == null) return Optional.empty();

        Group groupCopy = Group.copy(group);
        return Optional.of(groupCopy);
    }

    @Override
    public List<Group> getAll() {
        if (groups == null) return null;

        return groups.stream()
                .map(Group::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, Group prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (!exists(name)) throw new IllegalArgumentException("Group not found");

        List<Group> updatedList = groups.stream()
                .map(g -> g.getName().equals(name) ? Group.copy(prototype) : g)
                .collect(Collectors.toList());

        saveAllGroups(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException {
        Validation.notEmptyArgument(name);

        if (!exists(name)) throw new IllegalArgumentException("Group not found");

        int originalSize = groups.size();

        List<Group> updatedGroups = groups.stream()
                .filter(g -> !g.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedGroups.size() < originalSize) {
            saveAllGroups(updatedGroups);
        }
    }

    @Override
    public boolean exists(String name) throws IOException {
        if(groups == null || name == null || name.isEmpty()) return false;

        return groups.stream()
                .anyMatch(g -> g.getName()
                .equals(name));
    }

    private Group findOne(String name) {
        if(groups == null || name == null || name.isEmpty()) return null;

        return this.groups.stream()
                .filter(g -> g.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllGroups(List<Group> groups) throws IOException {
        saveLoadService.save(DataSaveKeys.GROUPS, groups);
        loadGroups();
    }

    private void loadGroups()  throws IOException {
        String cannotLoadMessage = "Error loading groups";
        if (!saveLoadService.canLoad(DataSaveKeys.GROUPS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Group>>(){}.getType();
        List<Group> loadedGroups =
                (List<Group>) saveLoadService.load(DataSaveKeys.GROUPS, type);
        this.groups = new ArrayList<>(loadedGroups);
    }

    private void validate(Group group) throws ValidationException {
        Validation.notNull(group);
        Validation.notEmpty(group.getName());
        Validation.checkMin(group.getMinCapacity(), Group.MIN_CAPACITY);
        Validation.checkMin(group.getMaxCapacity(), Group.MIN_CAPACITY);
        Validation.checkMax(group.getMaxCapacity(), Group.MAX_CAPACITY);
        Validation.notNull(group.getLanguage());
        Validation.notNull(group.getYearOfStudy());
    }

}
