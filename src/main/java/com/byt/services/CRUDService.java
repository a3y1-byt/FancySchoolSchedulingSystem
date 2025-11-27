package com.byt.services;

import java.io.IOException;

public interface CRUDService<TEntity> {
    void create(TEntity prototype) throws IllegalArgumentException, IOException;

    TEntity get(String id) throws IllegalArgumentException, IOException;

    void update(String id, TEntity prototype) throws IllegalArgumentException, IOException;

    void delete(String id) throws IllegalArgumentException, IOException;

    boolean exists(String id) throws IOException;
}
