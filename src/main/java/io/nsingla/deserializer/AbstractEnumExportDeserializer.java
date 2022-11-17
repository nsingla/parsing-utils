package io.nsingla.deserializer;

import io.nsingla.annotations.ExportDeserializer;

import java.util.Arrays;

public class AbstractEnumExportDeserializer<E extends Enum> extends ExportDeserializer<E> {

    private final Class<E> clazz;

    public AbstractEnumExportDeserializer(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E deserialize(Object valueToParse) {
        String stringToParse = String.valueOf(valueToParse);
        return Arrays.stream(clazz.getEnumConstants())
            .filter(s -> s.name().equalsIgnoreCase(stringToParse))
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("'" + stringToParse + "' is not a valid " + clazz.getSimpleName() + " value")
            );
    }
}