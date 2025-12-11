package com.byt.services.scheduling;

import com.byt.data.scheduling.Specialization;
import com.byt.data.scheduling.Subject;
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

public class SpecializationService implements CRUDService<Specialization> {
    private final SaveLoadService saveLoadService;
    private List<Specialization> specializations;

    public SpecializationService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
        this.specializations = null;
    }

    @Override
    public void initialize() throws IOException {
        loadSpecializations();
    }

    @Override
    public void create(Specialization prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (exists(prototype.getName())) throw new IllegalArgumentException("Specialization already exists");

        specializations.add(Specialization.copy(prototype));
        saveAllSpecializations(specializations);
    }

    @Override
    public Optional<Specialization> get(String name)  {

        Specialization specialization = findOne(name);
        if (specialization == null) return Optional.empty();

        Specialization specializationCopy = Specialization.copy(specialization);
        return Optional.of(specializationCopy);
    }

    @Override
    public List<Specialization> getAll()  {
        if (specializations == null) return null;

        return specializations.stream()
                .map(Specialization::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String name, Specialization prototype)
            throws IllegalArgumentException, IOException, ValidationException
    {
        validate(prototype);

        if (!exists(name)) throw new IllegalArgumentException("Specialization not found");

        List<Specialization> updatedList = specializations.stream()
                .map(s -> s.getName().equals(name) ? Specialization.copy(prototype) : s)
                .collect(Collectors.toList());

        saveAllSpecializations(updatedList);
    }

    @Override
    public void delete(String name) throws IllegalArgumentException, IOException {
        Validation.notEmptyArgument(name);

        if (!exists(name)) throw new IllegalArgumentException("Specialization not found");

        int originalSize = specializations.size();

        List<Specialization> updatedSpecializations = specializations.stream()
                .filter(s -> !s.getName().equals(name))
                .collect(Collectors.toList());

        if (updatedSpecializations.size() < originalSize) {
            saveAllSpecializations(updatedSpecializations);
        }
    }

    @Override
    public boolean exists(String name)  {
        if(specializations == null|| name == null || name.isEmpty()) return false;

        return specializations.stream().
                anyMatch(s -> s.getName()
                .equals(name));
    }


    private Specialization findOne(String name) {
        if(specializations == null|| name == null || name.isEmpty()) return null;

        return this.specializations.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveAllSpecializations(List<Specialization> specializations) throws IOException{
        saveLoadService.save(DataSaveKeys.SPECIALIZATIONS, specializations);
        loadSpecializations();
    }

    private void loadSpecializations() throws IOException {
        String cannotLoadMessage = "Error loading specializations";
        if (!saveLoadService.canLoad(DataSaveKeys.SPECIALIZATIONS)) {
            throw new RuntimeException(cannotLoadMessage);
        }

        Type type = new TypeToken<List<Specialization>>(){}.getType();

        List<Specialization> loadedSpecializations =
                (List<Specialization>) saveLoadService.load(DataSaveKeys.SPECIALIZATIONS, type);

        this.specializations = new ArrayList<>(loadedSpecializations);

    }

    private void validate(Specialization specialization) {
        Validation.notNull(specialization);
        Validation.notEmpty(specialization.getName());

    }
}
