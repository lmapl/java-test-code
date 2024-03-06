package java_function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Consumer：属于消费型接口，需要传入参数，执行lambda表达式，没有返回值，我们常使用的forEach中就使用了Consumer。
 */
public class Consumer_T {

  public static void main(String[] args) {
    //使用Consumer打印姓张的人，由于Consumer没有返回值，只能打印结果
    Consumer<String> consumer = (name) -> {
      if (name.startsWith("张")){
        System.out.println("张姓人员：" + name);
      }
    };

    Consumer<String> secConsumer = (name) -> {
      if (Objects.equals("张翠山",name)){
        System.out.println("找到了：" + name);
      }
    };
    Objects.requireNonNull(consumer);

    String[] data = new String[]{"张三","里斯","张翠山"};
    for (String datum : data) {
      moreAndThen(datum,consumer,secConsumer);
    }
  }

  @SafeVarargs
  private static void moreAndThen(String data, Consumer<String>... consumers){
    Consumer<String> exeConsumer = null;
    for (Consumer<String> consumer : consumers) {
      if (Objects.isNull(exeConsumer)){
        exeConsumer = consumer;
        continue;
      }
      exeConsumer = exeConsumer.andThen(consumer);
    }
    assert exeConsumer != null;
    exeConsumer.accept(data);
  }

}
