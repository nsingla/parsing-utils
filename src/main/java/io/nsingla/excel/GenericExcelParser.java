package io.nsingla.excel;

import io.nsingla.annotations.IgnoreProperty;
import io.nsingla.annotations.excel.ExcelDeserialize;
import io.nsingla.annotations.excel.ExcelProperty;
import io.nsingla.formatters.DateTimeFormattingUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenericExcelParser<T> extends ExcelParser {

    private static final Logger logger = LoggerFactory.getLogger(GenericExcelParser.class);
    private Class<T> clazz;
    private HashMap<String, Integer> colIdxMap = new HashMap<>();
    private int numberOfLinesToSkip = 0;
    private List<T> listOfObjectsFromExcel = new ArrayList<>();

    public GenericExcelParser(Class<T> clazz, ExcelType excelType, String filePath, int numberOfLinesToSkip) throws Exception {
        super(excelType, filePath);
        this.clazz = clazz;
        this.numberOfLinesToSkip = numberOfLinesToSkip;
        parseToJavaObject(excelType);
    }

    public GenericExcelParser(Class<T> clazz, ExcelType excelType, InputStream is, int numberOfLinesToSkip) throws Exception {
        super(excelType, is);
        this.clazz = clazz;
        this.numberOfLinesToSkip = numberOfLinesToSkip;
        parseToJavaObject(excelType);
    }

    public GenericExcelParser(Class<T> clazz, String filePath) throws Exception {
        super(ExcelType.XLSX, filePath);
        this.clazz = clazz;
        parseToJavaObject(ExcelType.XLSX);
    }

    public GenericExcelParser(Class<T> clazz, InputStream is) throws Exception {
        super(ExcelType.XLSX, is);
        this.clazz = clazz;
        parseToJavaObject(ExcelType.XLSX);
    }

    private void parseToJavaObject(ExcelType excelType) throws IllegalAccessException, InstantiationException {
        Iterator<Row> iterator = excelType.equals(ExcelType.XLS) ? xlsSheet.iterator() : xlsxSheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            // Skip parsing lines == numberOfLinesToSkip - 1
            if (numberOfLinesToSkip > 0) {
                if (nextRow.getRowNum() < numberOfLinesToSkip) {
                    continue;
                }
            }
            if (clazz.isAssignableFrom(Map.class)) {
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                List<Object> cellValues = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();
                    cellValues.add(getCellValue(nextCell));
                }
                if (CollectionUtils.isNotEmpty(cellValues)) {
                    Map pair = new HashMap();
                    List<Object> values = new ArrayList<>();
                    for (int i = 1; i < cellValues.size(); i++) {
                        values.add(cellValues.get(i));
                    }
                    if (values.size() == 1) {
                        pair.put(String.valueOf(cellValues.get(0)), values.get(0));
                    } else {
                        pair.put(String.valueOf(cellValues.get(0)), values);
                    }
                    listOfObjectsFromExcel.add((T) pair);
                }
            } else {
                T object = clazz.newInstance();
                Field[] fields = object.getClass().getDeclaredFields();
                if (nextRow.getRowNum() == numberOfLinesToSkip) {
                    Iterator<Cell> cellIterator = nextRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell nextCell = cellIterator.next();
                        colIdxMap.put(nextCell.getStringCellValue().toLowerCase().trim(), nextCell.getColumnIndex());
                    }
                    continue;
                }
                if (isRowEmpty(nextRow)) {
                    continue;
                }
                // Filter out fields annotated with `IgnoreProperty`
                Arrays.asList(fields).stream().filter(field -> field.getAnnotation(IgnoreProperty.class) == null).forEach(field -> {
                    field.setAccessible(true);
                    ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    ExcelDeserialize excelDeserialize = field.getAnnotation(ExcelDeserialize.class);
                    String columnTitle = excelProperty != null ? excelProperty.value().toLowerCase() : String.join(" ", StringUtils.splitByCharacterTypeCamelCase(field.getName())).toLowerCase();
                    logger.trace("Parsing value for field: {}, in row: {} and columTitle: {}", field.getName(), nextRow.getRowNum(), columnTitle);
                    Integer idx = colIdxMap.get(columnTitle);
                    if (idx != null) {
                        Cell cell = getCell(nextRow, idx);
                        try {
                            if (cell != null) {
                                Object cellValue = getCellValue(cell);
                                logger.trace("Cell Type: {}, cell value: {}", cell.getCellType(), cellValue);
                                if (cellValue != null) {
                                    if (excelDeserialize != null) {
                                        field.set(object, excelDeserialize.using().getDeclaredConstructor().newInstance().deserialize(String.valueOf(cellValue)));
                                    } else if (field.getType().isEnum()) {
                                        field.set(object, Arrays.stream(field.getType().getEnumConstants()).filter(value -> value.toString().toLowerCase().equals(String.valueOf(cellValue).toLowerCase())).findFirst().orElse(null));
                                    } else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class)) {
                                        field.set(object, Boolean.valueOf(String.valueOf(cellValue)));
                                    } else if (field.getType().isAssignableFrom(Long.class)) {
                                        field.set(object, ((Double) cellValue).longValue());
                                    } else if (field.getType().isAssignableFrom(List.class)) {
                                        List<String> listValue;
                                        String stringCellValue = String.valueOf(cellValue);
                                        if (stringCellValue.contains(",")) {
                                            listValue = Arrays.asList(stringCellValue.split(",\\s?"));
                                        } else {
                                            listValue = new ArrayList<>();
                                            listValue.add(stringCellValue);
                                        }
                                        field.set(object, listValue);
                                    } else if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
                                        field.set(object, ((Double) cellValue).intValue());
                                    } else if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                                        field.set(object, LocalDateTime.parse(String.valueOf(cellValue), DateTimeFormattingUtils.dateTimeFormatters));
                                    } else if (field.getType().isAssignableFrom(LocalDate.class) || field.getType().isAssignableFrom(Date.class)) {
                                        field.set(object, LocalDate.parse(String.valueOf(cellValue), DateTimeFormattingUtils.dateFormatters));
                                    } else {
                                        field.set(object, field.getType().cast(cellValue));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                });
                listOfObjectsFromExcel.add(object);
            }
        }
    }

    public List<T> getListOfObjectsFromExcel() {
        return listOfObjectsFromExcel;
    }
}
