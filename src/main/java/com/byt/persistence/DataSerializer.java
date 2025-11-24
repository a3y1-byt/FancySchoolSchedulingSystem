package com.byt.persistence;

import java.lang.reflect.Type;

public interface DataSerializer {
    public String serialize(Object object);

    public Object deserialize(String serializedObject, Type type);
}
