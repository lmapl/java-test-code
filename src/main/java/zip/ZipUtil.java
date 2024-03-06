package zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
  	//压缩
    public void compress(File file, File zipFile) {
        byte[] buffer = new byte[1024];
        try {
            InputStream input  = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                zipOut.write(buffer, 0, length);
            }
            input.close();
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  //解压缩
    public void uncompress(File file, File outFile) {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream input  = new ZipInputStream(new FileInputStream(file));
            OutputStream output = new FileOutputStream(outFile);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdir();
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            input.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}