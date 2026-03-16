import java.util.*;
import java.util.concurrent.*;

/**
 * Problem 5: Real-Time Analytics Dashboard for Website Traffic
 * Maintains top 10 pages, unique visitors, and traffic sources with Zero Lag.
 */
public class P5_TrafficAnalyticsDashboard {

    // pageUrl -> visitCount
    private final Map<String, LongAdder> pageViews = new ConcurrentHashMap<>();
    // pageUrl -> Set of Unique UserIDs
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    // trafficSource -> visitCount
    private final Map<String, LongAdder> trafficSources = new ConcurrentHashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.computeIfAbsent(url, k -> new LongAdder()).increment();
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);
        trafficSources.computeIfAbsent(source, k -> new LongAdder()).increment();
    }

    public void getDashboard() {
        System.out.println("\n--- Real-Time Analytics Dashboard ---");
        
        // Calculate Top 10 Pages
        System.out.println("Top Pages:");
        pageViews.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue().sum(), e1.getValue().sum()))
            .limit(10)
            .forEach(entry -> {
                String url = entry.getKey();
                long views = entry.getValue().sum();
                int uniqueCount = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
                System.out.printf("1. %s - %d views (%d unique)%n", url, views, uniqueCount);
            });

        // Calculate Traffic Sources
        long totalTraffic = trafficSources.values().stream().mapToLong(LongAdder::sum).sum();
        System.out.println("\nTraffic Sources:");
        trafficSources.forEach((source, count) -> {
            long c = count.sum();
            double percent = (double) c / totalTraffic * 100;
            System.out.printf("%s: %.1f%%, ", source, percent);
        });
        System.out.println();
    }

    // LongAdder for high throughput updates (no contention)
    private static class LongAdder extends java.util.concurrent.atomic.LongAdder {}

    public static void main(String[] args) throws InterruptedException {
        P5_TrafficAnalyticsDashboard dashboard = new P5_TrafficAnalyticsDashboard();

        // Simulate high traffic
        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/article/breaking-news", "user_123", "google"); // Duplicate user
        dashboard.processEvent("/sports/championship", "user_999", "direct");

        dashboard.getDashboard();
        
        // Wait and simulate more events (batch behavior)
        Thread.sleep(2000);
        dashboard.processEvent("/article/breaking-news", "user_789", "google");
        dashboard.getDashboard();
    }
}
