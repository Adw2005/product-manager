package com.dmaxtech.productmanager.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário para serialização e desserialização de JSON utilizando Gson puro.
 */
public class JsonUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(FORMATTER));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    if (json == null || json.isJsonNull() || json.getAsString().isBlank()) {
                        return null;
                    }
                    return LocalDateTime.parse(json.getAsString(), FORMATTER);
                }
            })
            .create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static Gson getGson() {
        return GSON;
    }
}

