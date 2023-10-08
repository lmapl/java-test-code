package demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// 包依赖找不到啦，暂时注释掉

public class XssToInsertSql {
  public static void main(String[] args) {

    String path = "/Users/mapeiliang/Downloads/merged/OfficePLUS-Monthly-8.xlsx";

    List<String> list = new ArrayList<>();
    try {
      FileInputStream inputStream = new FileInputStream(new File(path));
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet xssfSheet = workbook.getSheetAt(0);
      int rowNumber = xssfSheet.getLastRowNum();
      for (int i = 0; i <= rowNumber; i++) {
        Row xssfRow = xssfSheet.getRow(i);
        Cell cell = xssfRow.getCell(0);
        list.add(cell.getStringCellValue());
      }
      workbook.close();
      inputStream.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

    list.remove(0);
    //拼接sql字符串
    int i=0;
    StringBuffer buffer = new StringBuffer();
    for (String item : list) {
      i++;
      buffer.append("('").append(item).append("'),");
      if(i == 100){
        String a = buffer.toString();
        a = a.substring(0, a.length() - 1);
        System.out.println("INSERT INTO office_codes(office_code) VALUES "+a + ";");
        buffer = new StringBuffer();
        i=0;
      }

    }


  }
}
