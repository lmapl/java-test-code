package guava.base.classs;

import com.google.common.base.Enums;

public class EnumsTest {
  /**
   * 枚举工具
   */
  public static void main(String[] args){
    System.out.println(Enums.getField(EnumEx.E1));
    System.out.println(Enums.getIfPresent(EnumEx.class,EnumEx.E1.name()));
  }
}
