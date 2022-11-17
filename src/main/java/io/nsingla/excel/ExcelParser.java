package io.nsingla.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelParser {

    protected Sheet xlsSheet;
    protected XSSFSheet xlsxSheet;

    public ExcelParser(ExcelType excelType, String filePath) throws IOException {
        this(excelType, new FileInputStream(new File(filePath)));
    }

    public ExcelParser(ExcelType excelType, InputStream is) throws IOException {
        try {
            switch (excelType) {
                case XLS:
                    Workbook workbook = new HSSFWorkbook(is);
                    xlsSheet = workbook.getSheetAt(0);
                    workbook.close();
                    break;
                case XLSX:
                    XSSFWorkbook xlsxWorkbook = new XSSFWorkbook(is);
                    xlsxSheet = xlsxWorkbook.getSheetAt(0);
                    xlsxWorkbook.close();
                    break;
                default:
                    throw new RuntimeException(String.format("'%d' Not a valid excel type format", excelType));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse file because of following exception:\n" + e.getMessage(), e);
        } finally {
            is.close();
        }
    }

    public Cell getCell(Row row, int index) {
        return row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    }

    public boolean isRowEmpty(Row row) {
        String data = "";
        DataFormatter dataFormatter = new DataFormatter();
        for (Cell cell : row) {
            data = data.concat(dataFormatter.formatCellValue(cell));
        }
        return data.trim().isEmpty();
    }

    public Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return StringUtils.isNotEmpty(cell.getStringCellValue()) ? cell.getStringCellValue().trim() : null;
            case BOOLEAN:
                return StringUtils.isNotEmpty(String.valueOf(cell.getBooleanCellValue())) ? cell.getBooleanCellValue() : null;
            case NUMERIC:
                return StringUtils.isNotEmpty(String.valueOf(cell.getNumericCellValue())) ? cell.getNumericCellValue() : null;
            case BLANK:
            case _NONE:
                return null;
            default:
                throw new RuntimeException(String.format("'%s' is an unknown celltype, so no value can be returned", cell.getCellType()));
        }
    }

    public Sheet getXlsSheet() {
        return xlsSheet;
    }

    public XSSFSheet getXlsxSheet() {
        return xlsxSheet;
    }

    public enum ExcelType {
        XLS, XLSX
    }
}
