package com.byt.persistence;

import com.byt.persistence.util.DataSaveKeys;

import java.io.IOException;
import java.lang.reflect.Type;

public final class SaveLoadService {
    private final DataSerializer serializer;
    private final DataRepository repository;

    public SaveLoadService(DataSerializer serializer, DataRepository repository) {
        this.serializer = serializer;
        this.repository = repository;
    }

    public boolean canLoad(String key) {
        return repository.exists(key);
    }

    public boolean canLoad(DataSaveKeys key) {
        return canLoad(key.repositoryKey);
    }

    public Object load(String key, Type type) throws IOException {
        String serializedObject = repository.read(key);
        return serializer.deserialize(serializedObject, type);
    }

    public Object load(DataSaveKeys key, Type type) throws IOException {
        return load(key.repositoryKey, type);
    }

    public boolean trySave(String key, Object data) {
        try {
            save(key, data);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    public boolean trySave(DataSaveKeys key, Object data) {
        return trySave(key.repositoryKey, data);
    }

    public void save(String key, Object data) throws IOException {
        String serializedObject = serializer.serialize(data);
        repository.write(key, serializedObject);
    }

    public void save(DataSaveKeys key, Object data) throws IOException {
        save(key.repositoryKey, data);
    }
}
