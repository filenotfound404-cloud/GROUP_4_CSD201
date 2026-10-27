package test;

import algorithm.ChecksumConsumer;
import algorithm.ahocorasick.ACDFAMatcher;
import algorithm.ahocorasick.ACFailMatcher;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;

/**
 * Bộ kiểm thử tính đúng Aho-Corasick (AhoCorasickTest.java) bằng JUnit 4
 * 1. Test ca "ushers" với {"he", "she", "his", "hers"} thẩm định Dictionary Suffix Link (outputLink)
 * 2. Equivalence Test: ACFailMatcher vs ACDFAMatcher trên k in {1, 10, 20} mẫu ngẫu nhiên (100% khớp)
 * 3. Kiểm tra duy trì trạng thái Automaton qua ranh giới block cực nhỏ B in {1, 2, 3, 7} bytes
 */
public class AhoCorasickTest {

    /**
     * Kích hoạt JUnit 4 Runner:
     * Cho phép bấm "Run File" (Shift + F6) trực tiếp trong NetBeans
     * mà vẫn thực thi 100% theo chuẩn JUnit Test Runner.
     */
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("     AHO-CORASICK JUNIT 4 TEST SUITE (AhoCorasickTest)");
        System.out.println("==================================================================\n");

        Result result = JUnitCore.runClasses(AhoCorasickTest.class);

        for (Failure failure : result.getFailures()) {
            System.err.println("  [LỖI] " + failure.toString());
        }

