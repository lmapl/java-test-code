package guava.base.interfaces;

public class Person{
    private String name = "default";
    public Person() {
      System.out.println("no param");
    }

    public Person(String name){
      System.out.println("has param");
      this.name = name;
    }
    String getName() {
      return name;
    }

    void setName(String name) {
      this.name = name;
      System.out.println("set name");
    }
  }