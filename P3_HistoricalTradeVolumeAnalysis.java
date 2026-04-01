import java.util.Arrays;

/**
 * Problem 3: Historical Trade Volume Analysis
 */
class Trade {
    String id;
    int volume;

    public Trade(String id, int volume) {
        this.id = id;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return id + ":" + volume;
    }
}

public class P3_HistoricalTradeVolumeAnalysis {

    public static void mergeSort(Trade[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    private static void merge(Trade[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;
        Trade[] L = new Trade[n1];
        Trade[] R = new Trade[n2];
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);
        
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (L[i].volume <= R[j].volume) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public static void quickSort(Trade[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(Trade[] arr, int low, int high) {
        Trade pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            // Descending
            if (arr[j].volume > pivot.volume) {
                i++;
                Trade temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Trade temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        Trade[] data = {
            new Trade("trade3", 500),
            new Trade("trade1", 100),
            new Trade("trade2", 300)
        };

        Trade[] mData = data.clone();
        mergeSort(mData, 0, mData.length - 1);
        System.out.println("MergeSort: " + Arrays.toString(mData));

        Trade[] qData = data.clone();
        quickSort(qData, 0, qData.length - 1);
        System.out.println("QuickSort (desc): " + Arrays.toString(qData));

        long total = 0;
        for (Trade t : data) total += t.volume;
        System.out.println("Merged morning+afternoon total: " + total);
    }
}
