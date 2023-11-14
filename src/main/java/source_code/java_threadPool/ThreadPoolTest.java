package source_code.java_threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class ThreadPoolTest {
  /**
   * java 原生线程池
   * 基于Executor接口中将任务提交和任务执行解耦的设计，
   * ExecutorService和其各种功能强大的实现类提供了非常简便方式来提交任务、执行任务、获取任务执行结果的全部过程。
   * ThreadPoolExecutor#execute(java.lang.Runnable) 提交任务
   * ThreadPoolExecutor#runWorker(source_code.java_threadPool.ThreadPoolExecutor.Worker) 执行任务
   * Future 获取结果
   *
   */
  public static void main(String[] args){
    StampedLock lock;
    System.out.println(-1 << 29);
    System.out.println(0 << 29);
    System.out.println(1 << 29);
    System.out.println(2 << 29);
    System.out.println(3 << 29);
    System.out.println((1 << 29) - 1);
    System.out.println(~29);
    System.out.println(Integer.toBinaryString(-30));
    System.out.println(Integer.toBinaryString(~29));
    java.util.concurrent.ThreadPoolExecutor  executor = new java.util.concurrent.ThreadPoolExecutor(1, 2, 60L,
        TimeUnit
        .SECONDS, new
        ArrayBlockingQueue<>(2), Executors
        .defaultThreadFactory());
    for(int i=0;i<10;i++){

      executor.execute(() ->{
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("appeng"+System.currentTimeMillis());
      });
    }

    System.out.println("11111111111111111111111111100010".length());
  }

}
