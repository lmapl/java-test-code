package java_function;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * function：接收一个参数，并指定返回参数类型。Function<T, R>：T：表示提交参数，R：表示返回参数。
 */
public class Function_T {
  public static void main(String[] args) {


    int len = strApply(String::length, "word");
    int len1 = "word".length();
    System.out.println("字符串长度：" + len);


    Integer compose = moreCompose(5, i -> i + 1, i -> i * 5);
    System.out.println("compose:先计算输入逻辑，再计算当前逻辑：" + compose);

    Integer andThen = moreAndThen(5, i -> i + 1, i -> i * 5);
    System.out.println("andThen:先计算当前逻辑，再计算传入逻辑：" + andThen);
  }

  /**
   * 字段长度
   */
  public static Integer strApply(Function<String, Integer> func, String word) {
    return func.apply(word);
  }

  /**
   * 多表达式计算，使用compose
   * 嵌套执行多个方法
   */
  @SafeVarargs
  public static Integer moreCompose(Integer initVal, Function<Integer, Integer>... func) {
    Function<Integer, Integer> fir = null;
    AtomicInteger ai = new AtomicInteger(0);
    for (Function<Integer, Integer> function : func) {
      int val = ai.intValue();
      // compose 是先执行参数function 再执行本身function；
      fir = val == 0 ? function : fir.compose(function);
      ai.incrementAndGet();
    }
    assert fir != null;
    return fir.apply(initVal);
  }

  /**
   * 多表达式计算，使用andThen
   * 嵌套执行多个方法
   */
  @SafeVarargs
  public static Integer moreAndThen(Integer initVal, Function<Integer, Integer>... func) {
    Function<Integer, Integer> fir = null;
    AtomicInteger ai = new AtomicInteger(0);
    for (Function<Integer, Integer> function : func) {
      int val = ai.intValue();
      //使用andThen 是先执行本身 function； 再执行参数function
      fir = val == 0 ? function : fir.andThen(function);
      ai.incrementAndGet();
    }
    assert fir != null;
    return fir.apply(initVal);
  }
}
