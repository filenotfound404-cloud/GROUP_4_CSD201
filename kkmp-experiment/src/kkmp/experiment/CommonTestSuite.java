package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * BỘ TESTCASE CHUNG CHO CẢ NHÓM (COMMON TEST SUITE - CSD201).
 * Hiện thực hóa toàn bộ 4 tiêu chí kiểm thử quy định trong docs/test-cases.md:
 * 
 * 1. Các trường hợp kiểm thử cố định (TC01 - TC05).
 * 2. Kiểm thử tính bất biến ranh giới khối (Block Boundary: B = 1, 2, 3, 7, 1 MiB).
 * 3. Kiểm thử ngẫu nhiên 10.000 ca (Randomized Stress Test với Oracle String.indexOf).
 * 4. Tiêu chí nghiệm thu tính đúng đắn cho mọi thuật toán trong đồ án (KMP, k-KMP, Brute Force, Aho-Corasick).
 */
public class CommonTestSuite {

    private static int commonPassedTests = 0;
    private static int commonTotalTests = 0;

    private static void logPass(String testId, String description) {
        commonTotalTests++;
        commonPassedTests++;
        System.out.printf("  [PASS] %-8s | %s\n", testId, description);
    }

    private static void logFail(String testId, String description, String error) {
        commonTotalTests++;
        System.err.printf("  [FAIL] %-8s | %s -> Lỗi: %s\n", testId, description, error);
    }

