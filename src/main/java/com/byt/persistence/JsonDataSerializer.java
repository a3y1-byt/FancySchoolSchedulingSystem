package com.byt.persistence;

import com.byt.persistence.gson_adapters.LocalDateAdapter;
import com.byt.persistence.gson_adapters.LocalDateTimeAdapter;
import com.byt.persistence.gson_adapters.LocalTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class JsonDataSerializer implements DataSerializer {
    private final Gson gsonSerializer = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @Override
    public String serialize(Object object) {
        return gsonSerializer.toJson(object);
    }

    @Override
    public Object deserialize(String serializedObject, Type type) {
        return gsonSerializer.fromJson(serializedObject, type);
    }
}

