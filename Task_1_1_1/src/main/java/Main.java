import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    int[] arr = {5, 10, -1, 7, 14, 42};
    sort(arr);
    System.out.println(Arrays.toString(arr));
  }

  /**
   * Sorts an array using heapsort
   * 
   * @param arr array to sort. mutable
   */
  static void sort(int[] arr) {
    for (int i = arr.length / 2 - 1; i >= 0; i--)
      heapify(arr, arr.length, i);

    for (int i = arr.length - 1 ; i > 0 ; i--) {
      int tmp = arr[0];
      arr[0] = arr[i];
      arr[i] = tmp;

      heapify(arr, i, 0);
    }
  }

  /**
   * Restores the heap property for the subtree rooted at i.
   * 
   * @param arr array to heapify
   * @param n size of the heap
   * @param i root index
   */
  static void heapify(int[] arr, int n, int i) {
    int largest = i;
    int left = 2 * i + 1;
    int right = 2 * i + 2;

    if (left < n && arr[largest] < arr[left])
      largest = left;
    if (right < n && arr[largest] < arr[right])
      largest = right;

    if (largest != i) {
      int tmp = arr[largest];
      arr[largest] = arr[i];
      arr[i] = tmp;

      heapify(arr, n, largest);
    }
  }
}