package algorithm;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Shared tests for algorithms that implement {@link PatternMatching}.
 *
 * Call {@link #runAll(PatternMatching)} from an algorithm runner. The current
 * interface receives the complete input as a byte array, so these tests do not
 * check block-boundary behavior.
 */
public final class CommonTestSuite {
    private static final long MULTIPLIER = 1_000_003L;

    private CommonTestSuite() {
    }

    /** Summary returned after testing one algorithm. */
    public static final class Summary {
        public final int passed;
        public final int total;

        private Summary(int passed, int total) {
            this.passed = passed;
            this.total = total;
        }

        public boolean allPassed() {
            return passed == total;
        }
    }

    /** Run the fixed test cases against one PatternMatching implementation. */
    public static Summary runAll(PatternMatching algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm không được null");
        }

        System.out.println("=== Common tests: " + algorithm.getAlgorithmName() + " ===");
        int passed = 0;
        int total = 0;

        // Overlapping matches: "aaa" starts at offsets 0, 1, and 2 in "aaaaa".
        total++;
        if (check(algorithm, "overlapping", "aaaaa", new String[]{"aaa"}, 3, 6_000_018L)) {
            passed++;
        }

        // Multiple patterns: verify offsets and pattern IDs in the checksum.
        total++;
        if (check(algorithm, "multiple patterns", "ushers",
                new String[]{"he", "she", "his", "hers"}, 3, 8_000_028L)) {
            passed++;
        }

        // A pattern longer than the input must not match.
        total++;
        if (check(algorithm, "pattern longer than text", "abc",
                new String[]{"abcd"}, 0, 0L)) {
            passed++;
        }

        // No match.
        total++;
        if (check(algorithm, "no match", "abcdef",
                new String[]{"xyz"}, 0, 0L)) {
            passed++;
        }

        System.out.printf("Common tests: %d / %d passed%n", passed, total);
        return new Summary(passed, total);
    }

    private static boolean check(PatternMatching algorithm, String testName,
            String text, String[] patterns, long expectedCount, long expectedChecksum) {
        byte[][] patternBytes = new byte[patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i] == null || patterns[i].isEmpty()) {
                System.err.println("[FAIL] " + testName + ": a test pattern must not be empty");
                return false;
            }
            patternBytes[i] = patterns[i].getBytes(StandardCharsets.ISO_8859_1);
        }

        ChecksumConsumer consumer = new ChecksumConsumer();
        try {
            algorithm.reset();
            algorithm.compilePattern(Arrays.asList(patternBytes));
            algorithm.searchPattern(text.getBytes(StandardCharsets.ISO_8859_1), consumer);

            boolean ok = consumer.getCount() == expectedCount
                    && consumer.getChecksum() == expectedChecksum;
            if (ok) {
                System.out.printf("[PASS] %s%n", testName);
            } else {
                System.err.printf("[FAIL] %s: expected count/checksum=%d/%d, got %d/%d%n",
                        testName, expectedCount, expectedChecksum,
                        consumer.getCount(), consumer.getChecksum());
            }
            return ok;
        } catch (RuntimeException e) {
            System.err.printf("[FAIL] %s: %s%n", testName, e.toString());
            return false;
        }
    }

    /**
     * Standalone entry point. This runs the suite against a small reference
     * matcher so the file can be launched by itself. It does not test the
     * project's KMP or other production algorithms.
     */
    public static void main(String[] args) {
        System.out.println("Running CommonTestSuite with the built-in reference matcher.");
        System.out.println("This run checks the test harness, not a project algorithm.\n");
        Summary summary = runAll(new ReferenceMatcher());
        if (!summary.allPassed()) {
            throw new AssertionError("Reference matcher failed the common test suite.");
        }
    }

    /** Simple overlapping multi-pattern matcher used only by standalone main. */
    private static final class ReferenceMatcher implements PatternMatching {
        private List<byte[]> patterns = java.util.Collections.emptyList();

        @Override
        public void compilePattern(List<byte[]> patterns) {
            if (patterns == null) {
                throw new IllegalArgumentException("Patterns must not be null");
            }
            for (byte[] pattern : patterns) {
                if (pattern == null || pattern.length == 0) {
                    throw new IllegalArgumentException("Patterns must not be empty");
                }
            }
            this.patterns = new java.util.ArrayList<>(patterns);
        }

        @Override
        public void searchPattern(byte[] data, ChecksumConsumer consumer) {
            if (data == null || consumer == null) {
                throw new IllegalArgumentException("Data and consumer must not be null");
            }
            for (int patternId = 0; patternId < patterns.size(); patternId++) {
                byte[] pattern = patterns.get(patternId);
                for (int start = 0; start <= data.length - pattern.length; start++) {
                    int matched = 0;
                    while (matched < pattern.length
                            && data[start + matched] == pattern[matched]) {
                        matched++;
                    }
                    if (matched == pattern.length) {
                        consumer.onMatch(start, patternId);
                    }
                }
            }
        }

        @Override
        public String getAlgorithmName() {
            return "Built-in reference matcher (standalone check only)";
        }

        @Override
        public void reset() {
            // The reference matcher has no search state to clear.
        }
    }
}
