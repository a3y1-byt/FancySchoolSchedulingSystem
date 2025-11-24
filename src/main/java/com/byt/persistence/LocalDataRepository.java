package com.byt.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LocalDataRepository implements DataRepository {
    private final Path savesPath;

    public LocalDataRepository(Path savesPath) {
        this.savesPath = savesPath;
    }

    @Override
    public String read(String key) throws IOException {
        return Files.readString(getSaveFilePath(key));
    }

    @Override
    public void write(String key, String serializedData) throws IOException {
        if (!exists(key))
            Files.createFile(getSaveFilePath(key));

        Files.write(getSaveFilePath(key), serializedData.getBytes());
    }

    @Override
    public void remove(String key) throws IOException {
        Files.delete(getSaveFilePath(key));
    }

    @Override
    public boolean exists(String key) {
        return Files.exists(getSaveFilePath(key));
    }

    private Path getSaveFilePath(String key) {
        return Path.of(savesPath.toString(), key + ".json");
    }
}
