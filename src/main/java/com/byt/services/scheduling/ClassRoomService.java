package com.byt.services.scheduling;

import com.byt.data.scheduling.ClassRoom;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
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
        String cannotLoadMessage = "Error loading classrooms";
        if (!saveLoadService.canLoad(DataSaveKeys.CLASSROOMS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<ClassRoom>>(){}.getType();
        try {
            List<ClassRoom> loadedClassRooms = (List<ClassRoom>) saveLoadService.load(DataSaveKeys.CLASSROOMS, type);
            this.classRooms = new ArrayList<>(loadedClassRooms);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    @Override
    public void create(ClassRoom prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (exists(prototype.getId())) throw new IllegalArgumentException("ClassRoom already exists");

        classRooms.add(ClassRoom.copy(prototype));
        saveAllClassRooms(classRooms);
        loadClassRooms();
    }

    @Override
    public Optional<ClassRoom> get(String id) throws IllegalArgumentException, IOException {
        ClassRoom classRoom = findById(id);
        if (classRoom == null) return Optional.empty();
        ClassRoom classRoomCopy = ClassRoom.copy(classRoom);
        return Optional.of(classRoomCopy);
    }

    @Override
    public List<ClassRoom> getAll() throws IOException {
        return classRooms.stream()
                .map(ClassRoom::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, ClassRoom prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if (!exists(id)) throw new IllegalArgumentException("ClassRoom with id " + id + " does not exist");

        List<ClassRoom> updatedList = classRooms.stream()
                .map(r -> r.getId().equals(id) ? ClassRoom.copy(prototype) : r)
                .collect(Collectors.toList());

        saveAllClassRooms(updatedList);
        loadClassRooms();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ClassRoom id is null or empty");
        if (!exists(id)) throw new IllegalArgumentException("ClassRoom with id " + id + " does not exist");

        int originalSize = classRooms.size();

        List<ClassRoom> updatedClassRooms = classRooms.stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());

        if (updatedClassRooms.size() < originalSize) {
            saveAllClassRooms(updatedClassRooms);
        }
        loadClassRooms();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadClassRooms();
        return classRooms.stream().anyMatch(r -> r.getId().equals(id));
    }

    public List<ClassRoom> listClassRoomsByBuildingId(String buildingId) {
        if(this.classRooms == null) return new ArrayList<>();
        return classRooms.stream()
                .filter(r -> r.getBuildingId().equals(buildingId))
                .map(ClassRoom::copy)
                .collect(Collectors.toList());
    }

    private ClassRoom findById(String id) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ClassRoom id is null or empty");

        return this.classRooms.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllClassRooms(List<ClassRoom> classRooms) {
        try {
            saveLoadService.save(DataSaveKeys.CLASSROOMS, classRooms);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save classrooms", e);
        }
    }

    private void loadClassRooms() {
        String cannotLoadMessage = "Error loading classrooms";
        if (!saveLoadService.canLoad(DataSaveKeys.CLASSROOMS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<ClassRoom>>(){}.getType();
        try {
            List<ClassRoom> loadedClassRooms = (List<ClassRoom>) saveLoadService.load(DataSaveKeys.CLASSROOMS, type);
            this.classRooms = new ArrayList<>(loadedClassRooms);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }

    private void validate(ClassRoom classRoom) {
        List<String> errors = new ArrayList<>();

        if (classRoom == null) {
            throw new IllegalArgumentException("ClassRoom cannot be null");
        }

        if (classRoom.getId() == null || classRoom.getId().trim().isEmpty()) {
            errors.add("ClassRoom ID is required");
        }

        if (classRoom.getName() == null || classRoom.getName().trim().isEmpty()) {
            errors.add("ClassRoom name is required");
        }

        if (classRoom.getCapacity() <= 0) {
            errors.add("ClassRoom capacity must be positive");
        }

        if (classRoom.getBuildingId() == null || classRoom.getBuildingId().trim().isEmpty()) {
            errors.add("Building ID is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }
}
