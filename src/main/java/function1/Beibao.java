package function1;

/**
 * 01背包
 */
public class Beibao {

  public int maxValue(int[] weight, int[] value, int W) {
    int n = weight.length;
    if (n == 0) {
      return 0;
    }
    // 辅助空间只需要O(W)即可
    int[] dp = new int[W + 1];
    for (int i = 0; i < n; i++) {
      // 注意这里必须从后向前！！！
      for (int k = W; k >= 1; k--) {
        int valueWith_i = (k - weight[i] >= 0) ? (dp[k - weight[i]] + value[i]) : 0;
        int valueWithout_i = dp[k];
        dp[k] = Math.max(valueWith_i, valueWithout_i);
      }
    }
    return dp[W];
  }

  public static void main(String[] args) {
    int[] weight = new int[]{1,2,3,4};
    int[] value = new int[]{2,3,4,10};
    int W = 7;
    Beibao beibao = new Beibao();
    System.out.print(beibao.maxValue(weight,value,W));
  }

}
