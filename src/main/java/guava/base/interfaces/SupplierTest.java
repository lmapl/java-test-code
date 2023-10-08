package guava.base.interfaces;

import java.util.function.Supplier;

public class SupplierTest {

  /**
   * Supplier<T>
   *   返回<T>类型对象
   */



  public static void main(String[] args){
    //获取新对象
    Supplier<Person> personSupplier = Person::new;
    System.out.println(personSupplier.get().getName());

    //根据已有对象复制
    Person person = new Person();
    person.setName("second");
    Supplier<Person> personSupplier2 = ()-> person;
    System.out.println(personSupplier2.get().getName());




  }
}
