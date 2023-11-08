
package source_code.java_threadPool;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

import source_code.java_locks.AbstractQueuedSynchronizer;
import source_code.java_locks.ReentrantLock;

/**
 * An 线程池 that executes each submitted task using
 * one of possibly several pooled threads, normally configured
 * using {@link Executors} factory methods.
 *
 * <p>Thread pools address two different problems:
 * they(它们) usually provide improved performance when executing(执行) large numbers of asynchronous(异步) tasks,
 * due to reduced per-task invocation overhead, 高性能执行
 * they provide a means of bounding and managing the resources, 资源控制
 *
 * including threads, consumed when executing a collection of tasks.
 * Each {@code ThreadPoolExecutor} also maintains(保存) some basic
 * statistics, such as(例如) the number of completed tasks(已经完成的任务数).
 *
 * <p>To be useful 穿过(across) 广泛的(a wide range of) 上下文/环境(contexts) ,
 * this class provides many 可调节的(adjustable) parameters and 可扩展性(extensibility) 钩子(hooks).
 * However, 程序设计员(programmers)  被敦促(are urged to) use the more convenient(实用的) factory methods(工厂方法)
 * 例如,Executors#newCachedThreadPool,Executors#newFixedThreadPool, 这些方法
 * preconfigure(预配置) settings for the most common usage
 * scenarios(方案).
 * Otherwise, use the following guide when manually
 * configuring and tuning this class(不然就只能人工配置和调整这些 class 的参数):
 *
 * <dl>
 *
 * <dt>Core and maximum pool sizes</dt>
 *
 * <dd>A {@code ThreadPoolExecutor} will automatically adjust(自动调整) the
 * pool size (see {@link #getPoolSize})
 * according to(根据) the bounds(极限,限制) set by
 * corePoolSize (see {@link #getCorePoolSize}) and
 * maximumPoolSize (see {@link #getMaximumPoolSize}).
 *
 * When a new task is submitted in method {@link #execute(Runnable)},
 * and fewer than corePoolSize threads are running, a new thread is
 * created to handle the request, even if other worker threads are
 * idle.
 * If there are more than corePoolSize but less than
 * maximumPoolSize threads running, a new thread will be created only
 * if the queue is full.
 *
 * By setting corePoolSize and maximumPoolSize
 * the same, you create a fixed-size thread pool.
 *
 * By setting
 * maximumPoolSize to an essentially unbounded value such as {@code
 * Integer.MAX_VALUE}, you allow the pool to accommodate an arbitrary
 * number of concurrent tasks.
 *
 * Most typically(典型的), core and maximum pool
 * sizes are set only upon construction(在构造器上设置),
 * but they may also be changed dynamically using {@link #setCorePoolSize} and {@link
 * #setMaximumPoolSize}. </dd>
 *
 * <dt>On-demand construction</dt>
 *
 * <dd>By default, even core threads are initially created and
 * started only when new tasks arrive, but this can be overridden(推翻/比…更重要)
 * dynamically using method {@link #prestartCoreThread} or {@link
 * #prestartAllCoreThreads}.
 * You probably(可能) want to prestart(预起动) threads if
 * you construct the pool with a non-empty queue. </dd>
 *
 * <dt>Creating new threads</dt>
 *
 * <dd>New threads are created using a {@link ThreadFactory}.
 * If not otherwise specified, a {@link Executors#defaultThreadFactory} is
 * used, that creates threads to all be in the same {@link
 * ThreadGroup} and with the same {@code NORM_PRIORITY} priority and
 * non-daemon status.
 * By supplying a different ThreadFactory, you can
 * alter the thread's name, thread group, priority, daemon(守护) status,etc(诸如此类,等等).
 * If a {@code ThreadFactory} fails to create a thread when asked
 * by returning null from {@code newThread}, the executor will
 * continue, but might not be able to execute any tasks.
 * Threads should possess the "modifyThread" {@code RuntimePermission}.
 * If worker threads or other threads using the pool do not possess this
 * permission, service may be degraded: configuration changes may not
 * take effect in a timely manner, and a shutdown pool may remain in a
 * state in which termination is possible but not completed.</dd>
 *
 * <dt>Keep-alive times</dt>
 *
 * <dd>If the pool currently(当前) has more than corePoolSize threads(比核心线程数多的线程),
 * excess threads(多出的) will be terminated(停止) if they have been idle(空闲) for more
 * than the keepAliveTime (see {@link #getKeepAliveTime(TimeUnit)}).
 * This provides a means of(一种手段) reducing resource consumption(减少资源消耗) when the
 * pool is not being actively used(充分使用).
 * If the pool becomes more active later, new threads will be constructed.
 * This parameter can also be changed dynamically using method {@link #setKeepAliveTime(long,
 * TimeUnit)}.
 * Using a value of {@code Long.MAX_VALUE} {@link TimeUnit#NANOSECONDS} effectively
 * disables idle threads from ever terminating prior to shut down.
 *
 * By default, the keep-alive policy applies only when there are more than corePoolSize threads.
 * (默认该机制只作用在多出核心线程数的线程上，但是也有方法可以让它作用在核心线程上)
 * But method {@link #allowCoreThreadTimeOut(boolean)} can be used to
 * apply this time-out policy to core threads as well(也),
 * so long as(只要) the keepAliveTime value is non-zero. </dd>
 *
 * <dt>Queuing</dt>
 *
 * <dd>Any {@link BlockingQueue} may be used to transfer and hold
 * submitted tasks.  The use of this queue interacts with pool sizing:
 *
 * <ul>
 *
 * <li> If fewer than corePoolSize threads are running, the Executor
 * always prefers adding a new thread
 * rather than queuing.
 *  线程数量少于核心线程数时，新任务会创建新线程而不会进入队列存储
 * </li>
 *
 * <li> If corePoolSize or more threads are running, the Executor
 * always prefers queuing a request rather than adding a new
 * thread.
 * 线程数量超出核心线程时，线程池会将新任务存入队列
 * </li>
 *
 * <li> If a request cannot be queued, a new thread is created unless
 * this would exceed maximumPoolSize, in which case, the task will be
 * rejected.
 * 队列存满时，在线程总数量没有超过  maximumPoolSize 在会创建新的线程处理；否则会拒绝这个新的任务
 * </li>
 *
 * </ul>
 *
 * There are three general strategies(三种常见策略) for queuing:
 * <ol>
 *
 * <li> <em> Direct handoffs(直接传递/移交).</em>
 * A good default choice for a work queue is a {@link SynchronousQueue}(同步的队列)
 * that hands off tasks to threads without otherwise holding them.
 * Here, an attempt to queue a task will fail if no threads are immediately available to run it,
 * so a new thread will be constructed.
 * This policy avoids(避免) lockups(封锁?) when
 * handling sets of requests that might have internal dependencies.
 * Direct handoffs generally require unbounded(无限的) maximumPoolSizes to
 * avoid(避免) rejection of new submitted tasks.
 * This in turn admits the possibility of unbounded thread growth when commands continue to
 * arrive on average faster than they can be processed.
 * 这在线程的产生大于处理速度时，反而又有可能造成 thread 无限量的增加，
 * </li>
 *
 * <li><em> Unbounded queues.(无限制的队列)</em>
 * Using an unbounded queue (例如 {@link LinkedBlockingQueue} 没有预定容量)
 * will cause new tasks to wait in the queue(新任务在队列中等待) when all corePoolSize threads are busy(忙碌的).
 * Thus(这样), no more than corePoolSize threads will ever be created(不会有超过核心线程数量的线程被创建).
 * (And the value of the maximumPoolSize therefore doesn't have any effect.)
 * This may be appropriate(适当的) when each task is completely independent(完全无关) of others,
 * so tasks cannot affect each others execution;
 *
 * for example, in a web page server. While this style of queuing can be useful in 使平滑(smoothing out)
 * transient bursts of requests(短暂爆发的请求), it admits the possibility of
 * unbounded work queue growth when commands continue to arrive on
 * average faster than they can be processed.  </li>
 *
 * <li><em>Bounded queues.(有限制的队列)</em>
 * A bounded queue (for example, an {@link ArrayBlockingQueue}) helps 防止资源耗尽(prevent resource exhaustion)
 * when used with finite(有限的) maximumPoolSizes, but can be more difficult(困难的) to
 * tune and control.
 *
 * Queue sizes and maximum pool sizes may be traded
 * off for each other: Using(使用) large queues and small pools minimizes(降低)
 * CPU usage, OS resources, and context-switching overhead(开销), but can
 * lead to(导致) artificially low throughput(低吞吐量).
 *
 * If tasks frequently block(经常阻塞) (for
 * example if they are I/O bound), a system may be able to schedule
 * time for more threads than you otherwise allow. Use of small queues
 * generally requires larger pool sizes, which keeps CPUs busier but
 * may encounter(遇到) unacceptable scheduling overhead, which also
 * decreases throughput.  </li>
 *
 * </ol>
 *
 * </dd>
 *
 * <dt>Rejected tasks(拒绝任务)</dt>
 *
 * <dd>New tasks submitted in method {@link #execute(Runnable)} will be
 * <em>rejected</em> when the Executor has been shut down, and also when
 * the Executor uses finite bounds for both maximum threads and work queue
 * capacity, and is saturated(已经饱和的).
 * In either case, the {@code execute} method invokes the {@link
 * RejectedExecutionHandler#rejectedExecution(Runnable, ThreadPoolExecutor)}
 * method of its {@link RejectedExecutionHandler}.
 * Four predefined handler policies are provided:
 *
 * <ol>
 *
 * <li> In the default {@link ThreadPoolExecutor.AbortPolicy}, the
 * handler throws a runtime {@link RejectedExecutionException} upon
 * rejection. </li>
 *
 * <li> In {@link ThreadPoolExecutor.CallerRunsPolicy}, the thread
 * that invokes {@code execute} itself runs the task. This provides a
 * simple feedback control mechanism that will slow down the rate that
 * new tasks are submitted. </li>
 *
 * <li> In {@link ThreadPoolExecutor.DiscardPolicy}, a task that
 * cannot be executed is simply dropped.  </li>
 *
 * <li>In {@link ThreadPoolExecutor.DiscardOldestPolicy}, if the
 * executor is not shut down, the task at the head of the work queue
 * is dropped, and then execution is retried (which can fail again,
 * causing this to be repeated.) </li>
 *
 * </ol>
 *
 * It is possible to define and use other kinds of {@link
 * RejectedExecutionHandler} classes. Doing so requires some care
 * especially when policies are designed to work only under particular
 * capacity or queuing policies. </dd>
 *
 * <dt>Hook methods(钩子方法)</dt>
 *
 * <dd>This class provides {@code protected} overridable
 * {@link #beforeExecute(Thread, Runnable)} and
 * {@link #afterExecute(Runnable, Throwable)} methods that are called
 * before and after execution of each task.
 * These can be used to manipulate the execution environment(操纵执行环境);
 * for example, reinitializing ThreadLocals, gathering statistics, or adding log entries.
 * Additionally, method {@link #terminated} can be overridden to perform
 * any special processing that needs to be done once the Executor has
 * fully terminated.
 *
 * <p>If hook or callback methods throw exceptions, internal worker
 * threads may in turn fail and abruptly terminate.</dd>
 *
 * <dt>Queue maintenance(队列维护)</dt>
 *
 * <dd>Method {@link #getQueue()} allows access to the work queue
 * for purposes(目的/用途) of monitoring and debugging.
 * Use of this method for any other purpose is strongly discouraged(强烈劝阻).
 * Two supplied methods, {@link #remove(Runnable)} and {@link #purge} are available to
 * assist in(协助) storage reclamation(存储回收) when large numbers of queued tasks
 * become cancelled.</dd>
 *
 * <dt>Finalization(结束)</dt>
 *
 * <dd>A pool that is no longer referenced in a program <em>AND</em>
 * has no remaining threads will be {@code shutdown} automatically.
 * If you would like to ensure that unreferenced pools are reclaimed even
 * if users forget to call {@link #shutdown}, then you must arrange
 * that unused threads eventually die, by setting appropriate
 * keep-alive times, using a lower bound of zero core threads and/or
 * setting {@link #allowCoreThreadTimeOut(boolean)}.  </dd>
 *
 * </dl>
 *
 * <p><b>Extension example(扩展内容)</b>.
 * Most extensions of this class override one or more of the protected hook methods.
 * For example,here is a subclass that adds a simple pause/resume feature:
 *
 *  <pre> {@code
 * class PausableThreadPoolExecutor extends ThreadPoolExecutor {
 *   private boolean isPaused;
 *   private ReentrantLock pauseLock = new ReentrantLock();
 *   private Condition unpaused = pauseLock.newCondition();
 *
 *   public PausableThreadPoolExecutor(...) { super(...); }
 *
 *   protected void beforeExecute(Thread t, Runnable r) {
 *     super.beforeExecute(t, r);
 *     pauseLock.lock();
 *     try {
 *       while (isPaused) unpaused.await();
 *     } catch (InterruptedException ie) {
 *       t.interrupt();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void pause() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = true;
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void resume() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = false;
 *       unpaused.signalAll();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ThreadPoolExecutor extends AbstractExecutorService {

    /**
     * The main pool control state, ctl, is an atomic integer packing
     * two conceptual(概念的) fields
     *   workerCount, indicating the effective number of threads
     *   runState,    indicating whether running, shutting down etc
     *
     * In order to pack them into one int, we limit workerCount to
     * (2^29)-1 (about 500 million) threads rather than (2^31)-1 (2
     * billion) otherwise representable.
     * If this is ever an issue in the future,
     * the variable can be changed to be an AtomicLong,
     * and the shift/mask constants below adjusted.
     * But until the need arises, this code is a bit faster and simpler using an int.
     * 为了将 workerCount 和 runState 保存到同一个 int 整数，
     * 限制 workerCount 的取值范围是 (2^29)-1 (差不多 500 million)，而不是(2^31)-1 (差不多 2 billion)
     * 未来如果需要扩展范围时，可以将 ctl 变量的类型改为 AtomicLong
     *
     * The workerCount is the number of workers that have been
     * permitted to(允许) start and not permitted to stop.
     * The value may be transiently(短暂的) different from the actual(实际的) number of live threads,
     * for example when a ThreadFactory fails to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating.
     * The user-visible(用户可见的) pool size is reported as the current size of the workers set.
     * workerCount 是有效 worker 线程的数量，有效是指已经 start，且没有 stop。
     * 这个数字有时会短暂的和实际的真实数量不一致，
     * 例如 线程工厂（ThreadFactory）没有成功的创建一个被要求的线程，或者被空闲的线程在被终止前也被计算在内
     *
     * The runState provides the main lifecycle control, taking on values:
     *   runState 是线程池的生存周期的主要控制变量
     *
     *   RUNNING:  Accept new tasks and process queued tasks
     *   运行中： 可以接受新任务并处理缓冲队列中的任务
     *
     *   SHUTDOWN: Don't accept new tasks, but process queued tasks
     *   被关闭： 不接受新任务，但会处理缓冲队列中的任务
     *
     *   STOP:  Don't accept new tasks, don't process queued tasks,and interrupt in-progress tasks
     *   已停止： 不接受新任务，也不会处理缓冲队列中的任务， 并且会终止正在被处理的任务
     *
     *   TIDYING:  All tasks have terminated, workerCount is zero,
     *             the thread transitioning to state TIDYING
     *             will run the terminated() hook method
     *   整理阶段： 所有任务都被终止、缓冲队列被清空后的状态，此时线程池将请求terminated()，尝试完全停止
     *
     *   TERMINATED: terminated() has completed
     *   终止状态： 线程池完全被终止
     *
     * The numerical order among these values matters, to allow
     * ordered comparisons.
     * The runState monotonically(单调地) increases over
     * time(随着时间的推移), but need not hit(命中/经过) each state. The transitions are:
     *
     * RUNNING -> SHUTDOWN
     *    On invocation of shutdown(), perhaps implicitly in finalize()
     * (RUNNING or SHUTDOWN) -> STOP
     *    On invocation of shutdownNow()
     * SHUTDOWN -> TIDYING
     *    When both queue and pool are empty
     * STOP -> TIDYING
     *    When pool is empty
     * TIDYING -> TERMINATED
     *    When the terminated() hook method has completed
     *
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     *
     * Detecting(研究发现) the transition from SHUTDOWN to TIDYING is less
     * straightforward(不如..简单直接) than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a recheck -- see
     * below).
     */

    // 变量 ctl 同时保存线程池的有效线程数量 workerCount 和 线程池的运行状态 runState
    // 高3位保存线程池的运行状态，低29位保存有效的线程数量
    // 当 线程数量变化时，就在 ctl 的低位 +1 或者 -1 ；
    // 当 线程池的运行状态变化时，就在 ctl 的高位位操作 ，
    // 用 state 枚举范围直接覆盖整个 ctl ?
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    /**
     * 32-3=29, 用以划定 ctl 中 workerCount 和 runState 的分割位置
     * 在 ctl 中线程数量占用的二进制位数
     */
    private static final int COUNT_BITS = Integer.SIZE - 3;

    /**
     * workerCount的取值范围 （1 ~ 536870911）
     */
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    // runState 总共占 32 中的高三位, 不在 workerCount 的数据范围内

    //-536870912
    private static final int RUNNING    = -1 << COUNT_BITS;
    //0
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    //536870912
    private static final int STOP       =  1 << COUNT_BITS;
    //1073741824
    private static final int TIDYING    =  2 << COUNT_BITS;
    //1610612736
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    // ～29 是取反码 = -30 （二进制是: 11111111111111111111111111100010）
    // 这个数学运算是怎么就能表示 runState ？
    private static int runStateOf(int c)     { return c & ~CAPACITY; }

    // 这个数学运算是怎么就能表示 workerCount ？
    private static int workerCountOf(int c)  { return c & CAPACITY; }

    private static int ctlOf(int rs, int wc) { return rs | wc; }

    /*
     * Bit field accessors that don't require unpacking ctl.
     * These depend on the bit layout and on workerCount being never negative.
     */

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * Attempts to CAS-increment the workerCount field of ctl.
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        //cas 操作在 ctl 的低位加 1
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * Attempts to CAS-decrement the workerCount field of ctl.
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        //cas 操作在 ctl 的低位减 1
        return ctl.compareAndSet(expect, expect - 1);
    }

    /**
     * Decrements the workerCount field of ctl. This is called only on
     * abrupt termination of a thread (see processWorkerExit). Other
     * decrements are performed within getTask.
     * 递减 workerCount ，这个方法只会在一个线程被意外的终止时才会被调用
     */
    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    /**
     * The queue used for holding tasks and handing off to worker threads.
     * We do not require that workQueue.poll() returning null necessarily means that workQueue.isEmpty(),
     * so rely solely(所以完全依赖) on isEmpty(方法) to see(去判断) if the queue is empty(队列是否是空的)
     * (which we must do for example when deciding whether to transition from SHUTDOWN to TIDYING).
     * This accommodates(兼容) special-purpose queues such as(例如) DelayQueues for which poll() is allowed to
     * return null even if it may later return non-null when delays expire.
     *
     * workQueue 用来保存 runnable 任务，并移交（getTask）给 work线程。
     * workQueue.poll() 返回一个 null 结果并不代表 workQueue 是空的。
     * 判断 workQueue 是否为空，完全要依靠 isEmpty 方法的结果, 例如在判断线程池是否要转为 SHUTDOWN 或 TIDYING 时，需要判断 workQueue 是否为空。
     * workQueue 可以是一个有特殊功能的队列，例如延迟队列可以返回 null 结果，也可以在延迟一段时间后返回一个非空结果
     *
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * Lock held on access to workers set and related bookkeeping.
     * While we could use a concurrent set of some sort, it turns out
     * to be generally preferable to use a lock.
     *
     * Among the reasons is that this serializes(连载,连续) interruptIdleWorkers,
     * which avoids(避免) unnecessary interrupt storms(非必要的中断事件), especially during shutdown(尤其是在 shutdown 期间).
     * Otherwise exiting threads(存在的线程) would(可能) concurrently interrupt(同时中断) those
     * that have not yet interrupted(还没有被中断).
     * It also simplifies(使简化) some of(... 中的一些) the associated statistics bookkeeping(相关的统计数据) of largestPoolSize
     * etc(比如最大线程池size等等).
     * We also hold mainLock on shutdown and shutdownNow, for the sake of(为了)
     * ensuring(确保) workers set is stable(稳定的) while separately(单独地) checking
     * permission to interrupt and actually interrupting.
     *
     * mainLock 持有对 workers集合和相关数据的访问权限
     *
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Set containing all worker threads in pool.
     * Accessed only when holding mainLock.
     *  线程池内的 worker 集合, 只有持有 mainLock 锁，才可以访问
     */
    private final HashSet<Worker> workers = new HashSet<>();

    /**
     * Wait condition to support awaitTermination
     * 等候终止的条件
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Tracks largest attained pool size.
     * Accessed only under mainLock.
     * 线程池内出现过的最大线程数量
     */
    private int largestPoolSize;

    /**
     * Counter for completed tasks.
     * Updated only on(只有在……) termination of worker threads(线程终止时修改).
     * Accessed only under mainLock.
     * 已经完成的线程数量
     */
    private long completedTaskCount;


    /*
     * All user control parameters are declared as volatiles so that
     * ongoing actions are based on freshest values, but without need
     * for locking, since no internal invariants depend on them
     * changing synchronously with respect to other actions.
     */

    /**
     * Factory for new threads. 创建新线程的工厂类
     * All threads are created using this factory (via method addWorker).
     * All callers must be prepared(准备处理添加失败) for addWorker to fail, which may reflect a system or user's
     * policy limiting the number of threads.
     *
     * Even though(虽然) it is not treated as(视为) an error,
     * failure to create threads may result in(导致) new tasks being rejected(拒绝) or existing ones remaining stuck(卡住) in
     * the queue.
     *
     * We go further and preserve pool invariants（保存线程池不变量）even in the face of（即使面对）errors
     * such as OutOfMemoryError,
     * that might be thrown while trying to create threads(可以在创建线程时扔出异常).
     * Such errors are rather common(相当普遍) due to(因为) the need to allocate(分配) a native stack(本地堆栈) in Thread.start,
     * and users will want to perform(执行) clean pool shutdown to clean up(清理).
     * There will likely be enough memory available for the cleanup code to
     * complete without encountering yet another OutOfMemoryError.
     *
     * 线程工厂，用于创建 worker 的 thread
     */
    private volatile ThreadFactory threadFactory;

    /**
     * Handler called when saturated(饱和的) or shutdown in execute(执行关机中).
     * 拒绝策略
     */
    private volatile RejectedExecutionHandler handler;

    /**
     * Timeout in nanoseconds for idle(空闲的) threads waiting for work.
     * Threads use this timeout when there are more than(超出) corePoolSize
     * present(现存的线程数量) or if allowCoreThreadTimeOut. Otherwise(不然) they wait
     * forever(永远) for new work.
     * 闲置线程等待任务的空闲时间， 超过这个时间的非核心线程将被终止
     */
    private volatile long keepAliveTime;

    /**
     * If false (default), core threads stay alive even when idle.
     * If true, core threads use keepAliveTime to time out waiting
     * for work.
     *
     * 核心线程释放可以被关闭
     */
    private volatile boolean allowCoreThreadTimeOut;

    /**
     * Core pool size is the minimum number of workers to keep alive
     * (and not allow to time out etc) unless allowCoreThreadTimeOut
     * is set, in which case the minimum is zero.
     *
     * 核心线程数量
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size. Note that the actual maximum is internally
     * bounded by CAPACITY.
     * 最大线程数量
     */
    private volatile int maximumPoolSize;

    /**
     * The default rejected execution handler
     * 默认拒绝策略
     */
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    /**
     * Permission required for callers of shutdown and shutdownNow.
     * We additionally require (see checkShutdownAccess) that callers
     * have permission to actually interrupt threads in the worker set
     * (as governed by Thread.interrupt, which relies on
     * ThreadGroup.checkAccess, which in turn relies on
     * SecurityManager.checkAccess). Shutdowns are attempted only if
     * these checks pass.
     *
     * All actual invocations of Thread.interrupt (see
     * interruptIdleWorkers and interruptWorkers) ignore
     * SecurityExceptions, meaning that the attempted interrupts
     * silently fail. In the case of shutdown, they should not fail
     * unless the SecurityManager has inconsistent policies, sometimes
     * allowing access to a thread and sometimes not. In such cases,
     * failure to actually interrupt threads may disable or delay full
     * termination. Other uses of interruptIdleWorkers are advisory,
     * and failure to actually interrupt will merely delay response to
     * configuration changes so is not handled exceptionally.
     * shutdown 权限控制
     */
    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    /* The context to be used when executing the finalizer, or null. */
    private final AccessControlContext acc;


    /***********------------------内部类 Worker--------------------------------****************/

    /**
     * Class Worker mainly maintains interrupt control state for
     * threads running tasks, along with other minor bookkeeping.
     * This class opportunistically extends AbstractQueuedSynchronizer
     * to simplify acquiring and releasing a lock surrounding each
     * task execution.  This protects against interrupts that are
     * intended to wake up a worker thread waiting for a task from
     * instead interrupting a task being run.  We implement a simple
     * non-reentrant mutual exclusion lock rather than use
     * ReentrantLock because we do not want worker tasks to be able to
     * reacquire the lock when they invoke pool control methods like
     * setCorePoolSize.  Additionally, to suppress interrupts until
     * the thread actually starts running tasks, we initialize lock
     * state to a negative value, and clear it upon start (in
     * runWorker).
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        /**
         * This class will never be serialized, but we provide a
         * serialVersionUID to suppress a javac warning.
         */
        private static final long serialVersionUID = 6138294804551838833L;

        /** Thread this worker is running in.  Null if factory fails. */
        // 运行 worker 的线程
        final Thread thread;
        /** Initial task to run.  Possibly null. */
        // worker 的第一个任务，是业务程序提交到线程池的参数
        Runnable firstTask;
        /** Per-thread task counter */
        // 完成的任务数量
        volatile long completedTasks;

        /**
         * Creates with given first task and thread from ThreadFactory.
         * @param firstTask the first task (null if none)
         */
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /** Delegates main run loop to outer runWorker  */
        // 执行外部方法 runWorker， runWorker 是一个循环遍历
        //在 java.lang.Thread.run() 内被调用
        // #worker.thread = #thread
        // #thread.target = #worker
        @Override
        public void run() {
            runWorker(this);
        }

        // Lock methods
        // The value 0 represents the unlocked state.
        // The value 1 represents the locked state.
        // 加锁结果？
        @Override
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        // 重写 AbstractQueuedSynchronizer#tryAcquire
        // 用于抢锁逻辑
        @Override
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

      // 重写 AbstractQueuedSynchronizer#tryRelease
      // 用于释放锁逻辑
        @Override
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        //抢锁
        public void lock()        { acquire(1); }
        //尝试加锁
        public boolean tryLock()  { return tryAcquire(1); }
        //释放锁
        public void unlock()      { release(1); }
        // 判断释放锁成功
        public boolean isLocked() { return isHeldExclusively(); }

        // 中断已经开始的 worker 线程
        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }

  /***********------------------内部类 Worker  end --------------------------------****************/


    /*
     * Methods for setting control state
     * set 变量 state 的方法
     */
    /**
     * Transitions runState to given target,
     * or leaves it alone if already at least the given target.
     *
     * @param targetState the desired state, either SHUTDOWN or STOP
     *        (but not TIDYING or TERMINATED -- use tryTerminate for that)
     */
    private void advanceRunState(int targetState) {
      //循环体不断尝试
      for (; ; ) {
        int c = ctl.get();
        if (runStateAtLeast(c, targetState)
            || ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c)))) {
          break;
        }
      }
    }

    /**
     * Transitions to TERMINATED state if either (SHUTDOWN and pool
     * and queue empty) or (STOP and pool empty).  If otherwise
     * eligible to terminate but workerCount is nonzero, interrupts an
     * idle worker to ensure that shutdown signals propagate. This
     * method must be called following any action that might make
     * termination possible -- reducing worker count or removing tasks
     * from the queue during shutdown. The method is non-private to
     * allow access from ScheduledThreadPoolExecutor.
     *  线程池进入 TERMINATED（终止） 状态
     */
    final void tryTerminate() {
      //循环体不断尝试
      for (; ; ) {
        int c = ctl.get();
        if (isRunning(c)
            || runStateAtLeast(c, TIDYING)
            || (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())) {
          return;
        }
        // Eligible to terminate
        if (workerCountOf(c) != 0) {
          interruptIdleWorkers(ONLY_ONE);
          return;
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
          if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
            try {
              terminated();
            } finally {
              ctl.set(ctlOf(TERMINATED, 0));
              termination.signalAll();
            }
            return;
          }
        } finally {
          mainLock.unlock();
        }
        // else retry on failed CAS
      }
    }

    /*
     * Methods for controlling interrupts to worker threads.
     */

    /**
     * If there is a security manager, makes sure caller has
     * permission to shut down threads in general (see shutdownPerm).
     * If this passes, additionally makes sure the caller is allowed
     * to interrupt each worker thread. This might not be true even if
     * first check passed, if the SecurityManager treats some threads
     * specially.
     *
     * 检查释放有Shutdown权限
     */
    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (Worker w : workers) {
                    security.checkAccess(w.thread);
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    /**
     * Interrupts all threads, even if active. Ignores SecurityExceptions
     * (in which case some threads may remain uninterrupted).
     *
     * 中断所有运行中的 worker 线程， 只在 shutdownNow 中被调用
     */
    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                w.interruptIfStarted();
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Interrupts threads that might be waiting for tasks (as
     * indicated by not being locked) so they can check for
     * termination or configuration changes. Ignores
     * SecurityExceptions (in which case some threads may remain
     * uninterrupted).
     *
     * @param onlyOne If true, interrupt at most one worker. This is
     * called only from tryTerminate when termination is otherwise
     * enabled but there are still other workers.  In this case, at
     * most one waiting worker is interrupted to propagate shutdown
     * signals in case all threads are currently waiting.
     * Interrupting any arbitrary thread ensures that newly arriving
     * workers since shutdown began will also eventually exit.
     * To guarantee eventual termination, it suffices to always
     * interrupt only one idle worker, but shutdown() interrupts all
     * idle workers so that redundant workers exit promptly, not
     * waiting for a straggler task to finish.
     *
     * 中断闲置的 worker 线程
     */
    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Common form of interruptIdleWorkers, to avoid having to
     * remember what the boolean argument means.
     */
    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    /*
     * Misc utilities, most of which are also exported to
     * ScheduledThreadPoolExecutor
     */

    /**
     * Invokes the rejected execution handler for the given command.
     * Package-protected for use by ScheduledThreadPoolExecutor.
     * 拒绝任务提交
     */
    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    /**
     * Performs any further cleanup following run state transition on
     * invocation of shutdown.  A no-op here, but used by
     * ScheduledThreadPoolExecutor to cancel delayed tasks.
     */
    void onShutdown() {
    }

    /**
     * State check needed by ScheduledThreadPoolExecutor to
     * enable running tasks during shutdown.
     *
     * @param shutdownOK true if should return true if SHUTDOWN
     */
    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    /**
     * Drains the task queue into a new list, normally using
     * drainTo. But if the queue is a DelayQueue or any other kind of
     * queue for which poll or drainTo may fail to remove some
     * elements, it deletes them one by one.
     *
     * 清空缓冲任务队列
     */
    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    /*
     * Methods for creating, running and cleaning up after workers
     * 创建 worker 线程，运行 worker 线程， worker 线程执行后清理相关的方法
     */
    /**
     * Checks if a new worker can be added with respect to current
     * pool state and the given bound (either core or maximum). If so,
     * the worker count is adjusted accordingly, and, if possible, a
     * new worker is created and started, running firstTask as its
     * first task. This method returns false if the pool is stopped or
     * eligible to shut down. It also returns false if the thread
     * factory fails to create a thread when asked.  If the thread
     * creation fails, either due to the thread factory returning
     * null, or due to an exception (typically OutOfMemoryError in
     * Thread.start()), we roll back cleanly.
     *
     * @param firstTask the task the new thread should run first (or
     * null if none). Workers are created with an initial first task
     * (in method execute()) to bypass queuing when there are fewer
     * than corePoolSize threads (in which case we always start one),
     * or when the queue is full (in which case we must bypass queue).
     * Initially idle threads are usually created via
     * prestartCoreThread or to replace other dying workers.
     *
     * @param core if true use corePoolSize as bound, else
     * maximumPoolSize. (A boolean indicator is used here rather than a
     * value to ensure reads of fresh values after checking other pool
     * state).
     * @return true if successful
     *
     * 新增一个 worker 线程
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        //尝试创建一个新线程，处理 firstTask
        //Worker内包含thread, 拉起start

        //检查是否可以创建新线程(线程池运行状态+线程池内线程数量限制)
        retry:
        for (;;) {
            //runState检查
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN && firstTask == null && ! workQueue.isEmpty())) {
                return false;
            }

            //workerCount检查
            for (;;) {
                int wc = workerCountOf(c);
                //core=true 核心线程数量限制；core=false最大线程数量限制
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)) {
                    return false;
                }
                if (compareAndIncrementWorkerCount(c)) {
                    break retry;
                }
                // Re-read ctl
                c = ctl.get();
                if (runStateOf(c) != rs) {
                    continue retry;
                }
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker worker = null;
        try {
            //为任务new 一个work执行
            worker = new Worker(firstTask);
            //获取worker内 ThreadFactory new的线程
            final Thread thread = worker.thread;
            if (thread != null) {
                //抢锁
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    // 检查线程池运行状态
                    int runState = runStateOf(ctl.get());
                    if (runState < SHUTDOWN || (runState == SHUTDOWN && firstTask == null)) {
                        // precheck that t is startable
                        //检查线程是活跃的
                        if (thread.isAlive()) {
                            throw new IllegalThreadStateException();
                        }
                        //保存到正在活跃的work集合内
                        workers.add(worker);
                        //修正largestPoolSize(达到过的最大线程数量)
                        int workersSize = workers.size();
                        if (workersSize > largestPoolSize) {
                            largestPoolSize = workersSize;
                        }
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    //任务增加成功，启动任务线程
                    thread.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted) {
                addWorkerFailed(worker);
            }
        }
        return workerStarted;
    }

    /**
     * Rolls back the worker thread creation.
     * - removes worker from workers, if present
     * - decrements worker count
     * - rechecks for termination, in case the existence of this
     *   worker was holding up termination
     *
     *   新增 worker 失败时的回滚操作
     */
    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null) {
                workers.remove(w);
            }
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Performs cleanup and bookkeeping for a dying worker. Called
     * only from worker threads. Unless completedAbruptly is set,
     * assumes that workerCount has already been adjusted to account
     * for exit.  This method removes thread from worker set, and
     * possibly terminates the pool or replaces the worker if either
     * it exited due to user task exception or if fewer than
     * corePoolSize workers are running or queue is non-empty but
     * there are no workers.
     *
     * @param w the worker
     * @param completedAbruptly if the worker died due to user exception
     *
     * worker 线程结束运行时，清理工作方法
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        // If abrupt, then workerCount wasn't adjusted
        if (completedAbruptly) {
            decrementWorkerCount();
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && ! workQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCountOf(c) >= min) {
                    return; // replacement not needed
                }
            }
            addWorker(null, false);
        }
    }

    /**
     * Performs blocking or timed wait for a task, depending on
     * current configuration settings, or returns null if this worker
     * must exit because of any of:
     * 1. There are more than maximumPoolSize workers (due to
     *    a call to setMaximumPoolSize).
     * 2. The pool is stopped.
     * 3. The pool is shutdown and the queue is empty.
     * 4. This worker timed out waiting for a task, and timed-out
     *    workers are subject to termination (that is,
     *    {@code allowCoreThreadTimeOut || workerCount > corePoolSize})
     *    both before and after the timed wait, and if the queue is
     *    non-empty, this worker is not the last thread in the pool.
     *
     * @return task, or null if the worker must exit, in which case
     *         workerCount is decremented
     *
     * 从 workQueue 获取排队中的任务， 结果可能为 null
     */
    private Runnable getTask() {
        // Did the last poll() time out?
        boolean timedOut = false;

        //循环体执行，直到找到一个task，或者队列为空结束
        for (;;) {
            int c = ctl.get();
            int runState = runStateOf(c);

            // Check if queue empty only if necessary.
            // x线程池被关闭了，或者 workQueue 已空时，减少 work 线程，并返回null
            if (runState >= SHUTDOWN && (runState >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int workerCount = workerCountOf(c);

            // Are workers subject to culling?
            // allowCoreThreadTimeOut 核心线程是否只在有限时间内获取下一个task，过期就返回 null
            // workerCount 线程数量已经超过核心线程数量
            boolean timed = allowCoreThreadTimeOut || workerCount > corePoolSize;

            // 循环重试 continue
            if ((workerCount > maximumPoolSize || (timed && timedOut))
                && (workerCount > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c)) {
                    return null;
                }
                continue;
            }

            try {
                // workQueue 中获取并移除一个任务 runnable
                Runnable runnable = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
                if (runnable != null) {
                    return runnable;
                }
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * Main worker run loop.  Repeatedly gets tasks from queue and
     * executes them, while coping with a number of issues:
     *
     * 1. We may start out with an initial task, in which case we
     * don't need to get the first one. Otherwise, as long as pool is
     * running, we get tasks from getTask. If it returns null then the
     * worker exits due to changed pool state or configuration
     * parameters.  Other exits result from exception throws in
     * external code, in which case completedAbruptly holds, which
     * usually leads processWorkerExit to replace this thread.
     *
     * 2. Before running any task, the lock is acquired to prevent
     * other pool interrupts while the task is executing, and then we
     * ensure that unless pool is stopping, this thread does not have
     * its interrupt set.
     *
     * 3. Each task run is preceded by a call to beforeExecute, which
     * might throw an exception, in which case we cause thread to die
     * (breaking loop with completedAbruptly true) without processing
     * the task.
     *
     * 4. Assuming beforeExecute completes normally, we run the task,
     * gathering any of its thrown exceptions to send to afterExecute.
     * We separately handle RuntimeException, Error (both of which the
     * specs guarantee that we trap) and arbitrary Throwables.
     * Because we cannot rethrow Throwables within Runnable.run, we
     * wrap them within Errors on the way out (to the thread's
     * UncaughtExceptionHandler).  Any thrown exception also
     * conservatively causes thread to die.
     *
     * 5. After task.run completes, we call afterExecute, which may
     * also throw an exception, which will also cause thread to
     * die. According to JLS Sec 14.20, this exception is the one that
     * will be in effect even if task.run throws.
     *
     * The net effect of the exception mechanics is that afterExecute
     * and the thread's UncaughtExceptionHandler have as accurate
     * information as we can provide about any problems encountered by
     * user code.
     *
     * @param worker the worker
     *
     * worker 执行方法，循环体，直到 worker 出现异常或找到新任务而终止
     */
    final void runWorker(Worker worker) {
        Thread currentThread = Thread.currentThread();
        Runnable task = worker.firstTask;
        worker.firstTask = null;
        worker.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            // 当前work线程的 firstTask 不为空，或者可以在 workQueue 中找到一个任务
            // while 循环实现了 work 线程的复用
            while (task != null || (task = getTask()) != null) {
                worker.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP)))
                    && !currentThread.isInterrupted()) {
                    currentThread.interrupt();
                }
                try {
                    //执行task 重写的 run 方法前置操作(程序员可以重写)
                    beforeExecute(currentThread, task);
                    Throwable thrown = null;
                    try {
                        //执行 task 重写的 run
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x;
                        throw x;
                    } catch (Error x) {
                        thrown = x;
                        throw x;
                    } catch (Throwable x) {
                        thrown = x;
                        throw new Error(x);
                    } finally {
                        //执行task 重写的 run 方法后置操作(程序员可以重写)
                        afterExecute(task, thrown);
                    }
                } finally {
                    //释放 task (可以被GC回收)
                    task = null;
                    worker.completedTasks++;
                    worker.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            // completedAbruptly = false: work 线程获取不到 task 来处理时，判定为是一个 dying(将死) 线程，需要被清理释放
            // completedAbruptly = true: try 异常时
            processWorkerExit(worker, completedAbruptly);
        }
    }

    // Public constructors and methods

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue} is null
     *
     * 线程池构造器
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} is null
     *
     *  线程池构造器
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param handler the handler to use when execution is blocked
     *        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code handler} is null
     *
     *  线程池构造器
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), handler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @param handler the handler to use when execution is blocked
     *        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} or {@code handler} is null
     *   线程池构造器
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.acc = System.getSecurityManager() == null ? null : AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     *
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code RejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *         {@code RejectedExecutionHandler}, if the task
     *         cannot be accepted for execution
     * @throws NullPointerException if {@code command} is null
     *
     * 提交新任务 Runnable 到线程池
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        /*
         * Proceed in 3 steps:
         * 分三步执行
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         * 如果当前线程池中正在运行的线程小于corePoolSize ，对于新提交的任务，会创建一个新的核心线程(corePool)来处理这个任务。
         * addWorker这个方法内会原子性的检查runStateh和workerCount这两个属性，避免出现错误的警告。
         * 换句话说:如果线程池的状态是RUNNING，线程池的大小小于核心线程数，说明还可以创建新线程，则启动新的线程执行这个任务。
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         * 即便新任务可以顺利的被放入到任务阻塞队列中，依旧需要double-check一下当前线程池是否还可以添加新线程，
         * 因为从上一次检查之后，池中的线程有可能会存在死掉的线程，又或者是线程池被shutdown了。
         * 所以需要再次检查线程池的状态,如果状态有变化的话，需要撤回新任务入对列，或者开启一个新的线程去执行刚提交的任务。
         *
         * 换句话说，如果线程池的状态是RUNNING ，线程池内的现有线程数量的大于核心线程数，小于最大线程数
         * 如果任务队列不满，可以把任务加入任务队等待处理。
         * 如果任务队列已经满了，说明现有线程已经不能支持当前的任务了，并且线程池还有继续扩充的空间，就可以创建一个新的线程来处理提交的任务,直到最大线程数。
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         *
         * 如果任务不能放入任务阻塞队列，那么就创建一个新线程来处理。如果创建线程失败，就拒绝这个任务。
         *
         * 换句话说，如果线程池的状态是RUNNING ，线程池内的现有线程数量大于最大线程数，
         * 并且任务队列已经满了，又或者是当线程池已经关闭或者上面的条件都不能满足时，
         * 则进行拒绝策略，拒绝策略在RejectedExecutionHandler接口中定义，可以有多种不同的实现。
         */
        int c = ctl.get();
        //第一步
        if (workerCountOf(c) < corePoolSize) {
            //线程数量小于核心线程数，创建一个新线程，处理command
            if (addWorker(command, true)) {
                return;
            }
            c = ctl.get();
        }
        //第二步： 线程数量已经大于核心线程数
        if (isRunning(c) && workQueue.offer(command)) {
            // 缓冲队列 workQueue 未满，可以保存待处理的 command
            int recheck = ctl.get();
            // 线程池不是 Running 时要回滚队列 workQueue
            if (! isRunning(recheck) && remove(command)) {
                reject(command);
            } else if (workerCountOf(recheck) == 0) {
                // work线程数量为 0 ？
                addWorker(null, false);
            }
        } else if (!addWorker(command, false)) {
            //第三步：当线程数量大于核心线程数量，且 workQueue 已满时，创建新线程处理任务，但不可以超过最大线程数量
            //!addWorker(command, false)
            reject(command);
        }
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     *
     * @throws SecurityException {@inheritDoc}
     *
     * 关闭线程池，会等待已经提交的任务执行完毕
     */
    @Override
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution. These tasks are drained (removed)
     * from the task queue upon return from this method.
     *
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  This implementation
     * cancels tasks via {@link Thread#interrupt}, so any task that
     * fails to respond to interrupts may never terminate.
     *
     * @throws SecurityException {@inheritDoc}
     *
     * 关闭线程池，不等待已经提交的任务执行完毕
     */
    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);
            //关闭正在运行中的任务
            interruptWorkers();
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return ! isRunning(ctl.get());
    }

    /**
     * Returns true if this executor is in the process of terminating
     * after {@link #shutdown} or {@link #shutdownNow} but has not
     * completely terminated.  This method may be useful for
     * debugging. A return of {@code true} reported a sufficient
     * period after shutdown may indicate that submitted tasks have
     * ignored or suppressed interruption, causing this executor not
     * to properly terminate.
     *
     * @return {@code true} if terminating but not yet terminated
     */
    public boolean isTerminating() {
        int c = ctl.get();
        return ! isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    @Override
    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (runStateAtLeast(ctl.get(), TERMINATED)) {
                    return true;
                }
                if (nanos <= 0) {
                    return false;
                }
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Invokes {@code shutdown} when this executor is no longer
     * referenced and it has no threads.
     */
    @Override
    protected void finalize() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null || acc == null) {
            shutdown();
        } else {
            PrivilegedAction<Void> pa = () -> { shutdown(); return null; };
            AccessController.doPrivileged(pa, acc);
        }
    }

    /**
     * Sets the thread factory used to create new threads.
     *
     * @param threadFactory the new thread factory
     * @throws NullPointerException if threadFactory is null
     * @see #getThreadFactory
     */
    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    /**
     * Returns the thread factory used to create new threads.
     *
     * @return the current thread factory
     * @see #setThreadFactory(ThreadFactory)
     */
    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /**
     * Sets a new handler for unexecutable tasks.
     *
     * @param handler the new handler
     * @throws NullPointerException if handler is null
     * @see #getRejectedExecutionHandler
     */
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }

    /**
     * Returns the current handler for unexecutable tasks.
     *
     * @return the current handler
     * @see #setRejectedExecutionHandler(RejectedExecutionHandler)
     */
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }

    /**
     * Sets the core number of threads.  This overrides any value set
     * in the constructor.  If the new value is smaller than the
     * current value, excess existing threads will be terminated when
     * they next become idle.  If larger, new threads will, if needed,
     * be started to execute any queued tasks.
     *
     * @param corePoolSize the new core size
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @see #getCorePoolSize
     *
     * 运行时修改核心线程数量， 注意当新值小于旧值时，已经存在的线程会在空闲时被终止
     */
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException();
        }
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(ctl.get()) > corePoolSize) {
            interruptIdleWorkers();
        }
        else if (delta > 0) {
            // We don't really know how many new threads are "needed".
            // As a heuristic, prestart enough new workers (up to new
            // core size) to handle the current number of tasks in
            // queue, but stop if queue becomes empty while doing so.
            int k = Math.min(delta, workQueue.size());
            while (k-- > 0 && addWorker(null, true)) {
                if (workQueue.isEmpty()) {
                    break;
                }
            }
        }
    }

    /**
     * Returns the core number of threads.
     *
     * @return the core number of threads
     * @see #setCorePoolSize
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Starts a core thread, causing it to idly wait for work. This
     * overrides the default policy of starting core threads only when
     * new tasks are executed. This method will return {@code false}
     * if all core threads have already been started.
     *
     * @return {@code true} if a thread was started
     *
     * 提前创建 一个worker 线程
     */
    public boolean prestartCoreThread() {
        return workerCountOf(ctl.get()) < corePoolSize &&
            addWorker(null, true);
    }

    /**
     * Same as prestartCoreThread except arranges that at least one
     * thread is started even if corePoolSize is 0.
     */
    void ensurePrestart() {
        int wc = workerCountOf(ctl.get());
        if (wc < corePoolSize) {
            addWorker(null, true);
        } else if (wc == 0) {
            addWorker(null, false);
        }
    }

    /**
     * Starts all core threads, causing them to idly wait for work. This
     * overrides the default policy of starting core threads only when
     * new tasks are executed.
     *
     * @return the number of threads started
     *
     * 提前创建 corePoolSize 个 worker 线程
     */
    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true)) {
            ++n;
        }
        return n;
    }

    /**
     * Returns true if this pool allows core threads to time out and
     * terminate if no tasks arrive within the keepAlive time, being
     * replaced if needed when new tasks arrive. When true, the same
     * keep-alive policy applying to non-core threads applies also to
     * core threads. When false (the default), core threads are never
     * terminated due to lack of incoming tasks.
     *
     * @return {@code true} if core threads are allowed to time out,
     *         else {@code false}
     *
     * @since 1.6
     *
     * 释放允许核心线程在闲置时被终止
     */
    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    /**
     * Sets the policy governing whether core threads may time out and
     * terminate if no tasks arrive within the keep-alive time, being
     * replaced if needed when new tasks arrive. When false, core
     * threads are never terminated due to lack of incoming
     * tasks. When true, the same keep-alive policy applying to
     * non-core threads applies also to core threads. To avoid
     * continual thread replacement, the keep-alive time must be
     * greater than zero when setting {@code true}. This method
     * should in general be called before the pool is actively used.
     *
     * @param value {@code true} if should time out, else {@code false}
     * @throws IllegalArgumentException if value is {@code true}
     *         and the current keep-alive time is not greater than zero
     *
     * @since 1.6
     */
    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value) {
                interruptIdleWorkers();
            }
        }
    }

    /**
     * Sets the maximum allowed number of threads. This overrides any
     * value set in the constructor. If the new value is smaller than
     * the current value, excess existing threads will be
     * terminated when they next become idle.
     *
     * @param maximumPoolSize the new maximum
     * @throws IllegalArgumentException if the new maximum is
     *         less than or equal to zero, or
     *         less than the {@linkplain #getCorePoolSize core pool size}
     * @see #getMaximumPoolSize
     *
     * 运行时设置最大线程数量
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize) {
            interruptIdleWorkers();
        }
    }

    /**
     * Returns the maximum allowed number of threads.
     *
     * @return the maximum allowed number of threads
     * @see #setMaximumPoolSize
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * Sets the time limit for which threads may remain idle before
     * being terminated.  If there are more than the core number of
     * threads currently in the pool, after waiting this amount of
     * time without processing a task, excess threads will be
     * terminated.  This overrides any value set in the constructor.
     *
     * @param time the time to wait.  A time value of zero will cause
     *        excess threads to terminate immediately after executing tasks.
     * @param unit the time unit of the {@code time} argument
     * @throws IllegalArgumentException if {@code time} less than zero or
     *         if {@code time} is zero and {@code allowsCoreThreadTimeOut}
     * @see #getKeepAliveTime(TimeUnit)
     *
     * 设置 worker 的闲置等待任务时间， 超过该时间， worker线程可能会被终止
     */
    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException();
        }
        if (time == 0 && allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0) {
            interruptIdleWorkers();
        }
    }

    /**
     * Returns the thread keep-alive time, which is the amount of time
     * that threads in excess of the core pool size may remain
     * idle before being terminated.
     *
     * @param unit the desired time unit of the result
     * @return the time limit
     * @see #setKeepAliveTime(long, TimeUnit)
     */
    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    /* User-level queue utilities */

    /**
     * Returns the task queue used by this executor. Access to the
     * task queue is intended primarily for debugging and monitoring.
     * This queue may be in active use.  Retrieving the task queue
     * does not prevent queued tasks from executing.
     *
     * @return the task queue
     *
     * 获取缓存任务队列
     */
    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    /**
     * Removes this task from the executor's internal queue if it is
     * present, thus causing it not to be run if it has not already
     * started.
     *
     * <p>This method may be useful as one part of a cancellation
     * scheme.  It may fail to remove tasks that have been converted
     * into other forms before being placed on the internal queue. For
     * example, a task entered using {@code submit} might be
     * converted into a form that maintains {@code Future} status.
     * However, in such cases, method {@link #purge} may be used to
     * remove those Futures that have been cancelled.
     *
     * @param task the task to remove
     * @return {@code true} if the task was removed
     */
    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }

    /**
     * Tries to remove from the work queue all {@link Future}
     * tasks that have been cancelled. This method can be useful as a
     * storage reclamation operation, that has no other impact on
     * functionality. Cancelled tasks are never executed, but may
     * accumulate in work queues until worker threads can actively
     * remove them. Invoking this method instead tries to remove them now.
     * However, this method may fail to remove tasks in
     * the presence of interference by other threads.
     *
     * 清空 workQueue
     */
    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled()) {
                    it.remove();
                }
            }
        } catch (ConcurrentModificationException fallThrough) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            for (Object r : q.toArray()) {
                if (r instanceof Future<?> && ((Future<?>) r).isCancelled()) {
                    q.remove(r);
                }
            }
        }

        tryTerminate(); // In case SHUTDOWN and now empty
    }

    /* Statistics */

    /**
     * Returns the current number of threads in the pool.
     *
     * @return the number of threads
     *
     * 获取运行中的 worker 线程数量
     */
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // Remove rare and surprising possibility of
            // isTerminated() && getPoolSize() > 0
            return runStateAtLeast(ctl.get(), TIDYING) ? 0
                : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate number of threads that are actively
     * executing tasks.
     *
     * @return the number of threads
     *
     * 获取正在执行任务的 worker 线程的数量， 这是一个相近值，不是真实值。
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers) {
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the largest number of threads that have ever
     * simultaneously been in the pool.
     *
     * @return the number of threads
     */
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate total number of tasks that have ever been
     * scheduled for execution. Because the states of tasks and
     * threads may change dynamically during computation, the returned
     * value is only an approximation.
     *
     * @return the number of tasks
     */
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns the approximate total number of tasks that have
     * completed execution. Because the states of tasks and threads
     * may change dynamically during computation, the returned value
     * is only an approximation, but one that does not ever decrease
     * across successive calls.
     *
     * @return the number of tasks
     */
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Returns a string identifying this pool, as well as its state,
     * including indications of run state and estimated worker and
     * task counts.
     *
     * @return a string identifying this pool, as well as its state
     */
    @Override
    public String toString() {
        long ncompleted;
        int nworkers, nactive;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = completedTaskCount;
            nactive = 0;
            nworkers = workers.size();
            for (Worker w : workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked()) {
                    ++nactive;
                }
            }
        } finally {
            mainLock.unlock();
        }
        int c = ctl.get();
        String rs = (runStateLessThan(c, SHUTDOWN) ? "Running" :
                     (runStateAtLeast(c, TERMINATED) ? "Terminated" :
                      "Shutting down"));
        return super.toString() +
            "[" + rs +
            ", pool size = " + nworkers +
            ", active threads = " + nactive +
            ", queued tasks = " + workQueue.size() +
            ", completed tasks = " + ncompleted +
            "]";
    }

    /* Extension hooks */

    /**
     * Method invoked prior to executing the given Runnable in the
     * given thread.  This method is invoked by thread {@code t} that
     * will execute task {@code r}, and may be used to re-initialize
     * ThreadLocals, or to perform logging.
     *
     * <p>This implementation does nothing, but may be customized in
     * subclasses. Note: To properly nest multiple overridings, subclasses
     * should generally invoke {@code super.beforeExecute} at the end of
     * this method.
     *
     * @param t the thread that will run task {@code r}
     * @param r the task that will be executed
     *
     * Runnable 任务被执行前需要执行的操作， 该方法可以被研发人员自定义，用以完成一些特殊逻辑
     */
    protected void beforeExecute(Thread t, Runnable r) { }

    /**
     * Method invoked upon completion of execution of the given Runnable.
     * This method is invoked by the thread that executed the task. If
     * non-null, the Throwable is the uncaught {@code RuntimeException}
     * or {@code Error} that caused execution to terminate abruptly.
     *
     * <p>This implementation does nothing, but may be customized in
     * subclasses. Note: To properly nest multiple overridings, subclasses
     * should generally invoke {@code super.afterExecute} at the
     * beginning of this method.
     *
     * <p><b>Note:</b> When actions are enclosed in tasks (such as
     * {@link FutureTask}) either explicitly or via methods such as
     * {@code submit}, these task objects catch and maintain
     * computational exceptions, and so they do not cause abrupt
     * termination, and the internal exceptions are <em>not</em>
     * passed to this method. If you would like to trap both kinds of
     * failures in this method, you can further probe for such cases,
     * as in this sample subclass that prints either the direct cause
     * or the underlying exception if a task has been aborted:
     *
     *  <pre> {@code
     * class ExtendedExecutor extends ThreadPoolExecutor {
     *   // ...
     *   protected void afterExecute(Runnable r, Throwable t) {
     *     super.afterExecute(r, t);
     *     if (t == null && r instanceof Future<?>) {
     *       try {
     *         Object result = ((Future<?>) r).get();
     *       } catch (CancellationException ce) {
     *           t = ce;
     *       } catch (ExecutionException ee) {
     *           t = ee.getCause();
     *       } catch (InterruptedException ie) {
     *           Thread.currentThread().interrupt(); // ignore/reset
     *       }
     *     }
     *     if (t != null)
     *       System.out.println(t);
     *   }
     * }}</pre>
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     * execution completed normally
     *
     * Runnable 任务被执行后需要执行的操作， 该方法可以被研发人员自定义，用以完成一些特殊逻辑
     *
     */
    protected void afterExecute(Runnable r, Throwable t) { }

    /**
     * Method invoked when the Executor has terminated.  Default
     * implementation does nothing. Note: To properly nest multiple
     * overridings, subclasses should generally invoke
     * {@code super.terminated} within this method.
     */
    protected void terminated() { }

    /* Predefined RejectedExecutionHandlers */

    /**
     * A handler for rejected tasks that runs the rejected task
     * directly in the calling thread of the {@code execute} method,
     * unless the executor has been shut down, in which case the task
     * is discarded.
     *
     * 用调用者所在的线程来执行任务（一个任务都不会丢，但有可能造成主任务阻塞）
     */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code CallerRunsPolicy}.
         */
        public CallerRunsPolicy() { }

        /**
         * Executes task r in the caller's thread, unless the executor
         * has been shut down, in which case the task is discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    /**
     * A handler for rejected tasks that throws a
     * {@code RejectedExecutionException}.
     *
     * 直接抛出异常，（会造成主任务终止）
     * 这是线程池的默认策略
     */
    public static class AbortPolicy implements RejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        public AbortPolicy() { }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
        }
    }

    /**
     * A handler for rejected tasks that silently discards the
     * rejected task.
     *
     * 直接丢弃任务 （会丢任务）
     */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code DiscardPolicy}.
         */
        public DiscardPolicy() { }

        /**
         * Does nothing, which has the effect of discarding task r.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    /**
     * A handler for rejected tasks that discards the oldest unhandled
     * request and then retries {@code execute}, unless the executor
     * is shut down, in which case the task is discarded.
     *
     * 丢弃阻塞队列中靠最前的任务，并执行当前任务（会丢任务）
     */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code DiscardOldestPolicy} for the given executor.
         */
        public DiscardOldestPolicy() { }

        /**
         * Obtains and ignores the next task that the executor
         * would otherwise execute, if one is immediately available,
         * and then retries execution of task r, unless the executor
         * is shut down, in which case task r is instead discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
