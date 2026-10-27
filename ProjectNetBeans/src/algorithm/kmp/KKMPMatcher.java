package algorithm.kmp;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import java.util.ArrayList;
import java.util.List;

/**
 * KKMPMatcher - Giải thuật k-KMP (Multi-Pattern KMP) quét stream 1 lượt duy nhất.
 * Lấy từ module của Cặp 2 (kkmp-experiment/KkmpBlockMatcher.java).
 */
public class KKMPMatcher implements PatternMatching {

    public static class PatternState {
        public final int id;
        public final byte[] pattern;
        public final int[] pi;
        public int matched;

        public PatternState(byte[] pattern, int id) {
            if (pattern == null || pattern.length == 0) {
                throw new IllegalArgumentException("Pattern cannot be empty");
            }
            this.id = id;
            this.pattern = pattern;
            this.pi = KMPMatcher.computePrefixTable(pattern);
            this.matched = 0;
        }
    }

    private List<PatternState> states = new ArrayList<>();
    private long globalOffset = 0;
    private long comparisonsCount = 0;

    public KKMPMatcher() {
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Pattern list cannot be empty");
        }
        states.clear();
        for (int i = 0; i < patterns.size(); i++) {
            byte[] p = patterns.get(i);
            if (p == null || p.length == 0) {
                throw new IllegalArgumentException("Pattern cannot be empty");
            }
            states.add(new PatternState(p, i));
        }
        reset();
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0 || states.isEmpty()) {
            return;
        }

        int k = states.size();
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];

            for (int sIdx = 0; sIdx < k; sIdx++) {
                PatternState state = states.get(sIdx);
                int m = state.pattern.length;

                while (state.matched > 0 && b != state.pattern[state.matched]) {
                    comparisonsCount++;
                    state.matched = state.pi[state.matched - 1];
                }

                comparisonsCount++;
                if (b == state.pattern[state.matched]) {
                    state.matched++;
                }

                if (state.matched == m) {
                    long matchOffset = (globalOffset + i) - m + 1;
                    if (consumer != null) {
                        consumer.onMatch(state.id, matchOffset);
                    }
                    state.matched = state.pi[m - 1];
                }
            }
        }
        globalOffset += data.length;
    }

    @Override
    public String getAlgorithmName() {
        return "kmp-k";
    }

    @Override
    public void reset() {
        for (PatternState state : states) {
            state.matched = 0;
        }
        this.globalOffset = 0;
        this.comparisonsCount = 0;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }
}