        if (result.wasSuccessful()) {
            System.out.printf("%n>>> [100%% PASS] TẤT CẢ %d BÀI TEST JUNIT ĐÃ ĐẠT THÀNH CÔNG! (Thời gian: %.3fs) <<<%n",
                    result.getRunCount(), result.getRunTime() / 1000.0);
        } else {
            System.err.printf("%n>>> [FAIL] Có %d bài test thất bại! <<<%n", result.getFailureCount());
            System.exit(1);
        }
    }

    /**
     * Yêu cầu 1: Kiểm tra ca "ushers" thẩm định Dictionary Suffix Link.
     * Mẫu: {"he", "she", "his", "hers"}
     * Văn bản: "ushers"
     * Kỳ vọng:
     *   - "she" tại offset 1 (id 1)
     *   - "he" tại offset 2 (id 0) -> được tìm thấy qua Output Link khi đang ở trạng thái kết thúc "she"
     *   - "hers" tại offset 2 (id 3)
     */
    @Test
    public void testUshers() {
        ACFailMatcher failMatcher = new ACFailMatcher();
        ACDFAMatcher dfaMatcher = new ACDFAMatcher();

        List<byte[]> patterns = Arrays.asList(
            "he".getBytes(StandardCharsets.ISO_8859_1),    // pid 0
            "she".getBytes(StandardCharsets.ISO_8859_1),   // pid 1
            "his".getBytes(StandardCharsets.ISO_8859_1),   // pid 2
            "hers".getBytes(StandardCharsets.ISO_8859_1)   // pid 3
        );
        byte[] text = "ushers".getBytes(StandardCharsets.ISO_8859_1);

        failMatcher.compilePattern(patterns);
        dfaMatcher.compilePattern(patterns);

        RecordingConsumer cFail = new RecordingConsumer();
        failMatcher.searchPattern(text, cFail);

        RecordingConsumer cDfa = new RecordingConsumer();
        dfaMatcher.searchPattern(text, cDfa);

        // Kiểm tra ACFailMatcher
        assertEquals("ACFail: Tổng số match phải là 3", 3, cFail.getCount());
        assertTrue("ACFail: Phải tìm thấy 'she' tại offset 1", cFail.hasMatch(1, 1));
        assertTrue("ACFail: Phải tìm thấy 'he' tại offset 2 qua outputLink", cFail.hasMatch(2, 0));
        assertTrue("ACFail: Phải tìm thấy 'hers' tại offset 2", cFail.hasMatch(2, 3));

        // Kiểm tra ACDFAMatcher
        assertEquals("ACDFA: Tổng số match phải là 3", 3, cDfa.getCount());
        assertTrue("ACDFA: Phải tìm thấy 'she' tại offset 1", cDfa.hasMatch(1, 1));
        assertTrue("ACDFA: Phải tìm thấy 'he' tại offset 2 qua outputLink", cDfa.hasMatch(2, 0));
        assertTrue("ACDFA: Phải tìm thấy 'hers' tại offset 2", cDfa.hasMatch(2, 3));
    }

    /**
     * Yêu cầu 2: Equivalence Test giữa 2 biến thể: Chạy đồng thời ACFailMatcher và ACDFAMatcher
     * trên tập k in {1, 10, 20} mẫu, đối chiếu count:check phải trùng khớp 100%.
     */
    @Test
    public void testEquivalence() {
        int[] kValues = {1, 10, 20};
        Random rng = new Random(2026);

        for (int k : kValues) {
            for (int run = 0; run < 50; run++) {
                List<byte[]> patterns = new ArrayList<>();
                for (int p = 0; p < k; p++) {
                    int patLen = 1 + rng.nextInt(6);
                    byte[] pat = new byte[patLen];
                    for (int b = 0; b < patLen; b++) {
                        pat[b] = (byte) ('a' + rng.nextInt(4)); // Bảng chữ cái nhỏ 4 ký tự tăng tỷ lệ match
                    }
                    patterns.add(pat);
                }

                int textLen = 100 + rng.nextInt(300);
                byte[] text = new byte[textLen];
                for (int b = 0; b < textLen; b++) {
                    text[b] = (byte) ('a' + rng.nextInt(4));
                }

                ACFailMatcher failMatcher = new ACFailMatcher();
                failMatcher.compilePattern(patterns);
                ChecksumConsumer cFail = new ChecksumConsumer();
                failMatcher.searchPattern(text, cFail);

                ACDFAMatcher dfaMatcher = new ACDFAMatcher();
                dfaMatcher.compilePattern(patterns);
                ChecksumConsumer cDfa = new ChecksumConsumer();
                dfaMatcher.searchPattern(text, cDfa);

                assertEquals(String.format("Mismatch count tại k=%d, run=%d", k, run),
                        cFail.getCount(), cDfa.getCount());
                assertEquals(String.format("Mismatch checksum tại k=%d, run=%d", k, run),
                        cFail.getChecksum(), cDfa.getChecksum());
            }
        }
    }

    /**
     * Yêu cầu 3: Kiểm tra duy trì trạng thái Automaton không bị reset khi chuyển tiếp
     * qua ranh giới các block kích thước cực nhỏ B in {1, 2, 3, 7} bytes so với quét toàn bộ một lần.
     */
    @Test
    public void testBlockBoundary() {
        int[] blockSizes = {1, 2, 3, 7};
        byte[] text = "2026-10-24 14:00:01 ERROR [Database] Connection timeout pool-1-thread-42 retry 3/5 ushers warning".getBytes(StandardCharsets.ISO_8859_1);

        List<byte[]> patterns = Arrays.asList(
            "ERROR".getBytes(StandardCharsets.ISO_8859_1),
            "timeout".getBytes(StandardCharsets.ISO_8859_1),
            "Database".getBytes(StandardCharsets.ISO_8859_1),
            "ushers".getBytes(StandardCharsets.ISO_8859_1),
            "he".getBytes(StandardCharsets.ISO_8859_1),
            "she".getBytes(StandardCharsets.ISO_8859_1)
        );

        // 1. Quét mốc chuẩn (Baseline toàn bộ văn bản một lần)
        ACFailMatcher baselineFail = new ACFailMatcher();
        baselineFail.compilePattern(patterns);
        ChecksumConsumer baseFailConsumer = new ChecksumConsumer();
        baselineFail.searchPattern(text, baseFailConsumer);

        ACDFAMatcher baselineDfa = new ACDFAMatcher();
        baselineDfa.compilePattern(patterns);
        ChecksumConsumer baseDfaConsumer = new ChecksumConsumer();
        baselineDfa.searchPattern(text, baseDfaConsumer);

        long expCount = baseFailConsumer.getCount();
        long expCheck = baseFailConsumer.getChecksum();

        for (int B : blockSizes) {
            // Kiểm tra ACFailMatcher khi đọc từng block B byte
            ACFailMatcher chunkedFail = new ACFailMatcher();
            chunkedFail.compilePattern(patterns);
            chunkedFail.reset();
            ChecksumConsumer cFail = new ChecksumConsumer();

            long offset = 0;
            while (offset < text.length) {
                int len = Math.min(B, text.length - (int) offset);
                byte[] chunk = new byte[len];
                System.arraycopy(text, (int) offset, chunk, 0, len);
                chunkedFail.searchBlock(chunk, offset, cFail);
                offset += len;
            }

            // Kiểm tra ACDFAMatcher khi đọc từng block B byte
            ACDFAMatcher chunkedDfa = new ACDFAMatcher();
            chunkedDfa.compilePattern(patterns);
            chunkedDfa.reset();
            ChecksumConsumer cDfa = new ChecksumConsumer();

            offset = 0;
            while (offset < text.length) {
                int len = Math.min(B, text.length - (int) offset);
                byte[] chunk = new byte[len];
                System.arraycopy(text, (int) offset, chunk, 0, len);
                chunkedDfa.searchBlock(chunk, offset, cDfa);
                offset += len;
            }

            assertEquals(String.format("ACFail count không khớp baseline tại B=%d", B),
                    expCount, cFail.getCount());
            assertEquals(String.format("ACFail checksum không khớp baseline tại B=%d", B),
                    expCheck, cFail.getChecksum());

            assertEquals(String.format("ACDFA count không khớp baseline tại B=%d", B),
                    expCount, cDfa.getCount());
            assertEquals(String.format("ACDFA checksum không khớp baseline tại B=%d", B),
                    expCheck, cDfa.getChecksum());
        }
    }

    private static class RecordingConsumer extends ChecksumConsumer {
        static class Match {
            final long offset;
            final int patternId;

            Match(long offset, int patternId) {
                this.offset = offset;
                this.patternId = patternId;
            }
        }

        final List<Match> matches = new ArrayList<>();

        @Override
        public void onMatch(long offSet, int patternId) {
            super.onMatch(offSet, patternId);
            matches.add(new Match(offSet, patternId));
        }

        boolean hasMatch(long offset, int patternId) {
            for (Match m : matches) {
                if (m.offset == offset && m.patternId == patternId) {
                    return true;
                }
            }
            return false;
        }
    }
}
