import java.util.*;

public class UsernameChecker {
    // O(1) lookup: maps username to userId
    private final Map<String, Integer> usernames = new HashMap<>();
    // O(1) lookup: maps username to number of check attempts
    private final Map<String, Integer> attemptFrequency = new HashMap<>();
    private int nextUserId = 1;

    /**
     * Helper to populate the system with existing users.
     */
    public boolean register(String username) {
        if (usernames.containsKey(username)) {
            return false;
        }
        usernames.put(username, nextUserId++);
        return true;
    }

    /**
     * Checks if a username exists in O(1) time.
     * Tracks popularity of attempted usernames.
     */
    public boolean checkAvailability(String username) {
        // Increment attempt frequency (Frequency counting)
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        
        // O(1) lookup in Hash Table
        return !usernames.containsKey(username);
    }

    /**
     * Suggests similar available usernames if the requested one is taken.
     * Appends numbers or modifies characters.
     */
    public List<String> suggestAlternatives(String username, int count) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;

        // Strategy 1: Replace underscore with dot or vice-versa (Higher priority simple variation)
        if (username.contains("_")) {
            String variant = username.replace("_", ".");
            if (!usernames.containsKey(variant)) {
                suggestions.add(variant);
            }
        } else if (username.contains(".")) {
            String variant = username.replace(".", "_");
            if (!usernames.containsKey(variant)) {
                suggestions.add(variant);
            }
        }

        // Strategy 2: Append numbers
        while (suggestions.size() < count) {
            String potential = username + suffix;
            if (!usernames.containsKey(potential) && !suggestions.contains(potential)) {
                suggestions.add(potential);
            }
            suffix++;
        }

        return suggestions.subList(0, Math.min(count, suggestions.size()));
    }

    /**
     * Returns the most searched username using frequency tracking.
     */
    public String getMostAttempted() {
        if (attemptFrequency.isEmpty()) {
            return null;
        }
        return attemptFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public int getAttemptCount(String username) {
        return attemptFrequency.getOrDefault(username, 0);
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        // 1. Pre-populate some users
        checker.register("john_doe");
        checker.register("admin");

        // 2. Check availability (O(1))
        System.out.println("Is 'john_doe' available? " + checker.checkAvailability("john_doe")); // false
        System.out.println("Is 'jane_smith' available? " + checker.checkAvailability("jane_smith")); // true

        // 3. Suggest alternatives
        System.out.println("Suggestions for 'john_doe': " + checker.suggestAlternatives("john_doe", 3));

        // 4. Track popularity
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        String top = checker.getMostAttempted();
        System.out.println("Most attempted: " + top + " (" + checker.getAttemptCount(top) + " attempts)");
    }
}
