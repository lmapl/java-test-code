package guava.base.interfaces;

import com.google.common.base.Predicate;

public class PredicateTest {
  /**
   * Predicate<T>
   *   处理参数<T>, 并返回一个布尔值结果
   */

  public static void main(String[] args){
    Person person1 = new Person();
    Person person2 = new Person("defaul");
    Predicate<Person> predicate = o -> (o.getName()+"t").equals("default");

    System.out.println(predicate.apply(person1));
    System.out.println(predicate.apply(person2));
  }
}
