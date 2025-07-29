package Sorting;

public class QuickSort implements SortingAlgorithm {
    @Override
    public void sort(String[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(String[] arr, int first, int last) {
        if (last > first) {
            int pivotIndex = partition(arr, first, last);
            quickSort(arr, first, pivotIndex - 1); // recursive call
            quickSort(arr, pivotIndex + 1, last);
        }
    }

    private int partition(String[] arr, int first, int last) {
        int randomPivotIndex = first + (int)(Math.random() * (last - first + 1));
        String temp = arr[randomPivotIndex];
        arr[randomPivotIndex] = arr[first]; // place random pivot at first
        arr[first] = temp;

        String pivot = arr[first]; // select the first element as the pivot
        int low = first + 1;
        int high = last;

        while (high > low) {
            // Forward scan: find element greater than the pivot
            while (low <= high && arr[low].compareTo(pivot) <= 0) {
                low++;
            }
            // Backward scan: find element less than or equal to the pivot
            while (low <= high && arr[high].compareTo(pivot) > 0) {
                high--;
            }
            if (high > low) {
                // Swap elements at low and high
                String temp1 = arr[high];
                arr[high] = arr[low];
                arr[low] = temp1;
            }
        }

        // Adjust high to handle duplicate elements
        while (high > first && arr[high].compareTo(pivot) >= 0) {
            high--;
        }

        // Place pivot in its correct position
        if (pivot.compareTo(arr[high]) > 0) {
            arr[first] = arr[high];
            arr[high] = pivot;
            return high;
        } else {
            return first;
        }
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }
}
