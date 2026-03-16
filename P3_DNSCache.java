import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 3: DNS Cache with TTL (Time To Live)
 * 
 * Scenario: Store domain-to-IP mappings to reduce lookup times.
 * Goal: O(1) Access, TTL-based expiration, LRU Eviction, Performance Metrics.
 */
public class P3_DNSCache {

    // Internal class to represent a DNS Entry
    private static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime; // System time (ms) when this entry expires

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // Cache parameters
    private final int capacity;
    // Map for O(1) lookup
    // Using Collections.synchronizedMap to wrap a LinkedHashMap for LRU behavior + thread safety
    private final Map<String, DNSEntry> cache;
    
    // Performance metrics
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);
    private final AtomicLong totalLookupTimeNs = new AtomicLong(0);

    public P3_DNSCache(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap with accessOrder=true enables LRU behavior
        this.cache = Collections.synchronizedMap(new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > capacity;
            }
        });

        // Background thread to clean expired entries (Optional but good practice)
        ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DNS-Cache-Cleaner");
            t.setDaemon(true);
            return t;
        });
        cleaner.scheduleAtFixedRate(this::cleanupExpired, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * Resolves a domain name to an IP.
     */
    public String resolve(String domain) {
        long startTime = System.nanoTime();
        DNSEntry entry = cache.get(domain);

        try {
            if (entry != null) {
                if (!entry.isExpired()) {
                    hits.incrementAndGet();
                    return entry.ipAddress + " (Cache HIT)";
                } else {
                    // Entry found but expired
                    cache.remove(domain);
                }
            }

            // Cache MISS or EXPIRED -> Query Upstream (Simulated)
            misses.incrementAndGet();
            String ip = queryUpstream(domain);
            
            // Add to cache with a 5-second TTL for demo purposes (usually 300+)
            cache.put(domain, new DNSEntry(domain, ip, 5));
            return ip + " (Cache MISS/Query Upstream)";

        } finally {
            totalLookupTimeNs.addAndGet(System.nanoTime() - startTime);
        }
    }

    /**
     * Simulates a slow upstream DNS query.
     */
    private String queryUpstream(String domain) {
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate latency
        return "172.217." + (int)(Math.random() * 255) + "." + (int)(Math.random() * 255);
    }

    private void cleanupExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void getCacheStats() {
        long h = hits.get();
        long m = misses.get();
        double ratio = (h + m == 0) ? 0 : (double) h / (h + m) * 100;
        double avgTime = (h + m == 0) ? 0 : (double) totalLookupTimeNs.get() / (h + m) / 1_000_000.0;
        
        System.out.println(String.format("Stats -> Hit Rate: %.1f%%, Avg Lookup Time: %.2fms, Cache Size: %d", 
            ratio, avgTime, cache.size()));
    }

    public static void main(String[] args) throws InterruptedException {
        P3_DNSCache dns = new P3_DNSCache(100);

        System.out.println("First lookup (Google): " + dns.resolve("google.com"));
        System.out.println("Second lookup (Google): " + dns.resolve("google.com"));
        System.out.println("Lookup (GitHub): " + dns.resolve("github.com"));

        dns.getCacheStats();

        System.out.println("\nWaiting for TTL to expire (6 seconds)...");
        Thread.sleep(6000);

        System.out.println("Expired lookup (Google): " + dns.resolve("google.com"));
        dns.getCacheStats();
    }
}
