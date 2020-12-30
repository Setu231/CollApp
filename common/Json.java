
package io.collapp.common;

import com.google.gson.*;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.TimeZone;

public final class Json {

    private Json() {
    }

    public static class CustomDateSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return date == null ? JsonNull.INSTANCE : new JsonPrimitive(formatDate(date));
        }
    }

    @SuppressWarnings("InvalidTimeZoneID")
    public static String formatDate(Date date) {
        return DateFormatUtils.format(date, Constants.DATE_FORMAT, TimeZone.getTimeZone("Z"));
    }

    public static final Gson GSON = new GsonBuilder()
        .serializeNulls()
        .setDateFormat(Constants.DATE_FORMAT)
        .registerTypeHierarchyAdapter(Date.class, new Json.CustomDateSerializer())
        .create();
}
