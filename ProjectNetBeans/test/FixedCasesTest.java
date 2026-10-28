package test;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import algorithm.kmp.KKMPMatcher;
import algorithm.kmp.KMPMatcher;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * FixedCasesTest - Kiểm thử 5 ca cố định bắt buộc tại Mục 5 của đề tài CSD201:
 * 1. "aaaaa" tìm "aaa" -> offset 0, 1, 2.
 * 2. "abababab" tìm "abab" -> offset 0, 2, 4.
 * 3. "abc" tìm "abcd" -> count = 0.
 * 4. Mẫu rỗng -> ném ngoại lệ IllegalArgumentException.
 * 5. "ushers" tìm {"he", "she", "his", "hers"} -> she@1, he@2, hers@2.
 */
public class FixedCasesTest {

    private static int passed = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               BỘ KIỂM THỬ 5 CA CỐ ĐỊNH CHUẨN ĐỀ TÀI (MỤC 5)                    ");
        System.out.println("================================================================================");

        testMatcher(new KMPMatcher());
        testMatcher(new KKMPMatcher());

        System.out.println("================================================================================");
        System.out.printf("KẾT QUẢ FIXED CASES TEST: %d / %d ĐẠT\n", passed, total);
        System.out.println("================================================================================");
    }

    private static void testMatcher(PatternMatching matcher) {
        System.out.println("\n--- Đang kiểm tra: " + matcher.getAlgorithmName() + " ---");

        // Ca 1: "aaaaa" tìm "aaa" -> 3 matches tại 0, 1, 2
        total++;
        matcher.reset();
        matcher.compilePattern(Arrays.asList("aaa".getBytes(StandardCharsets.ISO_8859_1)));
        ChecksumConsumer c1 = new ChecksumConsumer();
        matcher.searchPattern("aaaaa".getBytes(StandardCharsets.ISO_8859_1), c1);
        if (c1.getCount() == 3) {
            passed++;
            System.out.println("  [PASS] Ca 1: 'aaaaa' tìm 'aaa' (Count = 3)");
        } else {
            System.err.println("  [FAIL] Ca 1: Kỳ vọng count=3 nhưng nhận " + c1.getCount());
        }

        // Ca 2: "abababab" tìm "abab" -> 3 matches tại 0, 2, 4
        total++;
        matcher.reset();
        matcher.compilePattern(Arrays.asList("abab".getBytes(StandardCharsets.ISO_8859_1)));
        ChecksumConsumer c2 = new ChecksumConsumer();
        matcher.searchPattern("abababab".getBytes(StandardCharsets.ISO_8859_1), c2);
        if (c2.getCount() == 3) {
            passed++;
            System.out.println("  [PASS] Ca 2: 'abababab' tìm 'abab' (Count = 3)");
        } else {
            System.err.println("  [FAIL] Ca 2: Kỳ vọng count=3 nhưng nhận " + c2.getCount());
        }

        // Ca 3: "abc" tìm "abcd" -> 0 match
        total++;
        matcher.reset();
        matcher.compilePattern(Arrays.asList("abcd".getBytes(StandardCharsets.ISO_8859_1)));
        ChecksumConsumer c3 = new ChecksumConsumer();
        matcher.searchPattern("abc".getBytes(StandardCharsets.ISO_8859_1), c3);
        if (c3.getCount() == 0) {
            passed++;
            System.out.println("  [PASS] Ca 3: 'abc' tìm 'abcd' (Count = 0)");
        } else {
            System.err.println("  [FAIL] Ca 3: Kỳ vọng count=0 nhưng nhận " + c3.getCount());
        }

        // Ca 4: Mẫu rỗng -> IllegalArgumentException
        total++;
        boolean caught = false;
        try {
            matcher.reset();
            matcher.compilePattern(Arrays.asList("".getBytes(StandardCharsets.ISO_8859_1)));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        if (caught) {
            passed++;
            System.out.println("  [PASS] Ca 4: Mẫu rỗng ném ngoại lệ IllegalArgumentException thành công");
        } else {
            System.err.println("  [FAIL] Ca 4: Mẫu rỗng không ném ngoại lệ");
        }

        // Ca 5: "ushers" tìm {"he", "she", "his", "hers"} -> she@1, he@2, hers@2 (Count = 3)
        total++;
        matcher.reset();
        matcher.compilePattern(Arrays.asList(
                "he".getBytes(StandardCharsets.ISO_8859_1),
                "she".getBytes(StandardCharsets.ISO_8859_1),
                "his".getBytes(StandardCharsets.ISO_8859_1),
                "hers".getBytes(StandardCharsets.ISO_8859_1)
        ));
        ChecksumConsumer c5 = new ChecksumConsumer();
        matcher.searchPattern("ushers".getBytes(StandardCharsets.ISO_8859_1), c5);
        if (c5.getCount() == 3) {
            passed++;
            System.out.println("  [PASS] Ca 5: 'ushers' tìm {he, she, his, hers} (Count = 3)");
        } else {
            System.err.println("  [FAIL] Ca 5: Kỳ vọng count=3 nhưng nhận " + c5.getCount());
        }
    }
}
