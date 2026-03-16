import java.util.*;

/**
 * Problem 9: Two-Sum Problem Variants for Financial Transactions
 * Detects fraud and duplicate payments using hash map lookups.
 */
public class P9_TwoSumFinancialTransactions {

    private static class Transaction {
        int id;
        double amount;
        String merchant;
        long timestamp; // ms

        Transaction(int id, double amount, String merchant) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final List<Transaction> allTransactions = new ArrayList<>();
    // amount -> Transaction (Used for Two-Sum O(1) lookup)
    private final Map<Double, Transaction> amountLookup = new HashMap<>();

    public void addTransaction(Transaction tx) {
        allTransactions.add(tx);
        amountLookup.put(tx.amount, tx);
    }

    /**
     * Finds pairs of transactions that sum exactly to a target (Typical Money Laundering pattern)
     */
    public List<String> findTwoSum(double target) {
        List<String> results = new ArrayList<>();
        Map<Double, Transaction> seen = new HashMap<>();
        
        for (Transaction tx : allTransactions) {
            double complement = target - tx.amount;
            if (seen.containsKey(complement)) {
                results.add(String.format("Found Pair: ID:%d ($%.2f) and ID:%d ($%.2f)", 
                    seen.get(complement).id, complement, tx.id, tx.amount));
            }
            seen.put(tx.amount, tx);
        }
        return results;
    }

    /**
     * Detects potential duplicate payments (Same amount, same merchant)
     */
    public void detectDuplicates() {
        Map<String, List<Transaction>> merchantTxMap = new HashMap<>();
        for (Transaction tx : allTransactions) {
            String key = tx.merchant + ":" + tx.amount; // Composite key
            merchantTxMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tx);
        }

        System.out.println("\n--- Duplicate Alerts ---");
        merchantTxMap.forEach((key, list) -> {
            if (list.size() > 1) {
                System.out.printf("Alert: %d transactions for %s totalling $%.2f%n", 
                    list.size(), key.split(":")[0], Double.parseDouble(key.split(":")[1]));
            }
        });
    }

    public static void main(String[] args) {
        P9_TwoSumFinancialTransactions processor = new P9_TwoSumFinancialTransactions();

        processor.addTransaction(new Transaction(1, 500, "Store A"));
        processor.addTransaction(new Transaction(2, 300, "Store B"));
        processor.addTransaction(new Transaction(3, 200, "Store C"));
        processor.addTransaction(new Transaction(4, 500, "Store A")); // Possible Duplicate

        System.out.println("Two Sum for 500: " + processor.findTwoSum(500));
        processor.detectDuplicates();
    }
}
