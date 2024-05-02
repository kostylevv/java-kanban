package web.handler.taskhandler;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String timeString = reader.nextString();
        return LocalDateTime.parse(timeString, timeFormatter);
    }

    @Override
    public void write(JsonWriter writer, LocalDateTime time) throws IOException {
        if (time == null) {
            writer.nullValue();
            return;
        }
        writer.value(time.format(timeFormatter));
    }
}