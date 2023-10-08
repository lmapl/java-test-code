package guava.base.interfaces;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

public class JoinerTest {

  /**
   * 分隔符拼接字符
   * @param args
   */
  public static void main(String[] args){
    Joiner joiner = Joiner.on(",");
    System.out.println(joiner.join("a","b","c"));

    Joiner.MapJoiner mapJoiner =  joiner.withKeyValueSeparator("|");
    Map<String,Integer> map =new HashMap<>();
    map.put("a",1);
    map.put("b",2);
    System.out.println( mapJoiner.join(map));
  }
}
