package io;

import org.docx4j.*;
import org.docx4j.finders.CommentFinder;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.wml.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.jvnet.jaxb2_commons.ppp.Child;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

public class ConvertWordToPDFWithBookmarks {
    public static void main(String[] args) {
        try {
            String path = "/Users/mapeiliang/Downloads/图谱＆双链.docx";
            //String path = "/Users/mapeiliang/Downloads/联系信息.docx";
            String result = "/Users/mapeiliang/Downloads/result12.docx";
            // 加载Word文档
            WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(path));
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

            String xpath2 = "//w:t";
            List<Object> list3 = documentPart.getJAXBNodesViaXPath(xpath2, false);
            P p2 = null;
            R r2 = null;
            for(Object o1 : list3){
                Text text = (Text) o1;
                if(text.getValue().equals("修改：笔记a关联笔记b则笔记b也关联笔记a")){
                   r2 = (R)text.getParent();
                   p2 = (P) r2.getParent();
                }
            }

            String bookmarkName1 = "bookmarkC";
            bookmarkRun(p2, r2, bookmarkName1, 3);


            wordMLPackage.save(new java.io.File(result));

            // 进行转换
           /* Conversion conversion = new Conversion(wordMLPackage);
            OutputStream pdfOutputStream = new java.io.FileOutputStream(result);
            PdfConversion converter = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wordMLPackage);
            PdfSettings pdfSettings = new PdfSettings();
            converter.output(pdfOutputStream, pdfSettings);
            //annotations*/

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

    public static void bookmarkRun(P p, R r, String name, int id) {

        // Find the index
        int index = p.getContent().indexOf(r);

        if (index<0) {
            System.out.println("P does not contain R!");
            return;
        }

        ObjectFactory factory = Context.getWmlObjectFactory();
        BigInteger ID = BigInteger.valueOf(id);


        // Add bookmark end first
        CTMarkupRange mr = factory.createCTMarkupRange();
        mr.setId(ID);
        JAXBElement<CTMarkupRange> bmEnd = factory.createBodyBookmarkEnd(mr);
        p.getContent().add(index+1, bmEnd);

        // Next, bookmark start
        CTBookmark bm = factory.createCTBookmark();
        bm.setId(ID);
        bm.setName(name);
        JAXBElement<CTBookmark> bmStart =  factory.createBodyBookmarkStart(bm);
        p.getContent().add(index, bmStart);

    }

 }