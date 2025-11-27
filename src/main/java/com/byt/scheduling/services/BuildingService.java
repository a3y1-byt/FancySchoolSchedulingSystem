package com.byt.scheduling.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.scheduling.Building;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BuildingService {
    private final SaveLoadService saveLoadService;

    public BuildingService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
    }

    public List<Building> listBuildings() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.BUILDINGS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Building>>(){}.getType();
            return new ArrayList<>((List<Building>) saveLoadService.load(DataSaveKeys.BUILDINGS, type));
        } catch (IOException e) {
            System.err.println("Failed to load buildings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Building> getBuildingById(String id) {
        return listBuildings().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    public Building addBuilding(String id, String name, String address, String description) {
        List<Building> buildings = listBuildings();

        boolean exists = buildings.stream().anyMatch(b -> b.getId().equals(id));
        if (exists) {
            throw new IllegalArgumentException("Building with id " + id + " already exists");
        }

        Building building = Building.builder()
                .id(id)
                .name(name)
                .address(address)
                .description(description)
                .classRooms(new ArrayList<>())
                .build();

        buildings.add(building);
        saveAllBuildings(buildings);
        return building;
    }

    public Building updateBuilding(String id, String name, String address, String description) {
        List<Building> buildings = listBuildings();

        Building existingBuilding = getBuildingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Building with id " + id + " not found"));

        Building updatedBuilding = Building.builder()
                .id(id)
                .name(name)
                .address(address)
                .description(description)
                .classRooms(existingBuilding.getClassRooms())
                .build();

        buildings = buildings.stream()
                .map(b -> b.getId().equals(id) ? updatedBuilding : b)
                .collect(Collectors.toList());

        saveAllBuildings(buildings);
        return updatedBuilding;
    }

    public boolean deleteBuilding(String id) {
        List<Building> buildings = listBuildings();

        boolean removed = buildings.removeIf(b -> b.getId().equals(id));
        if (removed) {
            saveAllBuildings(buildings);
        }
        return removed;
    }

    private void saveAllBuildings(List<Building> buildings) {
        try {
            saveLoadService.save(DataSaveKeys.BUILDINGS, buildings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save buildings", e);
        }
    }
}

