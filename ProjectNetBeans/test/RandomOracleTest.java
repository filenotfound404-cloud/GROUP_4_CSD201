package test;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import algorithm.kmp.KKMPMatcher;
import algorithm.kmp.KMPMatcher;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * RandomOracleTest - Kiểm thử ngẫu nhiên 10,000 ca đối chiếu với Oracle (String.indexOf).
 * Đáp ứng Đầu việc 4.3 trong PHAN_CONG.md.
 */
public class RandomOracleTest {

    private static final int NUM_TESTS = 10000;
    private static final char[] ALPHABET = {'a', 'b'}; // sigma = 2

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       BỘ KIỂM THỬ 10.000 CA NGẪU NHIÊN ĐỐI CHIẾU ORACLE (STRING.INDEXOF)       ");
        System.out.println("================================================================================");

        testMatcher(new KMPMatcher());
        testMatcher(new KKMPMatcher());

        System.out.println("\n================================================================================");
        System.out.println(">>> TRẠNG THÁI: KIỂM THỬ ORACLE HOÀN TẤT THÀNH CÔNG! <<<");
        System.out.println("================================================================================");
    }

    private static void testMatcher(PatternMatching matcher) {
        System.out.println("\n--- Đang kiểm thử: " + matcher.getAlgorithmName() + " với " + NUM_TESTS + " ca ---");
        Random rng = new Random(42);
        int passed = 0;

        for (int t = 0; t < NUM_TESTS; t++) {
            int textLen = rng.nextInt(201); // 0 đến 200
            int patLen = 1 + rng.nextInt(8); // 1 đến 8

            String text = randomString(rng, textLen);
            String pattern = randomString(rng, patLen);

            // Oracle tính toán các vị trí xuất hiện (bao gồm overlapping)
            List<Long> oracleOffsets = new ArrayList<>();
            int fromIdx = 0;
            while (fromIdx <= text.length() - patLen) {
                int found = text.indexOf(pattern, fromIdx);
                if (found == -1) {
                    break;
                }
                oracleOffsets.add((long) found);
                fromIdx = found + 1; // tìm kiếm chồng lấn (overlapping)
            }

            long expectedCount = oracleOffsets.size();
            long expectedChecksum = 0;
            for (long off : oracleOffsets) {
                expectedChecksum += (off + 1L) * 1000003L;
            }

            matcher.reset();
            matcher.compilePattern(Arrays.asList(pattern.getBytes(StandardCharsets.ISO_8859_1)));
            ChecksumConsumer consumer = new ChecksumConsumer();
            matcher.searchPattern(text.getBytes(StandardCharsets.ISO_8859_1), consumer);

            if (consumer.getCount() == expectedCount && consumer.getChecksum() == expectedChecksum) {
                passed++;
            } else {
                System.err.printf("[FAIL] Ca #%d: text='%s', pattern='%s' -> Kỳ vọng: count=%d, checksum=%d | Nhận: count=%d, checksum=%d\n",
                        t, text, pattern, expectedCount, expectedChecksum, consumer.getCount(), consumer.getChecksum());
                break;
            }
        }

        System.out.printf("Kết quả %s: %d / %d ca đạt chuẩn đối chiếu Oracle (%.2f%%)\n",
                matcher.getAlgorithmName(), passed, NUM_TESTS, (100.0 * passed / NUM_TESTS));
        if (passed != NUM_TESTS) {
            throw new AssertionError("Kiểm thử Oracle thất bại!");
        }
    }

    private static String randomString(Random rng, int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET[rng.nextInt(ALPHABET.length)];
        }
        return new String(chars);
    }
}
