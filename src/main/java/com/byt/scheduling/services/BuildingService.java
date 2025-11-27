package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Building;
import com.byt.scheduling.ClassRoom;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BuildingService implements CRUDService<Building> {
    private final SaveLoadService saveLoadService;
    private final ClassRoomService classRoomService;
    private  List<Building> buildings;

    public BuildingService(SaveLoadService saveLoadService, ClassRoomService classRoomService) {
        this.saveLoadService = saveLoadService;
        this.classRoomService = classRoomService;
        this.buildings = null;
        loadBuildings();
    }

    @Override
    public void create(Building prototype) throws IllegalArgumentException, IOException {
        if (prototype == null) throw new IllegalArgumentException("Prototype is null");
        if(exists(prototype.getId())) throw new IllegalArgumentException("Building already exists");

        buildings.add(Building.copy(prototype));
        saveAllBuildings(buildings);
        loadBuildings();
    }

    @Override
    public Building get(String id) throws IllegalArgumentException, IOException {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Building id is null or empty");

        Building building =  findById(id);
        if(building == null) return null;

        List<ClassRoom> classRooms = classRoomService.listClassRoomsByBuildingId(building.getId());
        return Building.copy(building, classRooms);
    }

    @Override
    public void update(String id, Building prototype) throws IllegalArgumentException, IOException {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Building id is null or empty");
        if(!exists(id)) throw new IllegalArgumentException("Building with id " + id + " does not exist");

        List<Building> updatedList = buildings.stream()
                .map(b -> b.getId().equals(id) ? Building.copy(prototype) : b)
                .collect(Collectors.toList());

        saveAllBuildings(updatedList);
        loadBuildings();
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Building id is null or empty");
        if(!exists(id)) throw new IllegalArgumentException("Building with id " + id + " does not exist");

        int originalSize = buildings.size();

        List<Building> updatedBuildings = buildings.stream()
                .filter(b -> !b.getId().equals(id))
                .collect(Collectors.toList());


        if (updatedBuildings.size() < originalSize) {
            saveAllBuildings(buildings);
        }
        loadBuildings();
    }

    @Override
    public boolean exists(String id) throws IOException {
        loadBuildings();
        return buildings.stream().anyMatch(b -> b.getId().equals(id));
    }

    private Building findById(String id) {
        return this.buildings.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void saveAllBuildings(List<Building> buildings) {
        try {
            saveLoadService.save(DataSaveKeys.BUILDINGS, buildings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save buildings", e);
        }
    }

    private void loadBuildings() {
        String cannotLoadMessage = "Error loading buildings";
        if(!saveLoadService.canLoad(DataSaveKeys.BUILDINGS)){
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Building>>(){}.getType();
        try {
            List<Building> loadedBuildings = (List<Building>) saveLoadService.load(DataSaveKeys.BUILDINGS, type);
            this.buildings = new ArrayList<>(loadedBuildings);
        } catch (IOException e) {
            throw new RuntimeException(cannotLoadMessage, e);
        }
    }
}
