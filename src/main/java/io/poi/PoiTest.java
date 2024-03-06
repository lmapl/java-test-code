package io.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class PoiTest {
  public static void main(String[] args) throws IOException, InvalidFormatException {


   // String fileName = "/Users/mapeiliang/Downloads/111--多多岛/五子棋技巧图解__免费版的副本.doc";
    String fileName = "/Users/mapeiliang/Downloads/111--多多岛/五子棋技巧图解__免费版.doc";
    FileInputStream fis = new FileInputStream(fileName);
    HWPFDocument doc = new HWPFDocument(fis);
    System.out.println(doc.getText());

  }
}
