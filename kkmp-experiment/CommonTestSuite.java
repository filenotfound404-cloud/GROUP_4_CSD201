import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared test cases for byte-pattern matching algorithms.
 * This file can run by itself. Use PatternMatchingTestAdapter to test a
 * project algorithm that implements the PatternMatching interface.
 */
public final class CommonTestSuite {
    private static final long MULTIPLIER = 1_000_003L;

    private CommonTestSuite() {
    }

    /** Small interface that keeps this file runnable on its own. */
    public interface Matcher {
        String getName();

        void reset();

        void compile(List<byte[]> patterns);

        Result search(byte[] data);
    }

    /** Count and checksum returned by one search. */
    public static final class Result {
        public final long count;
        public final long checksum;

        public Result(long count, long checksum) {
            this.count = count;
            this.checksum = checksum;
        }
    }

    /** Summary returned after testing one matcher. */
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

    /** Run fixed cases against the supplied matcher. */
    public static Summary runAll(Matcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Matcher must not be null");
        }

        System.out.println("=== Common tests: " + matcher.getName() + " ===");
        int passed = 0;
        int total = 0;

        total++;
        if (check(matcher, "overlapping", "aaaaa", new String[]{"aaa"}, 3, 6_000_018L)) {
            passed++;
        }

        total++;
        if (check(matcher, "multiple patterns", "ushers",
                new String[]{"he", "she", "his", "hers"}, 3, 8_000_028L)) {
            passed++;
        }

        total++;
        if (check(matcher, "pattern longer than text", "abc",
                new String[]{"abcd"}, 0, 0L)) {
            passed++;
        }

        total++;
        if (check(matcher, "no match", "abcdef",
                new String[]{"xyz"}, 0, 0L)) {
            passed++;
        }

        System.out.printf("Common tests: %d / %d passed%n", passed, total);
        return new Summary(passed, total);
    }

    private static boolean check(Matcher matcher, String testName, String text,
            String[] patterns, long expectedCount, long expectedChecksum) {
        List<byte[]> patternBytes = new ArrayList<>();
        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty()) {
                System.err.println("[FAIL] " + testName + ": test patterns must not be empty");
                return false;
            }
            patternBytes.add(pattern.getBytes(StandardCharsets.ISO_8859_1));
        }

        try {
            matcher.reset();
            matcher.compile(patternBytes);
            Result actual = matcher.search(text.getBytes(StandardCharsets.ISO_8859_1));

            boolean ok = actual != null && actual.count == expectedCount
                    && actual.checksum == expectedChecksum;
            if (ok) {
                System.out.printf("[PASS] %s%n", testName);
            } else {
                long count = actual == null ? -1 : actual.count;
                long checksum = actual == null ? -1 : actual.checksum;
                System.err.printf("[FAIL] %s: expected count/checksum=%d/%d, got %d/%d%n",
                        testName, expectedCount, expectedChecksum, count, checksum);
            }
            return ok;
        } catch (RuntimeException e) {
            System.err.printf("[FAIL] %s: %s%n", testName, e.toString());
            return false;
        }
    }

    /** Standalone smoke test for the suite using its built-in reference matcher. */
    public static void main(String[] args) {
        System.out.println("Standalone run uses a built-in reference matcher.");
        System.out.println("It checks the test suite, not a project algorithm.\n");
        Summary summary = runAll(new ReferenceMatcher());
        if (!summary.allPassed()) {
            throw new AssertionError("The built-in reference matcher failed.");
        }
    }

    private static final class ReferenceMatcher implements Matcher {
        private List<byte[]> patterns = Collections.emptyList();

        @Override
        public String getName() {
            return "Built-in reference matcher";
        }

        @Override
        public void reset() {
            // No search state is stored between calls.
        }

        @Override
        public void compile(List<byte[]> patterns) {
            if (patterns == null) {
                throw new IllegalArgumentException("Patterns must not be null");
            }
            for (byte[] pattern : patterns) {
                if (pattern == null || pattern.length == 0) {
                    throw new IllegalArgumentException("Patterns must not be empty");
                }
            }
            this.patterns = new ArrayList<>(patterns);
        }

        @Override
        public Result search(byte[] data) {
            if (data == null) {
                throw new IllegalArgumentException("Data must not be null");
            }
            long count = 0;
            long checksum = 0;
            for (int patternId = 0; patternId < patterns.size(); patternId++) {
                byte[] pattern = patterns.get(patternId);
                for (int start = 0; start <= data.length - pattern.length; start++) {
                    int matched = 0;
                    while (matched < pattern.length
                            && data[start + matched] == pattern[matched]) {
                        matched++;
                    }
                    if (matched == pattern.length) {
                        count++;
                        checksum += ((long) start + 1L) * MULTIPLIER + patternId;
                    }
                }
            }
            return new Result(count, checksum);
        }
    }
}
