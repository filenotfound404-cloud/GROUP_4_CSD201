package algorithm;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Bộ kiểm tra dùng chung cho mọi thuật toán triển khai {@link PatternMatching}.
 *
 * Gọi {@link #runAll(PatternMatching)} từ main của từng thuật toán hoặc từ một
 * runner chung. Interface hiện tại nhận toàn bộ dữ liệu trong một mảng byte,
 * nên bộ kiểm tra này không kiểm tra việc đọc theo block.
 */
public final class CommonTestSuite {
    private static final long MULTIPLIER = 1_000_003L;

    private CommonTestSuite() {
    }

    /** Kết quả tổng hợp sau khi chạy bộ kiểm tra cho một thuật toán. */
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

    /** Chạy các ca cố định với một implementation của PatternMatching. */
    public static Summary runAll(PatternMatching algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm không được null");
        }

        System.out.println("=== Common tests: " + algorithm.getAlgorithmName() + " ===");
        int passed = 0;
        int total = 0;

        // Chồng lấn: "aaa" xuất hiện tại offset 0, 1, 2 trong "aaaaa".
        total++;
        if (check(algorithm, "overlapping", "aaaaa", new String[]{"aaa"}, 3, 6_000_018L)) {
            passed++;
        }

        // Nhiều mẫu: kiểm tra offset và patternId trong checksum.
        total++;
        if (check(algorithm, "multiple patterns", "ushers",
                new String[]{"he", "she", "his", "hers"}, 3, 8_000_028L)) {
            passed++;
        }

        // Mẫu dài hơn văn bản: không được có kết quả.
        total++;
        if (check(algorithm, "pattern longer than text", "abc",
                new String[]{"abcd"}, 0, 0L)) {
            passed++;
        }

        // Không khớp.
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
                System.err.println("[FAIL] " + testName + ": test pattern must not be empty");
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
}
