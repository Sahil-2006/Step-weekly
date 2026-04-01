import java.util.Arrays;
import java.util.Random;

/**
 * Problem 4: Portfolio Return Sorting
 */
class Asset {
    String name;
    double returnRate;
    double volatility;

    public Asset(String name, double returnRate, double volatility) {
        this.name = name;
        this.returnRate = returnRate;
        this.volatility = volatility;
    }

    @Override
    public String toString() {
        return name + ":" + (int) returnRate + "%";
    }
}

public class P4_PortfolioReturnSorting {

    public static void mergeSort(Asset[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    private static void merge(Asset[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;
        Asset[] L = new Asset[n1];
        Asset[] R = new Asset[n2];
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);
        
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            // Stable Sort: Left subarray element selected if tie
            if (L[i].returnRate <= R[j].returnRate) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public static void quickSort(Asset[] arr, int low, int high) {
        if (low < high) {
            // Pivot Selection (Median-of-3 or random)
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(Asset[] arr, int low, int high) {
        // Simple random pivot
        int pivotIdx = low + new Random().nextInt(high - low + 1);
        Asset tempP = arr[pivotIdx];
        arr[pivotIdx] = arr[high];
        arr[high] = tempP;
        
        Asset pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            // DESC returnRate + ASC volatility
            if (arr[j].returnRate > pivot.returnRate || 
               (arr[j].returnRate == pivot.returnRate && arr[j].volatility < pivot.volatility)) {
                i++;
                Asset temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Asset temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        Asset[] data = {
            new Asset("AAPL", 12.0, 5.0),
            new Asset("TSLA", 8.0, 15.0),
            new Asset("GOOG", 15.0, 8.0)
        };

        Asset[] mData = data.clone();
        mergeSort(mData, 0, mData.length - 1);
        System.out.println("Merge: " + Arrays.toString(mData));

        Asset[] qData = data.clone();
        quickSort(qData, 0, qData.length - 1);
        System.out.println("Quick (desc): " + Arrays.toString(qData));
    }
}
