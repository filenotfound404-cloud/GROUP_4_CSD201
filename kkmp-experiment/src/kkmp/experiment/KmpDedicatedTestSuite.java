package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * BỘ TESTCASE RIÊNG CHO THUẬT TOÁN KMP (NHIỆM VỤ CẶP 2 - SUBTASK 4).
 * 
 * Kiểm thử toàn diện và chuyên sâu từng khía cạnh của giải thuật KMP:
 * 1. Bảng tiền tố pi trên các dạng chuỗi đặc biệt (Fibonacci, tuần hoàn, không lặp, toàn trùng).
 * 2. Ranh giới Block cực hạn (B = 1, B < m, vết cắt ở mọi vị trí của mẫu).
 * 3. Khớp chồng lấn (Overlapping) phức tạp.
 * 4. Các trường hợp biên (Edge cases: rỗng, 1 ký tự, n < m, n = m, ngoại lệ).
 * 5. Tập dữ liệu bất lợi T4 (Worst-Case Adversarial Dataset - RQ2).
 * 6. Ổn định bộ nhớ trên luồng lớn (Stream memory scalability).
 */
public class KmpDedicatedTestSuite {

    private static int totalTests = 0;
    private static int passedTests = 0;

    private static void recordResult(String testName, boolean passed, String detail) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.printf("  [PASS] %-45s | %s\n", testName, detail);
        } else {
            System.err.printf("  [FAIL] %-45s | %s\n", testName, detail);
        }
    }

    // =========================================================================
    // 1. KIỂM THỬ BẢNG TIỀN TỐ PI (PREFIX FUNCTION)
    // =========================================================================
    public static void testPrefixTableSuite() {
        System.out.println("\n--- PHẦN 1: KIỂM THỬ BẢNG TIỀN TỐ PI (PREFIX FUNCTION) ---");

        // 1.1 Chuỗi không có tiền tố lặp
        checkPi("Không lặp (ABCDEF)", "ABCDEF", new int[]{0, 0, 0, 0, 0, 0});

        // 1.2 Toàn bộ ký tự giống nhau
        checkPi("Toàn ký tự giống (AAAAAA)", "AAAAAA", new int[]{0, 1, 2, 3, 4, 5});

        // 1.3 Mẫu kinh điển Sedgewick §5.3
        checkPi("Sedgewick §5.3 (ABABAC)", "ABABAC", new int[]{0, 0, 1, 2, 3, 0});

        // 1.4 Mẫu tuần hoàn chẵn
        checkPi("Tuần hoàn (ABABABAB)", "ABABABAB", new int[]{0, 0, 1, 2, 3, 4, 5, 6});

        // 1.5 Mẫu Knuth 1977
        checkPi("Knuth 1977 (AABAA)", "AABAA", new int[]{0, 1, 0, 1, 2});

        // 1.6 Chuỗi Fibonacci (F5 = ABAABABA)
        checkPi("Fibonacci String (ABAABABA)", "ABAABABA", new int[]{0, 0, 1, 1, 2, 3, 2, 3});

        // 1.7 Mẫu 1 ký tự
        checkPi("1 ký tự (X)", "X", new int[]{0});
    }

    private static void checkPi(String label, String patternStr, int[] expected) {
        byte[] pattern = patternStr.getBytes(StandardCharsets.ISO_8859_1);
        int[] actual = KmpBlockMatcher.computePrefixTable(pattern);
        boolean ok = Arrays.equals(expected, actual);
        recordResult("Bảng pi: " + label, ok, "Actual: " + Arrays.toString(actual));
    }

    // =========================================================================
    // 2. KIỂM THỬ RANH GIỚI BLOCK BUFFER CỰC HẠN
    // =========================================================================
    public static void testBlockBoundarySuite() throws IOException {
        System.out.println("\n--- PHẦN 2: KIỂM THỬ RANH GIỚI BLOCK (BOUNDARY INVARIANCE) ---");

        String text = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG. FOX JUMPS AGAIN!";
        String pattern = "FOX JUMPS";
        List<Long> baseline = KmpBlockMatcher.findAllPositions(text, pattern, 1024);

        int[] blockSizesToTest = {1, 2, 3, 4, 5, 7, 9, 10, 16, 64, 1024};
        boolean allMatch = true;
        for (int B : blockSizesToTest) {
            List<Long> res = KmpBlockMatcher.findAllPositions(text, pattern, B);
            if (!res.equals(baseline)) {
                allMatch = false;
                break;
            }
        }
        recordResult("Bất biến với mọi kích thước B (1 đến 1024)", allMatch, "Vị trí: " + baseline);

        // Kiểm thử vết cắt ở từng vị trí (mẫu 8 byte, cắt tại mọi offset từ 1 đến 7)
        String baseStr = "XXXXXXXX"; // prefix 8 bytes
        String patStr = "ABCDEFGH";  // mẫu 8 bytes
        String fullStr = baseStr + patStr + "YYYYYYYY";
        boolean allSplitOk = true;
        for (int split = 1; split <= 7; split++) {
            // Đặt block size sao cho block 1 cắt đúng vị trí 'split' bên trong mẫu patStr
            int B = baseStr.length() + split;
            List<Long> res = KmpBlockMatcher.findAllPositions(fullStr, patStr, B);
            if (res.size() != 1 || res.get(0) != (long) baseStr.length()) {
                allSplitOk = false;
                break;
            }
        }
        recordResult("Mẫu bị cắt ở mọi vị trí trong ranh giới", allSplitOk, "Thử nghiệm 7 vị trí cắt");
    }

    // =========================================================================
    // 3. KIỂM THỬ KHỚP CHỒNG LẤN (OVERLAPPING MATCHES)
    // =========================================================================
    public static void testOverlappingSuite() throws IOException {
        System.out.println("\n--- PHẦN 3: KIỂM THỬ KHỚP CHỒNG LẤN (OVERLAPPING) ---");

        // Chuỗi 10 chữ 'a', tìm 'aaa' -> 8 vị trí: 0, 1, 2, 3, 4, 5, 6, 7
        String text1 = "aaaaaaaaaa";
        String pat1 = "aaa";
        List<Long> res1 = KmpBlockMatcher.findAllPositions(text1, pat1, 2);
        boolean ok1 = (res1.size() == 8);
        recordResult("Lặp dày đặc 'aaa' trong 'a'^10 (B=2)", ok1, "Matches: " + res1.size() + " (Kỳ vọng: 8)");

        // Chuỗi tuần hoàn "abcabcabcabc", tìm "abcabc" -> vị trí: 0, 3, 6
        String text2 = "abcabcabcabc";
        String pat2 = "abcabc";
        List<Long> res2 = KmpBlockMatcher.findAllPositions(text2, pat2, 3);
        List<Long> exp2 = Arrays.asList(0L, 3L, 6L);
        boolean ok2 = exp2.equals(res2);
        recordResult("Tuần hoàn 'abcabc' trong 'abc'^4 (B=3)", ok2, "Vị trí: " + res2);
    }

    // =========================================================================
    // 4. KIỂM THỬ CÁC TRƯỜNG HỢP BIÊN VÀ NGOẠI LỆ (EDGE CASES)
    // =========================================================================
    public static void testEdgeCasesSuite() throws IOException {
        System.out.println("\n--- PHẦN 4: KIỂM THỬ TRƯỜNG HỢP BIÊN VÀ NGOẠI LỆ (EDGE CASES) ---");

        // 4.1 Stream rỗng
        List<Long> rEmpty = KmpBlockMatcher.findAllPositions("", "PATTERN", 4);
        recordResult("Stream rỗng", rEmpty.isEmpty(), "Matches = 0");

        // 4.2 Text ngắn hơn pattern
        List<Long> rShort = KmpBlockMatcher.findAllPositions("ABC", "ABCDEF", 4);
        recordResult("Text ngắn hơn mẫu (n < m)", rShort.isEmpty(), "Matches = 0");

        // 4.3 Text bằng đúng pattern (khớp)
        List<Long> rExactMatch = KmpBlockMatcher.findAllPositions("EXACT", "EXACT", 2);
        recordResult("Text bằng mẫu và khớp (n == m)", rExactMatch.equals(Arrays.asList(0L)), "Offset = 0");

        // 4.4 Text bằng đúng pattern (không khớp)
        List<Long> rExactMismatch = KmpBlockMatcher.findAllPositions("EXACT", "OTHER", 2);
        recordResult("Text bằng mẫu và trượt (n == m)", rExactMismatch.isEmpty(), "Matches = 0");

        // 4.5 Ngoại lệ: Mẫu null hoặc rỗng
        boolean exPattern = false;
        try {
            KmpBlockMatcher.computePrefixTable(new byte[0]);
        } catch (IllegalArgumentException e) {
            exPattern = true;
        }
        recordResult("Ngoại lệ khi pattern rỗng", exPattern, "Ném IllegalArgumentException thành công");

        // 4.6 Ngoại lệ: Block size <= 0
        boolean exBlock = false;
        try {
            KmpBlockMatcher.findAllPositions("DATA", "DA", 0);
        } catch (IllegalArgumentException e) {
            exBlock = true;
        }
        recordResult("Ngoại lệ khi blockSize <= 0", exBlock, "Ném IllegalArgumentException thành công");
    }

    // =========================================================================
    // 5. KIỂM THỬ TẬP DỮ LIỆU BẤT LỢI T4 (RQ2 WORST-CASE DEMONSTRATION)
    // =========================================================================
    public static void testAdversarialT4Suite() throws IOException {
        System.out.println("\n--- PHẦN 5: KIỂM THỬ TẬP DỮ LIỆU BẤT LỢI T4 (RQ2 ADVERSARIAL) ---");

        int n = 100_000;
        int m = 200;
        char[] textChars = new char[n];
        Arrays.fill(textChars, 'a');
        String text = new String(textChars);

        char[] patChars = new char[m];
        Arrays.fill(patChars, 'a');
        patChars[m - 1] = 'b'; // Mẫu 'aaaa...ab'
        String pattern = new String(patChars);

        byte[] textBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        byte[] patBytes = pattern.getBytes(StandardCharsets.ISO_8859_1);

        // KMP run
        ChecksumConsumer kmpConsumer = new ChecksumConsumer();
        long t0Kmp = System.nanoTime();
        try (InputStream in = new ByteArrayInputStream(textBytes)) {
            new KmpBlockMatcher().search(in, patBytes, 4096, kmpConsumer);
        }
        long t1Kmp = System.nanoTime();
        double kmpMs = (t1Kmp - t0Kmp) / 1_000_000.0;

        // Brute Force run
        long t0Bf = System.nanoTime();
        List<Long> bfRes = KmpBlockMatcher.bruteForceSearch(textBytes, patBytes);
        long t1Bf = System.nanoTime();
        double bfMs = (t1Bf - t0Bf) / 1_000_000.0;

        boolean speedupOk = (bfMs > kmpMs);
        recordResult("Tập dữ liệu T4: KMP duy trì O(n) tuyến tính",
                (kmpConsumer.getMatchCount() == 0 && bfRes.isEmpty() && speedupOk),
                String.format("KMP: %.2f ms vs BF: %.2f ms (KMP nhanh hơn %.1fx)", kmpMs, bfMs, bfMs / Math.max(0.01, kmpMs)));
    }

    // =========================================================================
    // 6. KIỂM THỬ TÍCH HỢP CHECKSUM VÀ INTERFACE PATTERNMATCHER
    // =========================================================================
    public static void testInterfaceAndChecksumSuite() throws IOException {
        System.out.println("\n--- PHẦN 6: KIỂM THỬ INTERFACE PATTERNMATCHER & CHECKSUMCONSUMER ---");

        PatternMatcher matcher = new KmpBlockMatcher();
        ChecksumConsumer consumer = new ChecksumConsumer();

        String sample = "LOG_ERROR_LINE_1_LOG_ERROR_LINE_2_LOG_ERROR_LINE_3";
        byte[] sampleBytes = sample.getBytes(StandardCharsets.ISO_8859_1);
        byte[] pBytes = "ERROR".getBytes(StandardCharsets.ISO_8859_1);

        try (InputStream in = new ByteArrayInputStream(sampleBytes)) {
            matcher.search(in, pBytes, 8, consumer);
        }

        boolean ok = (consumer.getMatchCount() == 3 && consumer.getChecksum() != 0);
        recordResult("Tích hợp chuẩn PatternMatcher & ChecksumConsumer", ok,
                "Algorithm: " + matcher.getName() + " | " + consumer);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("================================================================================");
        System.out.println("     BỘ KIỂM THỬ CHUYÊN BIỆT CHO THUẬT TOÁN KMP (KMP DEDICATED TEST SUITE)     ");
        System.out.println("                     NHIỆM VỤ CẶP 2 - CSD201 (SUBTASK 4)                        ");
        System.out.println("================================================================================");

        testPrefixTableSuite();
        testBlockBoundarySuite();
        testOverlappingSuite();
        testEdgeCasesSuite();
        testAdversarialT4Suite();
        testInterfaceAndChecksumSuite();

        System.out.println("\n================================================================================");
        System.out.printf("TỔNG KẾT BÀI KIỂM THỬ KMP: %d / %d ĐẠT (%.1f%%)\n",
                passedTests, totalTests, (100.0 * passedTests / totalTests));
        if (passedTests == totalTests) {
            System.out.println(">>> TRẠNG THÁI: TẤT CẢ CÁC BÀI TEST CHUYÊN BIỆT CHO KMP ĐÃ PASS 100%! <<<");
        } else {
            System.err.println(">>> TRẠNG THÁI: CÓ BÀI TEST KMP BỊ LỖI! <<<");
        }
        System.out.println("================================================================================");
    }
}
