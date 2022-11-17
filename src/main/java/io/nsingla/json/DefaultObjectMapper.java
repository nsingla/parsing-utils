package io.nsingla.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DefaultObjectMapper {

    private static final ObjectMapper defaultObjectMapper = getObjectMapper();
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static ObjectMapper getObjectMapper() {
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        return new ObjectMapper()
            .registerModule(new AfterburnerModule())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false) // Don't wrap the root value
            .setSerializationInclusion(JsonInclude.Include.NON_NULL) // Don't serialize null values
            .enable(JsonParser.Feature.ALLOW_COMMENTS)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) // Accept case insensitive
            .setDateFormat(dateFormat)
            .setDefaultPrettyPrinter(new DefaultPrettyPrinter())
            .setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
    }

    public static ObjectMapper getInstance() {
        return defaultObjectMapper;
    }
}