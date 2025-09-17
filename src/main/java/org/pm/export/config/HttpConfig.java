package org.pm.export.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

public final class HttpConfig {
    private HttpConfig() {}

    public static final URI BASE_URI = URI.create(
            System.getProperty("converter.baseUrl", "http://localhost:4000")
    );
    public static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    /** Gson that serializes LocalDate as yyyy-MM-dd */
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class,
            new TypeAdapter<LocalDate>(){
                @Override
                public void write(JsonWriter jsonWriter, LocalDate localDate)  throws IOException {
                    jsonWriter.value(localDate.toString());
                }
                @Override
                public LocalDate read(JsonReader jsonReader) throws IOException {
                    return LocalDate.parse(jsonReader.nextString());
                }
            }).create();
}
