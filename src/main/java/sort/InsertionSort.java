package sort;

public class InsertionSort {

  public static void sort(int[] arr){
    for(int i =0; i < arr.length; i++){
      for(int j = i; j > 0; j--){
        if(arr[j] < arr[j-1]){
          int temp = arr[j];
          arr[j] = arr[j-1];
          arr[j-1] = temp;
          print(arr);
        }
      }
    }
  }

  public static void main(String[] args){

    int[] arr = new int[]{1,5,6,3,3,5,8};

    InsertionSort.sort(arr);
    print(arr);


  }

  public static void print(int[] arr){
    for(int i=0;i <arr.length;i++){
      System.out.print(arr[i]+" ");
    }
    System.out.println(" ");

  }
}
