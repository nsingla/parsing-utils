package io.nsingla.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import io.nsingla.csv.GenericCsvParser;
import io.nsingla.datamodels.User;
import io.nsingla.excel.ExcelParser;
import io.nsingla.excel.GenericExcelParser;
import io.nsingla.json.DefaultObjectMapper;
import io.nsingla.junit5.TestBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ExampleTests extends TestBase {

    @Test
    public void testCsvParser() throws Exception {
        String filePath = this.getClass().getClassLoader().getResource("Users.csv").getFile();
        GenericCsvParser<User> csvParser = new GenericCsvParser(User.class, 0, filePath);
        List<User> userList = csvParser.getListOfObjectsFromCsv();
        System.out.println(userList);
    }

    @Test
    public void testExcelParser() throws Exception {
        String filePath = this.getClass().getClassLoader().getResource("Users.xlsx").getFile();
        GenericExcelParser<User> excelParser = new GenericExcelParser(User.class, ExcelParser.ExcelType.XLSX, filePath, 0);
        List<User> userList = excelParser.getListOfObjectsFromExcel();
        System.out.println(userList);
    }


    @Test
    public void testJsonParser() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("Users.json").getFile());
        List<User> userList = DefaultObjectMapper.getInstance().readValue(file, new TypeReference<List<User>>() {});
        System.out.println(userList);
    }
}
