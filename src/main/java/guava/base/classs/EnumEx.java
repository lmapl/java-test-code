package guava.base.classs;

public enum EnumEx {
  E1("E1",1),
  E2("E2",2) ;
  private String name;
  private int code;
  private EnumEx(String name,int code){
    this.code = code;
    this.name = name;
  }
}
