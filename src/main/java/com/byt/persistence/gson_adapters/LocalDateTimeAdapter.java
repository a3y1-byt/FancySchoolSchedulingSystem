package com.byt.persistence.gson_adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public final class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter writer, LocalDateTime localDateTime) throws IOException {

        writer.beginObject();

        writer.name("year");
        writer.value(localDateTime.getYear());
        writer.name("month");
        writer.value(localDateTime.getMonthValue());
        writer.name("dayOfMonth");
        writer.value(localDateTime.getDayOfMonth());
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
    public LocalDateTime read(JsonReader reader) throws IOException {
        int year = 0, month = 0, dayOfMonth = 0, hour = 0, minute = 0, second = 0, nanoOfSecond = 0;

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
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }
}
