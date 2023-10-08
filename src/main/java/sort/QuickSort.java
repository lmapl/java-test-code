package sort;

import java.util.Arrays;

public class QuickSort {

  public int[] sort(int[] sourceArray) {
    // 对 arr 进行拷贝，不改变参数内容
    int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

    return quickSort(arr, 0, arr.length - 1);
  }

  private int[] quickSort(int[] arr, int left, int right) {
    if (left < right) {
      int partitionIndex = partition(arr, left, right);
      printAr(arr);
      quickSort(arr, left, partitionIndex - 1);
      quickSort(arr, partitionIndex + 1, right);
    }
    return arr;
  }

  private int partition(int[] arr, int left, int right) {
    // 设定基准值（pivot）
    int pivot = left;
    int index = pivot + 1;
    for (int i = index; i <= right; i++) {
      if (arr[i] < arr[pivot]) {
        swap(arr, i, index);
        index++;
      }
    }
    swap(arr, pivot, index - 1);
    return index - 1;
  }

  private void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  public static void main(String[] args) {
    //int[] arr = new int[] {19, 12, 15, 20, 5, 21, 17, 24, 2, 4, 15, 5, 9, 5, 9, 12, 34};
    int[] arr = new int[] {7,8,1,4,11,3,9,10};
    QuickSort quickSort = new QuickSort();
    arr = quickSort.sort(arr);
    quickSort.printAr(arr);


  }

  private void printAr(int[] arr){
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println(" ");
  }
}
