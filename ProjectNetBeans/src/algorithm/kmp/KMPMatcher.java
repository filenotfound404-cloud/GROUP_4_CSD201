package algorithm.kmp;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import java.util.ArrayList;
import java.util.List;

/**
 * KMPMatcher - Giải thuật KMP (Knuth-Morris-Pratt).
 * Tuân thủ interface PatternMatching:
 * - Khi có 1 pattern: chạy KMP đơn mẫu.
 * - Khi có nhiều pattern: lặp qua từng pattern (hoặc khớp từng pattern với patternId tương ứng).
 */
public class KMPMatcher implements PatternMatching {

    private List<byte[]> patterns;
    private List<int[]> piTables;
    private int[] matchedStates;
    private long globalOffset = 0;
    private long comparisonsCount = 0;

    public KMPMatcher() {
    }

    public static int[] computePrefixTable(byte[] pattern) {
        if (pattern == null || pattern.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        int m = pattern.length;
        int[] pi = new int[m];
        pi[0] = 0;
        int j = 0;

        for (int i = 1; i < m; i++) {
            while (j > 0 && pattern[i] != pattern[j]) {
                j = pi[j - 1];
            }
            if (pattern[i] == pattern[j]) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Pattern list cannot be empty");
        }
        this.patterns = new ArrayList<>(patterns.size());
        this.piTables = new ArrayList<>(patterns.size());
        for (byte[] p : patterns) {
            if (p == null || p.length == 0) {
                throw new IllegalArgumentException("Pattern cannot be empty");
            }
            this.patterns.add(p);
            this.piTables.add(computePrefixTable(p));
        }
        reset();
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0 || patterns == null || patterns.isEmpty()) {
            return;
        }

        int numPatterns = patterns.size();
        for (int pIdx = 0; pIdx < numPatterns; pIdx++) {
            byte[] pattern = patterns.get(pIdx);
            int[] pi = piTables.get(pIdx);
            int m = pattern.length;
            int matched = matchedStates[pIdx];

            for (int i = 0; i < data.length; i++) {
                byte b = data[i];

                while (matched > 0 && b != pattern[matched]) {
                    comparisonsCount++;
                    matched = pi[matched - 1];
                }

                comparisonsCount++;
                if (b == pattern[matched]) {
                    matched++;
                }

                if (matched == m) {
                    long matchOffset = (globalOffset + i) - m + 1;
                    if (consumer != null) {
                        consumer.onMatch(matchOffset, pIdx);
                    }
                    matched = pi[m - 1];
                }
            }
            matchedStates[pIdx] = matched;
        }
        globalOffset += data.length;
    }

    @Override
    public String getAlgorithmName() {
        return "kmp";
    }

    @Override
    public void reset() {
        if (patterns != null) {
            this.matchedStates = new int[patterns.size()];
        }
        this.globalOffset = 0;
        this.comparisonsCount = 0;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }
}
