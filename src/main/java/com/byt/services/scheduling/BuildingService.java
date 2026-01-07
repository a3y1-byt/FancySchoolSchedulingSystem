package com.byt.services.scheduling;

import com.byt.data.scheduling.Building;
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

public class BuildingService implements CRUDService<Building> {
    private final SaveLoadService saveLoadService;
    private  List<Building> buildings;

    public BuildingService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.buildings = null;
    }

    @Override
    public void initialize() throws IOException {
        loadBuildings();
    }


    @Override
    public Optional<Building> get(String name) {
        Building building =  findOne(name);
        if(building == null) return Optional.empty();

        Building buildingCopy = Building.copy(building);
        return Optional.of(buildingCopy);
    }

    @Override
    public List<Building> getAll() {
        if (buildings == null) return null;
        return  buildings.stream()
                .map(Building::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void create(Building prototype)
            throws ValidationException, IllegalArgumentException, IOException
    {
        Validator.validateBuilding(prototype);

        boolean exists = exists(prototype.getName());
        if(exists) throw new IllegalArgumentException("Building already exists");

        buildings.add(Building.copy(prototype));
        saveAllBuildings(buildings);
    }

    @Override
    public void update(String name, Building prototype)
            throws IllegalArgumentException, ValidationException, IOException
    {
        Validator.validateBuilding(prototype);

        if(!exists(name)) throw new IllegalArgumentException("Building not found");

        List<Building> updatedList = buildings.stream()
                .map(b -> b.getName().equals(name) ? Building.copy(prototype) : b)
                .collect(Collectors.toList());

        saveAllBuildings(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException {
        Validator.notEmptyArgument(name);

        if(!exists(name)) throw new IllegalArgumentException("Building not found");

        int originalSize = buildings.size();

        List<Building> updatedBuildings = buildings.stream()
                .filter(b -> !b.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedBuildings.size() < originalSize) {
            saveAllBuildings(updatedBuildings);
        }
    }

    @Override
    public boolean exists(String name) {
        if(buildings == null || name == null || name.isEmpty()) return false;

        return buildings.stream()
                .anyMatch(b -> b.getName()
                .equals(name));
    }

    private Building findOne(String name) {
        if(buildings == null || name == null || name.isEmpty()) return null;

        return this.buildings.stream()
                .filter(b -> b.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllBuildings(List<Building> buildings) throws IOException {
        saveLoadService.save(DataSaveKeys.BUILDINGS, buildings);

        loadBuildings();
    }

    private void loadBuildings() throws IOException {
        String cannotLoadMessage = "Error loading buildings";
        if(!saveLoadService.canLoad(DataSaveKeys.BUILDINGS)){
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Building>>(){}.getType();
        List<Building> loadedBuildings =
                    (List<Building>) saveLoadService.load(DataSaveKeys.BUILDINGS, type);

            this.buildings = new ArrayList<>(loadedBuildings);
    }



}
