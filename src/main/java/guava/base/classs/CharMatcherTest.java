package guava.base.classs;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;

public class CharMatcherTest {

  /**
   * 字符串、字符匹配工具
   */

  public static void main(String[] args){
    CharMatcher charMatcher =  CharMatcher.anyOf("dmmenngnKgF");

    System.out.println(charMatcher.matches('d'));
    String a = charMatcher.retainFrom("g");
    System.out.println(a);

    CharMatcher charMatcher2 =  CharMatcher.anyOf("dmmenngnKgM");

    CharMatcher charMatcher3 =  charMatcher.and(charMatcher2);
    System.out.println(charMatcher3.matches('K'));
    System.out.println(charMatcher3.matches('F'));

    System.out.println(charMatcher3.matchesAllOf("dmmenngnKg"));
    System.out.println(charMatcher3.matchesAllOf("dmmenngnKgF"));
    }

}
