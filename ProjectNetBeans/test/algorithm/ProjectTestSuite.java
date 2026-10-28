package algorithm;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/** Shared correctness tests that can be run against every project algorithm. */
public final class ProjectTestSuite {
    private static final long MULTIPLIER = 1_000_003L;
    private static final List<TestCase> CASES = createCases();

    @Test
    public void testAllProjectAlgorithms() {
        Summary summary = runAll(
            new algorithm.bruteforce.BruteForceMatcher(),
            new algorithm.kmp.KMPMatcher(),
            new algorithm.ahocorasick.ACFailMatcher(),
            new algorithm.ahocorasick.ACDFAMatcher()
        );
        assertTrue("Tat ca thuat toan phai pass 100%", summary.allPassed());
    }

    public ProjectTestSuite() {
    }

    /** Run the same test cases against each supplied algorithm. */
    public static Summary runAll(PatternMatching... algorithms) {
        if (algorithms == null || algorithms.length == 0) {
            throw new IllegalArgumentException("Supply at least one algorithm");
        }

        int passed = 0;
        int total = 0;
        for (PatternMatching algorithm : algorithms) {
            if (algorithm == null) {
                System.err.println("[FAIL] Null algorithm instance");
                total += CASES.size();
                continue;
            }

            String name = algorithm.getAlgorithmName();
            System.out.println("\n=== Testing " + name + " ===");
            for (TestCase testCase : CASES) {
                total++;
                if (runCase(algorithm, testCase)) {
                    passed++;
                }
            }
        }

        System.out.printf("\nProject test result: %d / %d passed%n", passed, total);
        return new Summary(passed, total);
    }

    private static boolean runCase(PatternMatching algorithm, TestCase testCase) {
        byte[][] patterns = new byte[testCase.patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = testCase.patterns[i].getBytes(StandardCharsets.ISO_8859_1);
        }

        ChecksumConsumer consumer = new ChecksumConsumer();
        try {
            algorithm.reset();
            algorithm.compilePattern(Arrays.asList(patterns));
            algorithm.searchPattern(testCase.text.getBytes(StandardCharsets.ISO_8859_1), consumer);

            long expectedCount = countMatches(testCase.positions);
            long expectedChecksum = checksum(testCase.positions);
            boolean passed = consumer.getCount() == expectedCount
                    && consumer.getChecksum() == expectedChecksum;
            if (passed) {
                System.out.println("[PASS] " + testCase.name);
            } else {
                System.err.printf("[FAIL] %s: expected count/checksum=%d/%d, got %d/%d%n",
                        testCase.name, expectedCount, expectedChecksum,
                        consumer.getCount(), consumer.getChecksum());
            }
            return passed;
        } catch (RuntimeException e) {
            System.err.printf("[FAIL] %s: %s%n", testCase.name, e.toString());
            return false;
        }
    }

    private static long countMatches(long[][] positions) {
        long count = 0;
        for (long[] patternPositions : positions) {
            count += patternPositions.length;
        }
        return count;
    }

    private static long checksum(long[][] positions) {
        long result = 0;
        for (int patternId = 0; patternId < positions.length; patternId++) {
            for (long offset : positions[patternId]) {
                result += (offset + 1L) * MULTIPLIER + patternId;
            }
        }
        return result;
    }

    private static List<TestCase> createCases() {
        List<TestCase> cases = new ArrayList<>();
        cases.add(new TestCase("overlapping matches", "aaaaa",
                new String[]{"aaa"}, new long[][]{{0, 1, 2}}));
        cases.add(new TestCase("multiple patterns", "ushers",
                new String[]{"he", "she", "his", "hers"},
                new long[][]{{2}, {1}, {}, {2}}));
        cases.add(new TestCase("pattern longer than text", "abc",
                new String[]{"abcd"}, new long[][]{{}}));
        cases.add(new TestCase("no match", "abcdef",
                new String[]{"xyz"}, new long[][]{{}}));
        cases.add(new TestCase("single-byte patterns", "banana",
                new String[]{"a", "n"}, new long[][]{{1, 3, 5}, {2, 4}}));
        cases.add(new TestCase("duplicate patterns are counted by ID", "aaaa",
                new String[]{"aa", "aa"}, new long[][]{{0, 1, 2}, {0, 1, 2}}));
        cases.add(new TestCase("high byte values", "\u00E9\u00FF\u00E9\u00FF\u00E9\u0000",
                new String[]{"\u00E9\u00FF", "\u00FF\u00E9\u0000", "\u00E9"},
                new long[][]{{0, 2}, {3}, {0, 2, 4}}));
        cases.add(new TestCase("empty text", "", new String[]{"a"}, new long[][]{{}}));
        return cases;
    }

    /** Run algorithms whose classes have public no-argument constructors. */
    public static void main(String[] classNames) {
        if (classNames == null || classNames.length == 0) {
            System.err.println("No algorithms supplied.");
            System.err.println("Pass fully qualified class names implementing PatternMatching.");
            System.err.println("Example: algorithm.ProjectTestSuite algorithm.BruteForce");
            return;
        }

        PatternMatching[] algorithms = new PatternMatching[classNames.length];
        try {
            for (int i = 0; i < classNames.length; i++) {
                Class<?> type = Class.forName(classNames[i]);
                if (!PatternMatching.class.isAssignableFrom(type)) {
                    throw new IllegalArgumentException(classNames[i] + " does not implement PatternMatching");
                }
                Constructor<?> constructor = type.getDeclaredConstructor();
                algorithms[i] = (PatternMatching) constructor.newInstance();
            }
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not load algorithm: " + e.getMessage(), e);
        }

        Summary summary = runAll(algorithms);
        if (!summary.allPassed()) {
            throw new AssertionError("Some project tests failed.");
        }
    }

    private static final class TestCase {
        final String name;
        final String text;
        final String[] patterns;
        final long[][] positions;

        TestCase(String name, String text, String[] patterns, long[][] positions) {
            this.name = name;
            this.text = text;
            this.patterns = patterns;
            this.positions = positions;
        }
    }

    public static final class Summary {
        public final int passed;
        public final int total;

        Summary(int passed, int total) {
            this.passed = passed;
            this.total = total;
        }

        public boolean allPassed() {
            return passed == total;
        }
    }
}
