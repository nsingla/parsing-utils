package io.nsingla.deserializers;

import io.nsingla.annotations.ExportDeserializer;
import io.nsingla.formatters.DateTimeFormattingUtils;
import org.junit.platform.commons.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeExportDeserializer extends ExportDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(Object valueToParse) {
        String stringToParse = String.valueOf(valueToParse);
        if(StringUtils.isNotBlank(stringToParse)) {
            if(stringToParse.contains("+")) {
                return LocalDateTime.parse(stringToParse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } else {
                return LocalDateTime.parse(stringToParse, DateTimeFormattingUtils.dateTimeFormatters);
            }
        } else {
            return null;
        }
    }
}
