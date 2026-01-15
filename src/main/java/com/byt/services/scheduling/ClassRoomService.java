package com.byt.services.scheduling;

import com.byt.data.scheduling.ClassRoom;
import com.byt.exception.ValidationException;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.scheduling.Validator;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ClassRoomService implements CRUDService<ClassRoom> {
    private final SaveLoadService saveLoadService;
    private List<ClassRoom> classRooms;

    public ClassRoomService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.classRooms = null;
    }

    @Override
    public void initialize() throws IOException {
        loadClassRooms();
    }

    @Override
    public void create(ClassRoom prototype) throws IllegalArgumentException, IOException {
        Validator.validateClassRoom(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("ClassRoom already exists");

        classRooms.add(ClassRoom.copy(prototype));
        saveAllClassRooms(classRooms);
    }

    @Override
    public Optional<ClassRoom> get(String name) {
        ClassRoom classRoom = findOne(name);

        if (classRoom == null) return Optional.empty();

        ClassRoom classRoomCopy = ClassRoom.copy(classRoom);
        return Optional.of(classRoomCopy);
    }

    @Override
    public List<ClassRoom> getAll() {
        if (classRooms == null) return null;

        return classRooms.stream()
                .map(ClassRoom::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, ClassRoom prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        Validator.validateClassRoom(prototype);

        if (!exists(name)) throw new IllegalArgumentException("ClassRoom not found.");

        List<ClassRoom> updatedList = classRooms.stream()
                .map(r -> r.getName().equals(name) ? ClassRoom.copy(prototype) : r)
                .collect(Collectors.toList());

        saveAllClassRooms(updatedList);
    }

    @Override
    public void delete(String name)
            throws IllegalArgumentException, IOException, ValidationException
    {
        Validator.notEmptyArgument(name);
        if (!exists(name)) throw new IllegalArgumentException("ClassRoom not found.");

        int originalSize = classRooms.size();

        List<ClassRoom> updatedClassRooms = classRooms.stream()
                .filter(r -> {
                    if(!r.getName().equals(name)) return true;
                    r.removeBuilding(r.getBuilding());
                    r.getLessons().forEach(lesson -> {
                        lesson.removeClassRoom(r);
                    });
                    return false;
                })
                .collect(Collectors.toList());

        if (updatedClassRooms.size() < originalSize) {
            saveAllClassRooms(updatedClassRooms);
        }
        loadClassRooms();
    }

    @Override
    public boolean exists(String name) {
        if(classRooms == null || name == null || name.isEmpty()) return false;
        return classRooms.stream()
                .anyMatch(r -> r.getName()
                .equals(name));
    }

    private ClassRoom findOne(String name) {
        if(classRooms == null || name == null || name.isEmpty()) return null;

        return this.classRooms.stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllClassRooms(List<ClassRoom> classRooms) throws IOException {
        saveLoadService.save(DataSaveKeys.CLASSROOMS, classRooms);

        loadClassRooms();
    }

    private void loadClassRooms() throws IOException {
        String cannotLoadMessage = "Error loading classrooms";
        if (!saveLoadService.canLoad(DataSaveKeys.CLASSROOMS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<ClassRoom>>(){}.getType();
        List<ClassRoom> loadedClassRooms =
                    (List<ClassRoom>) saveLoadService.load(DataSaveKeys.CLASSROOMS, type);

        this.classRooms = new ArrayList<>(loadedClassRooms);
    }

}
