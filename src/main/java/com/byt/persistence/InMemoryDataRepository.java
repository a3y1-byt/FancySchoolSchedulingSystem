package com.byt.persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class InMemoryDataRepository implements DataRepository {
    private final Map<String, String> keyToSerializedData;

    public InMemoryDataRepository() {
        keyToSerializedData = new HashMap<>();
    }

    public InMemoryDataRepository(Map<String, String> preMadeContents) {
        keyToSerializedData = new HashMap<>(preMadeContents);
    }

    @Override
    public String read(String key) throws IOException {
        if (!exists(key))
            throw new IOException("The repository doesn't contain key '" + key + "'");

        return keyToSerializedData.get(key);
    }

    @Override
    public void write(String key, String serializedData) throws IOException {
        if (exists(key))
            remove(key);

        keyToSerializedData.put(key, serializedData);
    }

    @Override
    public void remove(String key) throws IOException {
        if (!exists(key))
            throw new IOException("The repository doesn't contain key '" + key + "'");

        keyToSerializedData.remove(key);
    }

    @Override
    public boolean exists(String key) {
        return keyToSerializedData.containsKey(key);
    }
}
