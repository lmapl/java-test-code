/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package source_code.java_locks;

/**
 * A synchronizer(同步器) that may be exclusively(独占的) owned by(属于) a thread.
 * This class provides a basis(基础/基准) for creating locks and related(相关的) synchronizers
 * that may entail(涉及) a notion of ownership(所有权概念).
 * The
 * {@code AbstractOwnableSynchronizer} class itself does not manage or
 * use this information.
 *
 * However, subclasses(子类) and tools may use
 * appropriately maintained values(适当维护这些值) to help(来帮助) control and monitor access(访问通道)
 * and provide diagnostics(诊断).
 * @since 1.6
 * @author Doug Lea
 */
public abstract class AbstractOwnableSynchronizer
    implements java.io.Serializable {

    /** Use serial ID even though all fields transient. */
    private static final long serialVersionUID = 3737899427754241961L;

    /**
     * Empty constructor for use by subclasses.
     * 空构造器
     */
    protected AbstractOwnableSynchronizer() { }

    /**
     * The current owner of exclusive mode(独占模式) synchronization.
     * 独占式同步器的 当前的 owner 线程
     */
    private transient Thread exclusiveOwnerThread;

    /**
     * Sets the thread that currently owns exclusive access.
     * A {@code null} argument indicates that no thread owns access.
     * This method does not otherwise impose any synchronization or
     * {@code volatile} field accesses.
     * @param thread the owner thread
     */
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    /**
     * Returns the thread last set by {@code setExclusiveOwnerThread},
     * or {@code null} if never set.  This method does not otherwise
     * impose any synchronization or {@code volatile} field accesses.
     * @return the owner thread
     */
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
