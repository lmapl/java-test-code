package thread;

import java.util.concurrent.Callable;

public class ThreadTest {
  public static void main(String[] args) throws Exception {
    ThreadImpCallable threadImpCallable = new ThreadImpCallable();
    threadImpCallable.call();
  }
}

class ThreadImpCallable implements Callable{

  @Override public Object call() throws Exception {
    String ret = "Hello " + Thread.currentThread().getName() + " impl java.lang.Callable";
    System.out.println(ret);
    return ret;
  }
}

class ThreadImpRunnable implements Runnable{
  @Override
  public void run() {
    System.out.println("Hello " + Thread.currentThread().getName() + " impl java.lang.Runnable");
  }
}
class MyThread extends Thread {
  @Override
  public void run() {
    System.out.println("Hello " + this.getName() + " extend java.lang.Thread");
  }
}
