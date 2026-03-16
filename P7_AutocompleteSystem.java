import java.util.*;

/**
 * Problem 7: Autocomplete System for Search Engine
 * Trie + HashMap for top 10 results based on frequency.
 */
public class P7_AutocompleteSystem {

    private final Map<String, Integer> queryFrequency = new HashMap<>();
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        // In-memory list of top suggestions for this prefix
        PriorityQueue<Pair> topSuggestions = new PriorityQueue<>(Comparator.comparingInt(p -> p.freq));
    }

    private static class Pair {
        String query;
        int freq;
        Pair(String q, int f) { query = q; freq = f; }
    }

    public List<String> search(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) return Collections.emptyList();
            current = current.children.get(ch);
        }

        // Return top results for this prefix
        List<String> results = new ArrayList<>();
        List<Pair> list = new ArrayList<>(current.topSuggestions);
        list.sort((p1, p2) -> Integer.compare(p2.freq, p1.freq));
        for (Pair p : list) results.add(p.query);
        return results;
    }

    public void updateFrequency(String query) {
        int freq = queryFrequency.getOrDefault(query, 0) + 1;
        queryFrequency.put(query, freq);
        
        // Update Trie with this query
        TrieNode current = root;
        for (char ch : query.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
            updateTopSuggestions(current, query, freq);
        }
    }

    private void updateTopSuggestions(TrieNode node, String query, int freq) {
        node.topSuggestions.removeIf(p -> p.query.equals(query));
        node.topSuggestions.add(new Pair(query, freq));
        if (node.topSuggestions.size() > 10) {
            node.topSuggestions.poll(); // Keep only top 10
        }
    }

    public static void main(String[] args) {
        P7_AutocompleteSystem searchEngine = new P7_AutocompleteSystem();
        
        searchEngine.updateFrequency("java tutorial");
        searchEngine.updateFrequency("java tutorial");
        searchEngine.updateFrequency("javascript");
        searchEngine.updateFrequency("java download");

        System.out.println("Autocomplete 'jav' -> " + searchEngine.search("jav"));
        
        searchEngine.updateFrequency("java 21 features");
        System.out.println("Autocomplete 'java' after sync -> " + searchEngine.search("java"));
    }
}
