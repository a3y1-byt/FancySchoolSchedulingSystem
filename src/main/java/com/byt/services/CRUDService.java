package com.byt.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CRUDService<TEntity> {
    void create(TEntity prototype) throws IllegalArgumentException, IOException;

    Optional<TEntity> get(String id) throws IllegalArgumentException, IOException;

    List<TEntity> getAll() throws IOException;

    void update(String id, TEntity prototype) throws IllegalArgumentException, IOException;

    void delete(String id) throws IllegalArgumentException, IOException;

    boolean exists(String id) throws IOException;
}
