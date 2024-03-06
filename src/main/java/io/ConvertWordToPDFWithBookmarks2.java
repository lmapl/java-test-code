package io;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.finders.CommentFinder;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.structure.DocumentModel;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.jvnet.jaxb2_commons.ppp.Child;

public class ConvertWordToPDFWithBookmarks2 {
    public static void main(String[] args) {
        try {
            String path = "/Users/mapeiliang/Downloads/图谱＆双链.docx";
            //String path = "/Users/mapeiliang/Downloads/联系信息.docx";
            String result = "/Users/mapeiliang/Downloads/result7.pdf";
            // 加载Word文档
            WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(path));

            DocumentModel documentModel =wordMLPackage.getDocumentModel();
            documentModel.getSections();

            // 获取主体内容部分（MainDocumentPart）
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
            List<Object> list = mainDocumentPart.getContent();
            //mainDocumentPart.get


            //R r = (R)p.getContent().get(0);

            // 进行转换
            Conversion conversion = new Conversion(wordMLPackage);
            OutputStream pdfOutputStream = new java.io.FileOutputStream(result);
            PdfConversion converter = new Conversion(wordMLPackage);
            PdfSettings pdfSettings = new PdfSettings();
            converter.output(pdfOutputStream, pdfSettings);
            //annotations

            System.out.println("Conversion complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean remove(List<Object> theList, Object bm) 	{
        // Can't just remove the object from the parent,
        // since in the parent, it may be wrapped in a JAXBElement
        for (Object ox : theList) {
            if (XmlUtils.unwrap(ox).equals(bm)) {
                return theList.remove(ox);
            }
        }
        return false;
    }


    private static boolean get(JAXBElement jaxbElement, String value) 	{
        /*Tbl
        jaxbElement.getValue()*/
        return false;
    }

 }