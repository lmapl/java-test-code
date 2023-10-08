package guava.base.interfaces;

import com.google.common.base.FinalizablePhantomReference;
import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableSoftReference;
import com.google.common.base.FinalizableWeakReference;

public class FinalizableReferenceTest {
  /**
   * java.lang.ref.Reference 引用基类； 强引用，软引用，弱引用，虚引用
   * 引用的强弱顺序是强、软、弱、和虚
   * finalize() 方法 有关系
   * finalize() 方法在垃圾收集器将对象从内存中清除出去之前被调用，完成必要的清理工作
   * finalize() 方法是在 Object 类中定义的，所有的类都继承了它。
   */
  public static void main(String[] args){
    //FinalizableReference finalizableReference;
    //三个实现类，都是java.lang.ref.Reference的子类
    //PhantomRefrence(虚引用)
    FinalizablePhantomReference finalizablePhantomReference;
    //SoftReference(软引用)
    FinalizableSoftReference finalizableSoftReference;
    //WeakReference(弱引用)
    FinalizableWeakReference finalizableWeakReference;

    FinalizableReferenceQueue finalizableReferenceQueue;
  }
}