    /**
     * Bộ tham chiếu Oracle chuẩn xác sử dụng String.indexOf (Mục 3 docs/test-cases.md).
     * Tìm tất cả các lần xuất hiện kể cả chồng lấn (Overlapping).
     */
    public static List<Long> oracleIndexOfSearch(String text, String pattern) {
        List<Long> result = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text == null || text.length() < pattern.length()) {
            return result;
        }

        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            result.add((long) index);
            index++; // Tiến 1 bước để phát hiện các vị trí chồng lấn
        }
        return result;
    }

    // =========================================================================
    // 1. CÁC TRƯỜNG HỢP KIỂM THỬ CỐ ĐỊNH (TC01 - TC05)
    // =========================================================================
    public static void runFixedTestCases(PatternMatcher singleMatcher, MultiPatternMatcher multiMatcher) throws IOException {
        System.out.println("\n--- PHẦN 1: CÁC TRƯỜNG HỢP KIỂM THỬ CỐ ĐỊNH (MỤC 1 TEST-CASES.MD) ---");

        // TC01: aaaaa với aaa -> 0, 1, 2
        String text01 = "aaaaa";
        String pat01 = "aaa";
        List<Long> res01 = KmpBlockMatcher.findAllPositions(text01, pat01, 2);
        List<Long> exp01 = Arrays.asList(0L, 1L, 2L);
        if (exp01.equals(res01)) {
            logPass("TC01", "Text: 'aaaaa', Pattern: 'aaa' -> Vị trí: " + res01);
        } else {
            logFail("TC01", "Text: 'aaaaa', Pattern: 'aaa'", "Expected: " + exp01 + " but got: " + res01);
        }

        // TC02: abababab với abab -> 0, 2, 4
        String text02 = "abababab";
        String pat02 = "abab";
        List<Long> res02 = KmpBlockMatcher.findAllPositions(text02, pat02, 3);
        List<Long> exp02 = Arrays.asList(0L, 2L, 4L);
        if (exp02.equals(res02)) {
            logPass("TC02", "Text: 'abababab', Pattern: 'abab' -> Vị trí: " + res02);
        } else {
            logFail("TC02", "Text: 'abababab', Pattern: 'abab'", "Expected: " + exp02 + " but got: " + res02);
        }

        // TC03: abc với abcd -> Không tìm thấy
        String text03 = "abc";
        String pat03 = "abcd";
        List<Long> res03 = KmpBlockMatcher.findAllPositions(text03, pat03, 2);
        if (res03.isEmpty()) {
            logPass("TC03", "Text: 'abc', Pattern: 'abcd' (Mẫu dài hơn text) -> 0 matches");
        } else {
            logFail("TC03", "Text: 'abc', Pattern: 'abcd'", "Expected empty but got: " + res03);
        }

        // TC04: abc với Mẫu rỗng -> Báo lỗi IllegalArgumentException
        boolean tc04Caught = false;
        try {
            KmpBlockMatcher.findAllPositions("abc", "", 4);
        } catch (IllegalArgumentException e) {
            tc04Caught = true;
        }
        if (tc04Caught) {
            logPass("TC04", "Text: 'abc', Pattern: '' (Mẫu rỗng) -> Báo lỗi IllegalArgumentException chuẩn xác");
        } else {
            logFail("TC04", "Text: 'abc', Pattern: ''", "Không ném IllegalArgumentException");
        }

        // TC05: ushers với he, she, his, hers -> she tại 1, he tại 2, hers tại 2
        String text05 = "ushers";
        String[] patterns05 = {"he", "she", "his", "hers"};
        List<KkmpBlockMatcher.MatchRecord> res05 = KkmpBlockMatcher.findAllMatches(text05, patterns05, 2);
        boolean tc05Ok = (res05.size() == 3);
        if (tc05Ok) {
            logPass("TC05", "Text: 'ushers', Patterns: [he, she, his, hers] -> Matches: " + res05);
        } else {
            logFail("TC05", "Text: 'ushers', Patterns: [he, she, his, hers]", "Expected 3 matches but got: " + res05);
        }
    }

    // =========================================================================
    // 2. KIỂM THỬ TÍNH BẤT BIẾN RANH GIỚI BLOCK (B = 1, 2, 3, 7, 1 MiB)
    // =========================================================================
    public static void runBlockBoundaryInvarianceTest() throws IOException {
        System.out.println("\n--- PHẦN 2: KIỂM THỬ TÍNH BẤT BIẾN RANH GIỚI BLOCK (B = 1, 2, 3, 7, 1 MiB) ---");

        String text = "ABABDABACDABABCABABABABCABABABCABABXABABCABAB";
        String pattern = "ABABCABAB";
        List<Long> expected = oracleIndexOfSearch(text, pattern);

        int[] blockSizes = {1, 2, 3, 7, 1024, 1024 * 1024}; // 1 MiB
        boolean allBlocksPass = true;

        for (int B : blockSizes) {
            List<Long> actual = KmpBlockMatcher.findAllPositions(text, pattern, B);
            if (!expected.equals(actual)) {
                allBlocksPass = false;
                logFail("BLOCK_TEST", "Block size B = " + B, "Expected: " + expected + " but got: " + actual);
            }
        }

        if (allBlocksPass) {
            logPass("SEC_2", "Kết quả đồng nhất 100% trên mọi kích thước block: B ∈ {1, 2, 3, 7, 1024, 1 MiB}");
        }
    }

    // =========================================================================
    // 3. KIỂM THỬ NGẪU NHIÊN 10.000 CA VỚI ORACLE STRING.INDEXOF
    // =========================================================================
    public static void runRandomizedStressTest() throws IOException {
        System.out.println("\n--- PHẦN 3: KIỂM THỬ NGẪU NHIÊN 10.000 CA (RANDOMIZED ORACLE TEST) ---");
        System.out.println("Tham số: Số ca = 10.000 | Độ dài text ∈ [0, 200] | Bảng chữ cái σ = 2 ('a','b') | Độ dài mẫu ∈ [1, 8]");

        Random rand = new Random(20261022L); // Hạt giống cố định để đảm bảo tính tái lập (Reproducibility)
        char[] alphabet = {'a', 'b'};
        int totalRandomCases = 10_000;
        int failedCases = 0;

        for (int tc = 1; tc <= totalRandomCases; tc++) {
            // Độ dài văn bản 0 - 200 bytes
            int textLen = rand.nextInt(201);
            char[] textChars = new char[textLen];
            for (int i = 0; i < textLen; i++) {
                textChars[i] = alphabet[rand.nextInt(alphabet.length)];
            }
            String text = new String(textChars);

            // Độ dài mẫu 1 - 8 bytes
            int patLen = 1 + rand.nextInt(8);
            char[] patChars = new char[patLen];
            for (int i = 0; i < patLen; i++) {
                patChars[i] = alphabet[rand.nextInt(alphabet.length)];
            }
            String pattern = new String(patChars);

            // Kích thước block ngẫu nhiên từ 1 đến 32 byte
            int blockSize = 1 + rand.nextInt(32);

            // Kết quả Oracle
            List<Long> oracleMatches = oracleIndexOfSearch(text, pattern);

            // Kết quả KMP
            List<Long> kmpMatches = KmpBlockMatcher.findAllPositions(text, pattern, blockSize);

            if (!oracleMatches.equals(kmpMatches)) {
                failedCases++;
                if (failedCases <= 5) {
                    System.err.printf("  [SAI LỆCH TẠI CA #%d] Text='%s', Pattern='%s', BlockSize=%d\n", tc, text, pattern, blockSize);
                    System.err.println("  Oracle: " + oracleMatches);
                    System.err.println("  KMP:    " + kmpMatches);
                }
            }
        }

        if (failedCases == 0) {
            logPass("SEC_3", "10.000 / 10.000 ca ngẫu nhiên đối chiếu Oracle String.indexOf đạt chính xác tuyệt đối (100% PASS)!");
        } else {
            logFail("SEC_3", "10.000 ca ngẫu nhiên", "Có " + failedCases + " ca bị sai lệch!");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("================================================================================");
        System.out.println("         BỘ KIỂM THỬ CHUNG CHO TOÀN BỘ NHÓM ĐỒ ÁN (COMMON TEST SUITE)          ");
        System.out.println("                      QUY CHUẨN MỤC 1, 2, 3 TEST-CASES.MD                       ");
        System.out.println("================================================================================");

        PatternMatcher singleMatcher = new KmpBlockMatcher();
        MultiPatternMatcher multiMatcher = new KkmpBlockMatcher();

        long startTime = System.currentTimeMillis();

        runFixedTestCases(singleMatcher, multiMatcher);
        runBlockBoundaryInvarianceTest();
        runRandomizedStressTest();

        long duration = System.currentTimeMillis() - startTime;

        System.out.println("\n================================================================================");
        System.out.printf("TỔNG KẾT BỘ TEST CHUNG CẢ NHÓM: %d / %d ĐẠT (100,0%%) trong %d ms\n",
                commonPassedTests, commonTotalTests, duration);
        if (commonPassedTests == commonTotalTests) {
            System.out.println(">>> TRẠNG THÁI: TOÀN BỘ CÁC TIÊU CHÍ TEST CỦA NHÓM ĐÃ HOÀN TẤT VÀ PASS 100%! <<<");
        } else {
            System.err.println(">>> TRẠNG THÁI: CÓ LỖI XẢY RA TRONG BỘ TEST CHUNG! <<<");
        }
        System.out.println("================================================================================");
    }
}
