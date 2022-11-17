package io.nsingla.annotations.csv;

import io.nsingla.annotations.ExportDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvDeserialize {
    Class<? extends ExportDeserializer> using() default ExportDeserializer.None.class;
}
