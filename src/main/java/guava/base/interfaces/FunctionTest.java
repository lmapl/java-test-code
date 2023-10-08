package guava.base.interfaces;

import java.util.function.Function;

public class FunctionTest {
  /**
   * Function<F,T>
   * 处理参数 <F>并返回结果 <T>
   * 常用方式：组合式函数编程； 匿名内部类
   */

  public static void main(String[] args){
    Function<String,Integer> function = new Function<String,Integer>(){
      @Override public Integer apply(String o) {
        o = o.trim();
        return o.length();
      }
    };
    Function<String,Integer> function2 = String::length;

    String ae = "dkkenngg";
    System.out.println(function.apply(ae));
    System.out.println(function2.apply(ae));
  }
}
