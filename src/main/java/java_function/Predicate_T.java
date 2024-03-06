package java_function;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Predicate：提供了最基本的一些逻辑判断，
 * 例如：与，或，非等。
 * 在jdk1.8提供的很多处理集合的方法中就使用了这个接口，例如：filter
 */
public class Predicate_T {
  public static void main(String[] args) {

    boolean conform = conformLength(s -> s.length() > 5, "helloWord");
    System.out.println("字符串长度是否符合："+conform);

    boolean nonConform = nonConform(s -> s.length() > 5, "helloWord");
    System.out.println("字符串长度是否符合(非)："+nonConform);

    boolean contain = moreContain("helloWord", s -> s.contains("h"),
        s -> s.contains("W"),
        s -> s.contains("Word"));
    System.out.println("字符串是否包含多条件："+contain);

    boolean single = singleContain("helloWord", s -> s.contains("hello"),
        s -> s.contains("b"),
        s -> s.contains("a"));
    System.out.println("字符串是否包含单条件："+single);
  }

  /**
   * 是否匹配长度
   */
  public static boolean conformLength(Predicate<String> predicate,String word){
    return predicate.test(word);
  }

  /**
   * 非
   */
  public static boolean nonConform(Predicate<String> predicate,String word){
    return predicate.negate().test(word);
  }

  /**
   * 多条件匹配
   */
  @SafeVarargs
  public static boolean moreContain(String data, Predicate<String>... predicates){
    Predicate<String> more = null;
    AtomicInteger ai = new AtomicInteger(0);
    for (Predicate<String> predicate : predicates) {
      int val = ai.intValue();
      more = val == 0 ? predicate : more.and(predicate);
      ai.incrementAndGet();
    }
    assert more != null;
    return more.test(data);
  }
  /**
   * 多条件匹配
   */
  @SafeVarargs
  public static boolean singleContain(String data, Predicate<String>... predicates) {
    Predicate<String> single = null;
    AtomicInteger ai = new AtomicInteger(0);
    for (Predicate<String> predicate : predicates) {
      int val = ai.intValue();
      single = val == 0 ? predicate : single.or(predicate);
      ai.incrementAndGet();
    }
    assert single != null;
    return single.test(data);
  }
}
