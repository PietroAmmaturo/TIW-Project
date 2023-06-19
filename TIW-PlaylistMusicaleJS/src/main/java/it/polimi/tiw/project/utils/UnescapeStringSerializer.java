package it.polimi.tiw.project.utils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UnescapeStringSerializer implements JsonSerializer < String > {
    @Override
    public JsonElement serialize(String escapedString, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(StringEscapeUtils.unescapeJava(escapedString));
    }

}