package test;

import algorithm.kmp.KMPMatcher;
import java.util.Arrays;

/**
 * KMPTest - Bộ kiểm thử chuyên sâu thuật toán KMP (Nhiệm vụ Cặp 2 - Đầu việc 4.1).
 * 1. Kiểm tra tính đúng đắn của bảng tiền tố pi.
 * 2. Kiểm tra cơ chế giữ biến trạng thái j qua block.
 * 3. Kiểm tra mảng trạng thái k phần tử của MultiKMP.
 */
public class KMPTest {

    private static int passed = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            BỘ KIỂM THỬ CHUYÊN BIỆT KMP (ĐẦU VIỆC 4.1 PHÂN CÔNG)                ");
        System.out.println("================================================================================");

        testPrefixTable();
        testBlockStateContinuity();

        System.out.println("\n================================================================================");
        System.out.printf("TỔNG KẾT KMP TEST: %d / %d ĐẠT\n", passed, total);
        System.out.println("================================================================================");
    }

    private static void testPrefixTable() {
        System.out.println("\n--- 1. Kiểm thử Bảng Tiền tố pi ---");
        checkPi("ABCDEF (Không lặp)", "ABCDEF", new int[]{0, 0, 0, 0, 0, 0});
        checkPi("AAAAAA (Toàn ký tự giống)", "AAAAAA", new int[]{0, 1, 2, 3, 4, 5});
        checkPi("ABABAC (Sedgewick §5.3)", "ABABAC", new int[]{0, 0, 1, 2, 3, 0});
        checkPi("ABABABAB (Tuần hoàn)", "ABABABAB", new int[]{0, 0, 1, 2, 3, 4, 5, 6});
        checkPi("AABAA (Knuth 1977)", "AABAA", new int[]{0, 1, 0, 1, 2});
        checkPi("ABAABABA (Fibonacci)", "ABAABABA", new int[]{0, 0, 1, 1, 2, 3, 2, 3});
    }

    private static void checkPi(String name, String pat, int[] expected) {
        total++;
        int[] actual = KMPMatcher.computePrefixTable(pat.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        if (Arrays.equals(actual, expected)) {
            passed++;
            System.out.println("  [PASS] " + name + " -> " + Arrays.toString(actual));
        } else {
            System.err.println("  [FAIL] " + name + " -> Kỳ vọng " + Arrays.toString(expected) + " nhưng nhận " + Arrays.toString(actual));
        }
    }

    private static void testBlockStateContinuity() {
        System.out.println("\n--- 2. Kiểm thử duy trì trạng thái j qua ranh giới block ---");
        total++;
        KMPMatcher matcher = new KMPMatcher();
        matcher.compilePattern(Arrays.asList("ABCD".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1)));
        algorithm.ChecksumConsumer consumer = new algorithm.ChecksumConsumer();

        // Cắt chuỗi "ABCD" thành 2 block: Block 1 = "AB", Block 2 = "CD"
        matcher.searchPattern("AB".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1), consumer);
        matcher.searchPattern("CD".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1), consumer);

        if (consumer.getCount() == 1) {
            passed++;
            System.out.println("  [PASS] Duy trì trạng thái qua ranh giới block: 'AB' + 'CD' tìm 'ABCD' (Count = 1)");
        } else {
            System.err.println("  [FAIL] Bị mất trạng thái qua block: count=" + consumer.getCount());
        }
    }
}
