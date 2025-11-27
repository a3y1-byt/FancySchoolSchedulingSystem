package com.byt.persistence.gson_adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;

public final class LocalTimeAdapter extends TypeAdapter<LocalTime> {
    @Override
    public void write(JsonWriter writer, LocalTime localDateTime) throws IOException {

        writer.beginObject();

        writer.name("hour");
        writer.value(localDateTime.getHour());
        writer.name("minute");
        writer.value(localDateTime.getMinute());
        writer.name("second");
        writer.value(localDateTime.getSecond());
        writer.name("nanoOfSecond");
        writer.value(localDateTime.getNano());

        writer.endObject();
    }

    @Override
    public LocalTime read(JsonReader reader) throws IOException {
        int hour = 0, minute = 0, second = 0, nanoOfSecond = 0;

        reader.beginObject();
        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldName = reader.nextName();
            }

            switch (fieldName)
            {
                case "hour":
                    hour = reader.nextInt();
                    break;

                case "minute":
                    minute = reader.nextInt();
                    break;

                case "second":
                    second = reader.nextInt();
                    break;

                case "nanoOfSecond":
                    nanoOfSecond = reader.nextInt();
                    break;

                default:
                    break;
            }
        }

        reader.endObject();
        return LocalTime.of(hour, minute, second, nanoOfSecond);
    }
}
