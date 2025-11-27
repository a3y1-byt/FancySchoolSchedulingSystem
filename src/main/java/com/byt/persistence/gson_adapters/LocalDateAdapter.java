package com.byt.persistence.gson_adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public final class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter writer, LocalDate localDate) throws IOException {

        writer.beginObject();

        writer.name("year");
        writer.value(localDate.getYear());
        writer.name("month");
        writer.value(localDate.getMonthValue());
        writer.name("dayOfMonth");
        writer.value(localDate.getDayOfMonth());

        writer.endObject();
    }

    @Override
    public LocalDate read(JsonReader reader) throws IOException {
        int year = 0, month = 0, dayOfMonth = 0;

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
                case "year":
                    year = reader.nextInt();
                    break;

                case "month":
                    month = reader.nextInt();
                    break;

                case "dayOfMonth":
                    dayOfMonth = reader.nextInt();
                    break;

                default:
                    break;
            }
        }

        reader.endObject();
        return LocalDate.of(year, month, dayOfMonth);
    }
}
