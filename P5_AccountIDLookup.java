import java.util.Arrays;

/**
 * Problem 5: Account ID Lookup in Transaction Logs
 */
class Log {
    String accountId;
    String details;

    public Log(String accountId, String details) {
        this.accountId = accountId;
        this.details = details;
    }

    @Override
    public String toString() {
        return accountId;
    }
}

public class P5_AccountIDLookup {

    public static void linearSearch(Log[] logs, String target) {
        int comparisons = 0;
        int firstOcc = -1;
        for (int i = 0; i < logs.length; i++) {
            comparisons++;
            if (logs[i].accountId.equals(target)) {
                if (firstOcc == -1) firstOcc = i;
            }
        }
        System.out.println("Linear first " + target + ": index " + firstOcc + " (" + comparisons + " comparisons)");
    }

    public static void binarySearch(Log[] logs, String target) {
        int low = 0, high = logs.length - 1;
        int comparisons = 0;
        int index = -1;
        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            int res = logs[mid].accountId.compareTo(target);
            if (res == 0) {
                index = mid;
                high = mid - 1; // Find first occurrence
            } else if (res < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        
        int count = 0;
        for (Log l : logs) if (l.accountId.equals(target)) count++;
        
        System.out.println("Binary " + target + ": index " + index + " (" + comparisons + " comparisons), count=" + count);
    }

    public static void main(String[] args) {
        Log[] data = {
            new Log("accB", "T1"),
            new Log("accA", "T2"),
            new Log("accB", "T3"),
            new Log("accC", "T4")
        };

        linearSearch(data, "accB");

        // Sort for binary search
        Arrays.sort(data, (a, b) -> a.accountId.compareTo(b.accountId));
        binarySearch(data, "accB");
    }
}
