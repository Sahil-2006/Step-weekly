import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem 6: Distributed Rate Limiter for API Gateway
 * Token Bucket implementation for 100,000 clients.
 */
public class P6_DistributedRateLimiter {

    // Simple Token Bucket representation per client
    private static class TokenBucket {
        final long maxTokens;
        final long refillRate; // Tokens per second
        final AtomicLong tokens;
        final AtomicLong lastRefillTime;

        TokenBucket(long limit, long refillRate) {
            this.maxTokens = limit;
            this.refillRate = refillRate;
            this.tokens = new AtomicLong(limit);
            this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
        }

        boolean tryConsume() {
            refill();
            long current = tokens.get();
            while (current > 0) {
                if (tokens.compareAndSet(current, current - 1)) {
                    return true;
                }
                current = tokens.get();
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long last = lastRefillTime.get();
            long elapsedSeconds = (now - last) / 1000;
            
            if (elapsedSeconds > 0) {
                if (lastRefillTime.compareAndSet(last, now)) {
                    long newTokens = Math.min(maxTokens, tokens.get() + (elapsedSeconds * refillRate));
                    tokens.set(newTokens);
                }
            }
        }
    }

    private final Map<String, TokenBucket> clientLimits = new ConcurrentHashMap<>();

    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = clientLimits.computeIfAbsent(clientId, 
            k -> new TokenBucket(1000, 10)); // 1000 burst capacity, 10 tokens/sec refill
        return bucket.tryConsume();
    }

    public static void main(String[] args) throws InterruptedException {
        P6_DistributedRateLimiter limiter = new P6_DistributedRateLimiter();
        String clientId = "abc123";

        // Test Burst Traffic
        for (int i = 0; i < 5; i++) {
            System.out.println("Consume 1: " + (limiter.checkRateLimit(clientId) ? "Allowed" : "Denied"));
        }

        // Test Denied
        limiter.clientLimits.put(clientId, new TokenBucket(1, 1)); // Strict limit for test
        System.out.println("Check 1: " + (limiter.checkRateLimit(clientId) ? "Allowed" : "Denied"));
        System.out.println("Check 2 (Denied): " + (limiter.checkRateLimit(clientId) ? "Allowed" : "Denied"));
        
        System.out.println("\nWaiting 2s for refill...");
        Thread.sleep(2000);
        System.out.println("Check 3 after refill (Allowed): " + (limiter.checkRateLimit(clientId) ? "Allowed" : "Denied"));
    }
}
