package io.nsingla.csv;

import io.nsingla.annotations.IgnoreProperty;
import io.nsingla.annotations.csv.CsvDeserialize;
import io.nsingla.annotations.csv.CsvProperty;
import io.nsingla.formatters.DateTimeFormattingUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericCsvParser<T> {
    private final String listSeparator = ",\\s?";
    private Class<T> clazz;
    private HashMap<String, Integer> colIdxMap = new HashMap<>();
    private CSVReader csvReader;
    private List<T> listOfObjectsFromCsv;

    private GenericCsvParser(Class<T> clazz, Integer numberOfLinesToSkip, Reader reader) {
        this.clazz = clazz;
        CSVParser parser = new CSVParserBuilder()
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .build();
        csvReader = new CSVReaderBuilder(reader)
            .withSkipLines(numberOfLinesToSkip != null ? numberOfLinesToSkip : 0)
            .withCSVParser(parser)
            .build();
        this.listOfObjectsFromCsv = parseToJavaObject();
    }

    public GenericCsvParser(Class<T> clazz, Integer numberOfLinesToSkip, String filePath) throws FileNotFoundException {
        this(clazz, numberOfLinesToSkip, new FileReader(filePath));
    }

    public GenericCsvParser(Class<T> clazz, Integer numberOfLinesToSkip, InputStream is) {
        this(clazz, numberOfLinesToSkip, new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
    }

    public GenericCsvParser(Class<T> clazz, String filePath) throws FileNotFoundException {
        this(clazz, null, filePath);
    }

    private List<T> parseToJavaObject() {
        List<T> listOfMappedObjects = new ArrayList<>();
        try {
            List<String[]> allLines = csvReader.readAll();
            AtomicInteger lineNumber = new AtomicInteger(0);
            allLines.stream().forEach(nextLine -> {
                if (clazz.isAssignableFrom(Map.class)) {
                    if (nextLine.length > 1) {
                        Map pair = new HashMap();
                        List<Object> values = new ArrayList<>();
                        for (int i = 1; i < nextLine.length; i++) {
                            values.add(nextLine[i]);
                        }
                        if (values.size() == 1) {
                            pair.put(nextLine[0], values.get(0));
                        } else {
                            pair.put(nextLine[0], values);
                        }
                        listOfMappedObjects.add((T) pair);
                    }
                } else {
                    if (lineNumber.getAndIncrement() == 0) {
                        for (int j = 0; j < nextLine.length; j++) {
                            // Create Map of column names and its index
                            colIdxMap.put(nextLine[j].toLowerCase().trim(), j);
                        }
                    } else {
                        try {
                            T object = clazz.getDeclaredConstructor().newInstance();
                            Field[] fields = object.getClass().getDeclaredFields();
                            Arrays.asList(fields).stream().filter(field -> field.getAnnotation(IgnoreProperty.class) == null).forEach(field -> {
                                field.setAccessible(true);
                                CsvDeserialize csvPropertyDeserialize = field.getAnnotation(CsvDeserialize.class);
                                CsvProperty csvProperty = field.getAnnotation(CsvProperty.class);
                                String columnTitle = csvProperty != null ? csvProperty.value().toLowerCase() : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(field.getName())).toLowerCase();
                                Integer colIndex = colIdxMap.get(columnTitle);
                                if (colIndex != null) {
                                    String columnValue = nextLine[colIndex].trim();
                                    try {
                                        if (StringUtils.isNotEmpty(columnValue)) {
                                            if (csvPropertyDeserialize != null) {
                                                field.set(object, csvPropertyDeserialize.using().newInstance().deserialize(columnValue));
                                            } else if (field.getType().isAssignableFrom(Integer.class) ||
                                                field.getType().isAssignableFrom(int.class)) {
                                                field.set(object, Integer.parseInt(columnValue));
                                            } else if (field.getType().isAssignableFrom(String.class)) {
                                                field.set(object, columnValue);
                                            } else if (field.getType().isAssignableFrom(Boolean.class) ||
                                                field.getType().isAssignableFrom(boolean.class)) {
                                                field.set(object, Boolean.valueOf(columnValue));
                                            } else if (field.getType().isAssignableFrom(Long.class) ||
                                                field.getType().isAssignableFrom(long.class)) {
                                                field.set(object, Long.valueOf(columnValue));
                                            } else if (field.getType().isAssignableFrom(Double.class) ||
                                                field.getType().isAssignableFrom(double.class)) {
                                                field.set(object, Double.valueOf(columnValue));
                                            } else if (field.getType().isAssignableFrom(LocalDate.class)) {
                                                field.set(object, LocalDate.parse(columnValue, DateTimeFormattingUtils.dateFormatters));
                                            } else if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                                                field.set(object, LocalDateTime.parse(columnValue, DateTimeFormattingUtils.dateTimeFormatters));
                                            } else if (field.getType().isAssignableFrom(List.class)) {
                                                List<String> decoupledElementsList;
                                                List<String> parsedElements = new ArrayList<>();
                                                decoupledElementsList = Arrays.asList(columnValue.split(this.listSeparator));
                                                for (String entry : decoupledElementsList) {
                                                    if (!entry.contains(this.listSeparator)) {
                                                        parsedElements.add(entry);
                                                    }
                                                }
                                                field.set(object, parsedElements);
                                            }
                                        }
                                    } catch (Exception e) {
                                        throw new RuntimeException(e.getMessage() + "for field: '" + columnTitle + "'", e);
                                    }
                                }
                            });
                            listOfMappedObjects.add(object);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            });
            return listOfMappedObjects;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public List<T> getListOfObjectsFromCsv() {
        return listOfObjectsFromCsv;
    }
}
