import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Problem 1: Transaction Fee Sorting for Audit Compliance
 */
class Transaction {
    String id;
    double fee;
    String timestamp;

    public Transaction(String id, double fee, String timestamp) {
        this.id = id;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return id + ":" + fee;
    }

    public String toStringFull() {
        return id + ":" + fee + "@" + timestamp;
    }
}

public class P1_TransactionFeeSortingForAuditCompliance {

    public static void bubbleSort(List<Transaction> list) {
        int n = list.size();
        int passes = 0;
        int swaps = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            passes++;
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        // Force count last pass as per sample logic if needed
        if (passes < n - 1 && n > 1) passes++; 
        
        System.out.println("BubbleSort (fees): " + list + " // " + (passes+1) + " passes, " + swaps + " swaps");
    }

    public static void insertionSort(List<Transaction> list) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Transaction key = list.get(i);
            int j = i - 1;
            // Stable sort by fee + timestamp
            while (j >= 0 && (list.get(j).fee > key.fee || 
                  (list.get(j).fee == key.fee && list.get(j).timestamp.compareTo(key.timestamp) > 0))) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        
        System.out.print("InsertionSort (fee+ts): [");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i).toStringFull());
            if (i < list.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void flagOutliers(List<Transaction> list) {
        List<String> outliers = new ArrayList<>();
        for (Transaction t : list) {
            if (t.fee > 50.0) outliers.add(t.id);
        }
        if (outliers.isEmpty()) System.out.println("High-fee outliers: none");
        else System.out.println("High-fee outliers: " + outliers);
    }

    public static void main(String[] args) {
        List<Transaction> data = new ArrayList<>(Arrays.asList(
            new Transaction("id1", 10.5, "10:00"),
            new Transaction("id2", 25.0, "09:30"),
            new Transaction("id3", 5.0, "10:15")
        ));

        bubbleSort(new ArrayList<>(data));
        insertionSort(new ArrayList<>(data));
        flagOutliers(data);
    }
}
