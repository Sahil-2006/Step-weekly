import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 2: E-commerce Flash Sale Inventory Manager
 * 
 * Scenario: 50,000 customers trying to buy 100 units simultaneously.
 * Goal: Prevent overselling, High performance (O(1)), Handle Waiting List.
 */
public class P2_FlashSaleInventoryManager {

    // O(1) Lookup: ProductId -> Current Stock Count
    // Use ConcurrentHashMap for thread-safe O(1) access
    private final Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // Waiting List: ProductId -> Queue of User IDs (FIFO)
    // Use LinkedBlockingQueue or similar for thread-safe waiting list
    private final Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    /**
     * Initializes a product with initial stock.
     */
    public void addProduct(String productId, int initialStock) {
        inventory.put(productId, new AtomicInteger(initialStock));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());
    }

    /**
     * Checks stock level in O(1) time.
     */
    public int getStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    /**
     * Processes purchase requests in O(1) time (amortized).
     * Handles concurrent requests safely using Atomic operations.
     */
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);
        
        if (stock == null) {
            return "Error: Product not found.";
        }

        // Attempt a non-blocking thread-safe decrement
        while (true) {
            int currentStock = stock.get();
            if (currentStock <= 0) {
                // Stock exhausted, add to waiting list (FIFO)
                Queue<Integer> waitList = waitingLists.get(productId);
                waitList.add(userId);
                
                // Get current position in waiting list
                int position = 0;
                Iterator<Integer> it = waitList.iterator();
                while(it.hasNext()) {
                    position++;
                    if (it.next() == userId) break;
                }
                return "Added to waiting list, position #" + position;
            }

            // Try to update stock atomically
            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "Success, " + (currentStock - 1) + " units remaining";
            }
            // If compareAndSet fails, another thread updated first, so retry loop
        }
    }

    public static void main(String[] args) throws InterruptedException {
        P2_FlashSaleInventoryManager manager = new P2_FlashSaleInventoryManager();
        String productId = "IPHONE15_256GB";
        
        // Setup: 100 units available
        manager.addProduct(productId, 100);

        System.out.println("Initial Stock: " + manager.getStock(productId) + " units available");

        // Simulate massive concurrent flash sale (50,000 users)
        int totalUsers = 150; // Using 150 for demo to show both success and waiting list
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        for (int i = 1; i <= totalUsers; i++) {
            final int userId = i;
            executor.submit(() -> {
                String result = manager.purchaseItem(productId, userId);
                if (userId % 20 == 0 || userId > 98) { // Sample output
                    System.out.println("User " + userId + ": " + result);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("\nFinal Stock: " + manager.getStock(productId));
    }
}
