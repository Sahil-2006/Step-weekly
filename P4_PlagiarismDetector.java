import java.util.*;

/**
 * Problem 4: Plagiarism Detection System
 * Uses N-grams and Hash Tables to detect similarity between documents.
 */
public class P4_PlagiarismDetector {

    // N-gram length (e.g., 5 words)
    private static final int N = 5;
    
    // Hash Table: N-gram -> Set of Document IDs that contain it
    private final Map<String, Set<String>> ngramIndex = new HashMap<>();
    // Store original document sizes for similarity calculation
    private final Map<String, Integer> docNgramCounts = new HashMap<>();

    public void addDocument(String docId, String content) {
        List<String> ngrams = extractNgrams(content);
        docNgramCounts.put(docId, ngrams.size());
        
        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
        System.out.println("Analyzed " + docId + ": Extracted " + ngrams.size() + " n-grams");
    }

    public void analyzePlagiarism(String docId, String content) {
        List<String> ngrams = extractNgrams(content);
        int totalNgrams = ngrams.size();
        
        // Count matches for each document in the database
        Map<String, Integer> matchCounts = new HashMap<>();
        for (String ngram : ngrams) {
            Set<String> matches = ngramIndex.get(ngram);
            if (matches != null) {
                for (String matchDocId : matches) {
                    matchCounts.put(matchDocId, matchCounts.getOrDefault(matchDocId, 0) + 1);
                }
            }
        }

        System.out.println("\nResults for " + docId + ":");
        matchCounts.forEach((otherDocId, count) -> {
            double similarity = (double) count / totalNgrams * 100;
            String status = similarity > 50 ? "PLAGIARISM DETECTED" : (similarity > 10 ? "suspicious" : "common patterns");
            System.out.println(String.format("-> Found %d matching n-grams with %s", count, otherDocId));
            System.out.println(String.format("-> Similarity: %.1f%% (%s)", similarity, status));
        });
    }

    private List<String> extractNgrams(String content) {
        String[] words = content.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            ngrams.add(sb.toString().trim());
        }
        return ngrams;
    }

    public static void main(String[] args) {
        P4_PlagiarismDetector detector = new P4_PlagiarismDetector();
        
        // Database documents
        detector.addDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog repeatedly in the forest.");
        detector.addDocument("essay_092.txt", "Climate change is a significant global challenge that requires immediate action from all nations to reduce carbon emissions.");

        // New submission
        String suspiciousContent = "Climate change is a significant global challenge that requires immediate action to reduce emissions.";
        detector.analyzePlagiarism("submission_1.txt", suspiciousContent);
    }
}
