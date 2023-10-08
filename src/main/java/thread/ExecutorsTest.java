package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsTest {
  public static void main(String[] args){
    ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.submit(new Runnable() {
      @Override public void run() {
        System.out.print("aaa");
      }
    });
  }
}
