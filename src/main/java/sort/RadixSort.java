package sort;

import java.util.Arrays;

/**
 * 基数排序
 * 考虑负数的情况还可以参考： https://code.i-harness.com/zh-CN/q/e98fa9
 */
public class RadixSort {

  public int[] sort(int[] sourceArray) {
    // 对 arr 进行拷贝，不改变参数内容
    int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

    int min = getMinValue(sourceArray);
    int maxDigit = getMaxDigit(arr,min);
    return radixSort(arr, maxDigit,min);
  }

  /**
   * 获取最高位数
   */
  private int getMaxDigit(int[] arr,int min) {
    int maxValue = getMaxValue(arr);
    return getNumLenght(maxValue,min);
  }

  private int getMaxValue(int[] arr) {
    int maxValue = arr[0];
    for (int value : arr) {
      if (maxValue < value) {
        maxValue = value;
      }
    }
    return maxValue;
  }

  private int getMinValue(int[] arr) {
    int minValue = arr[0];
    for (int value : arr) {
      if (minValue > value) {
        minValue = value;
      }
    }
    return minValue;
  }

  protected int getNumLenght(long num, int min) {
    if (num == 0) {
      return 1;
    }
    int lenght = 0;
    for (long temp = num; temp != 0; temp /= min) {
      lenght++;
    }
    return lenght;
  }

  private int[] radixSort(int[] arr, int maxDigit, int min) {
    int mod = min;
    int dev = 1;

    for (int i = 0; i < maxDigit; i++, dev *= min, mod *= min) {
      // 考虑负数的情况，这里扩展一倍队列数，其中 [0-9]对应负数，[10-19]对应正数 (bucket + 10)
      int[][] counter = new int[mod * 2][0];

      for (int j = 0; j < arr.length; j++) {
        int bucket = ((arr[j] % mod) / dev) + mod;
        counter[bucket] = arrayAppend(counter[bucket], arr[j]);
      }

      int pos = 0;
      for (int[] bucket : counter) {
        for (int value : bucket) {
          arr[pos++] = value;
        }
      }

      for (int m = 0; m < arr.length; m++) {
        System.out.print(arr[m] + " ");
      }
      System.out.println( " ");

    }

    return arr;
  }

  /**
   * 自动扩容，并保存数据
   */
  private int[] arrayAppend(int[] arr, int value) {
    arr = Arrays.copyOf(arr, arr.length + 1);
    arr[arr.length - 1] = value;
    return arr;
  }

  public static void main(String[] args) {
    int[] sourceArray = new int[] {23, 4, 65, 2, 199, 24, 67, 89, 10, 28};

    for (int i = 0; i < sourceArray.length; i++) {
      System.out.print(sourceArray[i] + " ");
    }

    System.out.println( " ");

    RadixSort radixSort = new RadixSort();
    sourceArray = radixSort.sort(sourceArray);
  }
}