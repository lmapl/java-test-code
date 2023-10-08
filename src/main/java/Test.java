import java.io.IOException;
import java.util.TreeMap;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

public class Test {
  public static void main(String[] args) throws Exception {
    Resource publicResource = new ClassPathResource("ip2region.xdb");
    byte[] cBuff = FileCopyUtils.copyToByteArray(publicResource.getInputStream());
    Searcher searcher = Searcher.newWithBuffer(cBuff);
    String region = searcher.search("23.99.118.16");
    System.out.print(region);
  }

}
