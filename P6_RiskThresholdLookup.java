
/**
 * Problem 6: Risk Threshold Binary Lookup
 */
public class P6_RiskThresholdLookup {

    public static int linearSearch(int[] bands, int threshold) {
        int comparisons = 0;
        for (int b : bands) {
            comparisons++;
            if (b == threshold) return comparisons;
        }
        System.out.println("Linear: threshold=" + threshold + " - not found (" + comparisons + " comps)");
        return -1;
    }

    public static void binaryFloorCeiling(int[] bands, int target) {
        int low = 0, high = bands.length - 1;
        int floor = -1, ceil = -1;
        int comparisons = 0;
        
        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            if (bands[mid] == target) {
                floor = ceil = bands[mid];
                break;
            } else if (bands[mid] < target) {
                floor = bands[mid];
                low = mid + 1;
            } else {
                ceil = bands[mid];
                high = mid - 1;
            }
        }
        System.out.println("Binary floor(" + target + "): " + floor + ", ceiling: " + ceil + " (" + comparisons + " comps)");
    }

    public static void main(String[] args) {
        int[] sortedBands = {10, 25, 50, 100};
        
        linearSearch(sortedBands, 30);
        binaryFloorCeiling(sortedBands, 30);
    }
}
