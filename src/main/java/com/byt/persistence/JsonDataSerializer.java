package com.byt.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public final class JsonDataSerializer implements DataSerializer {
    private final Gson gsonSerializer = new GsonBuilder().create();

    @Override
    public String serialize(Object object) {
        return gsonSerializer.toJson(object);
    }

    @Override
    public Object deserialize(String serializedObject, Type type) {
        return gsonSerializer.fromJson(serializedObject, type);
    }
}
