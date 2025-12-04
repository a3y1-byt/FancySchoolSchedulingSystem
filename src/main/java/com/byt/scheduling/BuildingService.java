package com.byt.scheduling;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
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

    public BuildingService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.classRoomService = new ClassRoomService(saveLoadService);
        this.buildings = null;
        loadBuildings();
    }

    @Override
    public void initialize() throws IOException {
        CRUDService.super.initialize();
    }

    @Override
    public void create(Building prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

        if(exists(prototype.getId())) throw new IllegalArgumentException("Building already exists");

        buildings.add(Building.copy(prototype));
        saveAllBuildings(buildings);
        loadBuildings();
    }

    @Override
    public Optional<Building> get(String id) throws IllegalArgumentException, IOException {
        Building building =  findById(id);

        if(building == null) return Optional.empty();
        List<ClassRoom> classRooms = classRoomService.listClassRoomsByBuildingId(building.getId());
        Building buildingCopy = Building.copy(building, classRooms);
        return Optional.of(buildingCopy);
    }

    @Override
    public List<Building> getAll() throws IOException {
        return  buildings.stream()
                .map(Building::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, Building prototype) throws IllegalArgumentException, IOException {
        validate(prototype);

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
            saveAllBuildings(updatedBuildings);
        }
        loadBuildings();
    }

    @Override
    public boolean exists(String id) throws IOException {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Building id is null or empty");
        loadBuildings();
        return buildings.stream().anyMatch(b -> b.getId().equals(id));
    }

    private Building findById(String id) {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Building id is null or empty");

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

    private void validate(Building building) {
        List<String> errors = new ArrayList<>();

        if (building == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }

        if (building.getId() == null || building.getId().trim().isEmpty()) {
            errors.add("Building ID is required");
        }

        if (building.getName() == null || building.getName().trim().isEmpty()) {
            errors.add("Building name is required");
        }

        if (building.getAddress() == null || building.getAddress().trim().isEmpty()) {
            errors.add("Building address is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }
}
