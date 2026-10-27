package test;

import algorithm.ChecksumConsumer;
import algorithm.ahocorasick.ACDFAMatcher;
import algorithm.ahocorasick.ACFailMatcher;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Bộ kiểm thử tính đúng Aho-Corasick (AhoCorasickTest.java)
 * 1. Test ca "ushers" với {"he", "she", "his", "hers"} thẩm định Dictionary Suffix Link (outputLink)
 * 2. Equivalence Test: ACFailMatcher vs ACDFAMatcher trên k in {1, 10, 20} mẫu ngẫu nhiên (100% khớp)
 * 3. Kiểm tra duy trì trạng thái Automaton qua ranh giới block cực nhỏ B in {1, 2, 3, 7} bytes
 */
public class AhoCorasickTest {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("     AHO-CORASICK CORRECTNESS TEST SUITE (AhoCorasickTest)");
        System.out.println("==================================================================\n");

        boolean pass1 = testUshers();
        boolean pass2 = testEquivalence();
        boolean pass3 = testBlockBoundary();

        if (pass1 && pass2 && pass3) {
            System.out.println("\n>>> [100% PASS] TẤT CẢ CÁC BÀI TEST TÍNH ĐÚNG AHO-CORASICK ĐÃ ĐẠT! <<<");
        } else {
            throw new AssertionError("Có bài test Aho-Corasick không đạt!");
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
    public static boolean testUshers() {
        System.out.println("--- [1] Test ca 'ushers' & Dictionary Suffix Link ---");
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

        boolean failCheck = cFail.getCount() == 3 
                && cFail.hasMatch(1, 1)  // she at 1
                && cFail.hasMatch(2, 0)  // he at 2
                && cFail.hasMatch(2, 3); // hers at 2

        boolean dfaCheck = cDfa.getCount() == 3
                && cDfa.hasMatch(1, 1)
                && cDfa.hasMatch(2, 0)
                && cDfa.hasMatch(2, 3);

        if (failCheck && dfaCheck) {
            System.out.printf("  [PASS] Cả AC-Fail và AC-DFA đều tìm đúng: 'she' @ 1, 'he' @ 2, 'hers' @ 2 (count=%d)%n", cFail.getCount());
            return true;
        } else {
            System.err.printf("  [FAIL] Ushers test: FailPass=%b, DfaPass=%b%n", failCheck, dfaCheck);
            return false;
        }
    }

    /**
     * Yêu cầu 2: Equivalence Test giữa 2 biến thể: Chạy đồng thời ACFailMatcher và ACDFAMatcher
     * trên tập k in {1, 10, 20} mẫu, đối chiếu count:check phải trùng khớp 100%.
     */
    public static boolean testEquivalence() {
        System.out.println("\n--- [2] Test tính tương đương (Equivalence Test: Fail vs DFA) ---");
        int[] kValues = {1, 10, 20};
        Random rng = new Random(2026);
        boolean allPassed = true;

        for (int k : kValues) {
            System.out.printf("  Đang kiểm tra với k = %d mẫu ngẫu nhiên (chạy 50 ca/k)...%n", k);
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

                if (cFail.getCount() != cDfa.getCount() || cFail.getChecksum() != cDfa.getChecksum()) {
                    System.err.printf("  [FAIL] Mismatch tại k=%d: Fail(count=%d, check=%d) != DFA(count=%d, check=%d)%n",
                            k, cFail.getCount(), cFail.getChecksum(), cDfa.getCount(), cDfa.getChecksum());
                    allPassed = false;
                    break;
                }
            }
        }

        if (allPassed) {
            System.out.println("  [PASS] 100% các ca thử nghiệm k in {1, 10, 20} cho kết quả count:checksum trùng khớp tuyệt đối giữa Fail và DFA!");
        }
        return allPassed;
    }

    /**
     * Yêu cầu 3: Kiểm tra duy trì trạng thái Automaton không bị reset khi chuyển tiếp
     * qua ranh giới các block kích thước cực nhỏ B in {1, 2, 3, 7} bytes so với quét toàn bộ một lần.
     */
    public static boolean testBlockBoundary() {
        System.out.println("\n--- [3] Test duy trì trạng thái qua ranh giới block B in {1, 2, 3, 7} bytes ---");
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

        boolean allPassed = true;

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

            boolean failOk = cFail.getCount() == expCount && cFail.getChecksum() == expCheck;
            boolean dfaOk = cDfa.getCount() == expCount && cDfa.getChecksum() == expCheck;

            if (failOk && dfaOk) {
                System.out.printf("  [PASS] Block size B = %d bytes -> Khớp 100%% với Baseline (count=%d, check=%d)%n",
                        B, expCount, expCheck);
            } else {
                System.err.printf("  [FAIL] Block size B = %d: Fail(count=%d, check=%d), DFA(count=%d, check=%d) != Expected(%d, %d)%n",
                        B, cFail.getCount(), cFail.getChecksum(), cDfa.getCount(), cDfa.getChecksum(), expCount, expCheck);
                allPassed = false;
            }
        }
        return allPassed;
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
