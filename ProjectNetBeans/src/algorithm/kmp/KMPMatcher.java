package algorithm.kmp;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import java.util.List;

/**
 * KMPMatcher - Giải thuật KMP (Knuth-Morris-Pratt) khớp mẫu đơn đọc theo block.
 * Lấy từ module của Cặp 2 (kkmp-experiment/KmpBlockMatcher.java).
 */
public class KMPMatcher implements PatternMatching {

    private byte[] pattern;
    private int[] pi;
    private int matched = 0;
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
        byte[] p = patterns.get(0);
        if (p == null || p.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }
        this.pattern = p;
        this.pi = computePrefixTable(p);
        reset();
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0 || pattern == null || pi == null) {
            return;
        }

        int m = pattern.length;
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
                    consumer.onMatch(0, matchOffset);
                }
                matched = pi[m - 1];
            }
        }
        globalOffset += data.length;
    }

    @Override
    public String getAlgorithmName() {
        return "kmp";
    }

    @Override
    public void reset() {
        this.matched = 0;
        this.globalOffset = 0;
        this.comparisonsCount = 0;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }
}
