import java.util.*;

/**
 * Problem 10: Multi-Level Cache System with Hash Tables
 * L1 (Memory), L2 (SSD/File), L3 (Database/Full System).
 * Goal: LRU, Popularity-based Promotion, and Performance Tracking.
 */
public class P10_MultiLevelCacheSystem {

    private static class VideoData {
        String id, title;
        VideoData(String id) { this.id = id; this.title = "Video " + id; }
    }

    // L1: LinkedHashMap with access-order (O(1) Memory LRU)
    private final Map<String, VideoData> l1Cache;
    // L2: Simulates SSD Lookup (O(1) with slight latency)
    private final Map<String, String> l2Cache = new HashMap<>(); // videoId -> SSDPath
    // Video -> Access Count (Used for promotion logic)
    private final Map<String, Integer> accessCounts = new HashMap<>();

    public P10_MultiLevelCacheSystem(int l1Size) {
        this.l1Cache = new LinkedHashMap<String, VideoData>(l1Size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > l1Size; // Simple LRU eviction
            }
        };
    }

    public VideoData getVideo(String videoId) {
        // Step 1: Check L1
        long startTime = System.nanoTime();
        if (l1Cache.containsKey(videoId)) {
            printLatency(startTime, "L1 HIT");
            return l1Cache.get(videoId);
        }

        // Step 2: Check L2
        if (l2Cache.containsKey(videoId)) {
            simulateLatency(5); // 5ms simulated latency for SSD
            printLatency(startTime, "L2 HIT");
            
            // Increment access for promotion
            int count = accessCounts.getOrDefault(videoId, 0) + 1;
            accessCounts.put(videoId, count);
            
            VideoData data = new VideoData(videoId); // Reconstruct data
            if (count > 2) {
                System.out.println("-> Promoting video to L1");
                l1Cache.put(videoId, data);
            }
            return data;
        }

        // Step 3: Check L3 (Database)
        simulateLatency(150); // 150ms simulated DB latency
        printLatency(startTime, "L3 HIT");
        
        VideoData data = new VideoData(videoId);
        l2Cache.put(videoId, "ssd/path/" + videoId); // Add to L2 initially
        accessCounts.put(videoId, 1);
        return data;
    }

    private void simulateLatency(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    private void printLatency(long startNs, String result) {
        double ms = (System.nanoTime() - startNs) / 1_000_000.0;
        System.out.printf("%s obtained in %.2fms%n", result, ms);
    }

    public static void main(String[] args) {
        P10_MultiLevelCacheSystem streaming = new P10_MultiLevelCacheSystem(5);

        System.out.println("--- First Access (DB) ---");
        streaming.getVideo("movie1");

        System.out.println("\n--- Second Access (L2) ---");
        streaming.getVideo("movie1");

        System.out.println("\n--- Third Access (Promotion to L1) ---");
        streaming.getVideo("movie1");

        System.out.println("\n--- Fast Access (L1) ---");
        streaming.getVideo("movie1");
    }
}
