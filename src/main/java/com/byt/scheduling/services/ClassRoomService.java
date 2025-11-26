package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.ClassRoom;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ClassRoomService {
    private final SaveLoadService saveLoadService;

    public ClassRoomService(SaveLoadService saveLoadService, BuildingService buildingService) {
        this.saveLoadService = saveLoadService;
    }

    public ClassRoom addClassRoom(String id, String name, int floor, int capacity, String buildingId) {
        List<ClassRoom> classRooms = listClassRooms();

        boolean exists = classRooms.stream().anyMatch(r -> r.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("ClassRoom with id " + id + " already exists");
        }

        ClassRoom classRoom = ClassRoom.builder()
                .id(id)
                .name(name)
                .floor(floor)
                .capacity(capacity)
                .buildingId(buildingId)
                .build();

        classRooms.add(classRoom);
        saveAllClassRooms(classRooms);
        return classRoom;
    }

    public Optional<ClassRoom> getClassRoomById(String id) {
        return listClassRooms().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }


    public List<ClassRoom> listClassRooms() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.CLASSROOMS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<ClassRoom>>(){}.getType();
            List<ClassRoom> classRooms = (List<ClassRoom>) saveLoadService.load(DataSaveKeys.CLASSROOMS, type);
            return new ArrayList<>(classRooms);
        } catch (IOException e) {
            System.err.println("Failed to load classrooms: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ClassRoom updateClassRoom(String id, String name, int floor, int capacity, String buildingId) {

        List<ClassRoom> classRooms = listClassRooms();

        ClassRoom existingRoom = getClassRoomById(id)
                .orElseThrow(() -> new IllegalArgumentException("ClassRoom with id " + id + " not found"));

        ClassRoom updatedRoom = ClassRoom.builder()
                .id(id)
                .name(name)
                .floor(floor)
                .capacity(capacity)
                .buildingId(buildingId)
                .build();

        classRooms = classRooms.stream()
                .map(r -> r.getId().equals(id) ? updatedRoom : r)
                .collect(Collectors.toList());

        saveAllClassRooms(classRooms);
        return updatedRoom;
    }

    public boolean deleteClassRoom(String id) {
        List<ClassRoom> classRooms = listClassRooms();

        boolean removed = classRooms.removeIf(r -> r.getId().equals(id));
        if (removed) {
            saveAllClassRooms(classRooms);
        }
        return removed;
    }

    private void saveAllClassRooms(List<ClassRoom> classRooms) {
        try {
            saveLoadService.save(DataSaveKeys.CLASSROOMS, classRooms);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save classrooms", e);
        }
    }
}