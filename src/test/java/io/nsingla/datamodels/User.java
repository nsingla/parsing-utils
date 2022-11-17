package io.nsingla.datamodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.nsingla.deserializers.CustomDateTimeExportDeserializer;
import io.nsingla.annotations.csv.CsvDeserialize;
import io.nsingla.annotations.csv.CsvProperty;
import io.nsingla.annotations.excel.ExcelDeserialize;
import io.nsingla.annotations.excel.ExcelProperty;
import io.nsingla.deserializers.CustomDateTimeJsonDeserializer;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    @CsvProperty("creation date")
    @ExcelProperty("creation date")
    @JsonProperty("creation date")
    @CsvDeserialize(using = CustomDateTimeExportDeserializer.class)
    @ExcelDeserialize(using = CustomDateTimeExportDeserializer.class)
    @JsonDeserialize(using = CustomDateTimeJsonDeserializer.class)
    private LocalDateTime creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", creationDate='" + creationDate + '\'' +
            '}';
    }
}
