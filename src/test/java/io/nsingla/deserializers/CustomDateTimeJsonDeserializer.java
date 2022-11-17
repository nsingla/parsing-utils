package io.nsingla.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.nsingla.formatters.DateTimeFormattingUtils;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String stringToParse = jsonParser.getText();
        if (StringUtils.isNotBlank(stringToParse)) {
            if (stringToParse.contains("+")) {
                return LocalDateTime.parse(stringToParse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } else {
                return LocalDateTime.parse(stringToParse, DateTimeFormattingUtils.dateTimeFormatters);
            }
        } else {
            return null;
        }
    }
}
