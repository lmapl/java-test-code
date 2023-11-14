package pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * pdf按页改为jpg
 */
public class Pdf2Jpg {
    public static void main(String[] args) throws Exception {
        pdfToImageFile();
    }

    static void pdfToImageFile() throws Exception {
        PDDocument doc = null;
        ByteArrayOutputStream os = null;
        InputStream stream = null;
        OutputStream out = null;
        try {
            // pdf路径
            stream = new FileInputStream("/Users/mapeiliang/Downloads/OKR完成度填写指南.pdf");
            // 加载解析PDF文件
            doc = PDDocument.load(stream);
            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            PDPageTree pages = doc.getPages();
            int pageCount = pages.getCount();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 200);
                os = new ByteArrayOutputStream();
                ImageIO.write(bim, "jpg", os);
                byte[] dataList = os.toByteArray();
                // jpg文件转出路径
                File file = new File("/Users/mapeiliang/Downloads/pdf/hello_" + i + ".jpg");
                if (!file.getParentFile().exists()) {
                    // 不存在则创建父目录及子文件
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                out.write(dataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (doc != null) { doc.close(); }
            if (os != null) { os.close(); }
            if (stream != null) { stream.close(); }
            if (out != null) { out.close(); }
        }
    }
}
