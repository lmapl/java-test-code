package source_code.ac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AhoCorasickDoubleArrayTrieTest {
  /**
   * ac自动机字符串匹配
   */
  public static void main(String[] args){
    Map<String,String> sensitiveMap = new HashMap<>();
    sensitiveMap.put("习大大","习大大");
    sensitiveMap.put("赌博","赌博");
    //sensitiveMap.put("狗东西","狗东西");
    //sensitiveMap.put("习近平","习近平");
    sensitiveMap.put("习大但","习大但");
    //sensitiveMap.put("够大但","够大但");
    //sensitiveMap.put("abc kekk","abc kekk");
    //sensitiveMap.put("苟富贵","苟富贵");
    //sensitiveMap.put("完了","完了");
    AhoCorasickDoubleArrayTrie<String> act = new AhoCorasickDoubleArrayTrie<>();
    act.build(sensitiveMap);

    List<AhoCorasickDoubleArrayTrie.Hit<String>> v =act.parseText("习大大够大但你他习大但abc kekk妈完了");
    System.out.print(v.get(0).toString());
  }
}
