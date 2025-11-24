package com.byt.persistence;

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

    public Object load(String key, Type type) throws IOException {
        String serializedObject = repository.read(key);
        return serializer.deserialize(serializedObject, type);
    }

    public boolean trySave(String key, Object data) {
        try {
            save(key, data);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    public void save(String key, Object data) throws IOException {
        String serializedObject = serializer.serialize(data);
        repository.write(key, serializedObject);
    }
}
