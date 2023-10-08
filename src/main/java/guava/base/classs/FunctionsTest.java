package guava.base.classs;


import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class FunctionsTest {
  //接口Function的常用方式集合
  public static void main(String[] args){

    Map<String,Integer> map = new HashMap<>();
    map.put("a",1);
    map.put("b",2);
    Function<String,Integer> f = Functions.forMap(map);
    System.out.println(f.apply("a"));


  }
}
