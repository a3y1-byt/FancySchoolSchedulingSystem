package com.byt.persistence;

import java.io.IOException;

public interface DataRepository {
    public String read(String key) throws IOException;

    public void write(String key, String serializedData) throws IOException;

    public void remove(String key) throws IOException;

    public boolean exists(String key);
}

