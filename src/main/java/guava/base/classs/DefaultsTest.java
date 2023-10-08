package guava.base.classs;

import com.google.common.base.Defaults;

public class DefaultsTest {
  /**
   * 基础数据类型的默认值，非封装类
   * @param args
   */
  public static void main(String[] args){
   System.out.println(Defaults.defaultValue(int.class));
    System.out.println(Defaults.defaultValue(Integer.class));
  }
}
