package guava.base.classs;

import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

public class EquivalenceTest {
  /**
   * 是否相同判断
   */

  public static void main(String[] args){
    Equivalence equalsa = Equivalence.equals();
    System.out.println(equalsa.equivalent(1,1));

    Equivalence identitya = Equivalence.identity();
    System.out.println(identitya.equivalent(1,1));



    System.out.println(identitya.wrap("a").equals(identitya.wrap("a")));
    System.out.println(identitya.wrap("a").equals(identitya.wrap("hello")));


    String a1 = "a";
    String a2 = "a";
    String a3 = new String("a");
    String a4 = "b";
   // String a1 = "a";
    Set<Equivalence.Wrapper<String>> identityHashSet = Sets.newHashSet();
    identityHashSet.add(Equivalence.identity().wrap(a1));
    identityHashSet.add(Equivalence.identity().wrap(a2));
    identityHashSet.add(Equivalence.identity().wrap(a3));
    identityHashSet.add(Equivalence.identity().wrap(a4));
    System.out.println(identityHashSet.size());

    Set<String> hashSet = Sets.newHashSet();
    hashSet.add(a1);
    hashSet.add(a2);
    hashSet.add(a3);
    hashSet.add(a4);
    System.out.println(hashSet.size());
    }
}
