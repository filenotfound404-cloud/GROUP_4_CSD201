package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cài đặt giải thuật KMP (Knuth-Morris-Pratt) tối ưu cho việc đọc theo từng khối (block streaming).
 * 
 * Mục tiêu kỹ thuật (Nhiệm vụ Cặp 2 - Hải):
 * 1. Tiền xử lý bảng tiền tố pi (Prefix function / Failure function) theo Knuth (1977) & Sedgewick §5.3.
 * 2. Giữ nguyên biến trạng thái j (số byte đã khớp) liên tục qua các ranh giới block kích thước B.
 * 3. Hỗ trợ tìm kiếm các kết quả chồng lấn (overlapping matches) với độ phức tạp thời gian O(n) độc lập với B.
 * 4. Xác định chính xác vị trí tuyệt đối của mẫu (0-indexed byte offset) trong toàn bộ luồng dữ liệu.
 */
public class KmpBlockMatcher {

    /**
     * Interface callback nhận vị trí xuất hiện của mẫu trong luồng dữ liệu.
     */
    @FunctionalInterface
    public interface MatchListener {
        void onMatch(long byteOffset);
    }

    /**
     * Xây dựng bảng tiền tố pi (Prefix Function / Failure Function).
     * pi[i] là độ dài tiền tố thực sự dài nhất của pattern[0..i] đồng thời là hậu tố của pattern[0..i].
     * 
     * Độ phức tạp: Thời gian O(m), Bộ nhớ O(m) với m = pattern.length.
     * Tham khảo: Knuth, Morris, Pratt (1977) & Robert Sedgewick (Algorithms 4th ed, §5.3).
     * 
     * @param pattern mảng byte của mẫu cần tìm
     * @return mảng int[] kích thước m chứa các giá trị tiền tố pi
     */
    public static int[] computePrefixTable(byte[] pattern) {
        if (pattern == null || pattern.length == 0) {
            throw new IllegalArgumentException("Mẫu pattern không được null hoặc rỗng");
        }

        int m = pattern.length;
        int[] pi = new int[m];
        pi[0] = 0; // Ký tự đầu tiên không có tiền tố thực sự nào phù hợp
        int j = 0; // j biểu diễn độ dài tiền tố khớp trước đó

        for (int i = 1; i < m; i++) {
            // Khi ký tự tiếp theo không khớp, lui trạng thái j về pi[j - 1]
            while (j > 0 && pattern[i] != pattern[j]) {
                j = pi[j - 1];
            }

            // Nếu ký tự khớp, mở rộng tiền tố thêm 1
            if (pattern[i] == pattern[j]) {
                j++;
            }

            pi[i] = j;
        }

        return pi;
    }

    /**
     * Tìm kiếm mẫu pattern trong luồng dữ liệu InputStream bằng thuật toán KMP đọc theo từng block.
     * 
     * Giải pháp kỹ thuật giữ trạng thái j qua ranh giới block:
     * - Biến trạng thái j (current matched length) được khởi tạo ngoài vòng lặp đọc block (j = 0).
     * - Khi đọc xong một block buffer kích thước B, j KHÔNG bị reset mà được giữ nguyên giá trị.
     * - Khi block tiếp theo được nạp vào buffer, việc so khớp tiếp tục với giá trị j hiện tại.
     * - Nhờ tính chất Markov (trạng thái tiếp theo chỉ phụ thuộc vào byte hiện tại và j), KMP không cần
     *   quay lui con trỏ stream hoặc lưu lại các byte của block trước.
     * 
     * @param input luồng dữ liệu cần tìm kiếm
     * @param pattern mẫu byte cần tìm
     * @param blockSize kích thước buffer mỗi lần đọc từ input (B bytes)
     * @param listener callback nhận vị trí byte xuất hiện
     * @return tổng số lần xuất hiện tìm thấy
     * @throws IOException khi có lỗi đọc stream
     */
    public static long searchStream(InputStream input, byte[] pattern, int blockSize, MatchListener listener) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream không được null");
        }
        if (pattern == null || pattern.length == 0) {
            throw new IllegalArgumentException("Pattern không được null hoặc rỗng");
        }
        if (blockSize <= 0) {
            throw new IllegalArgumentException("Block size phải lớn hơn 0 (blockSize = " + blockSize + ")");
        }

        int m = pattern.length;
        int[] pi = computePrefixTable(pattern);

        byte[] buffer = new byte[blockSize];
        int j = 0;                 // BIẾN TRẠNG THÁI CỐT LÕI: số ký tự đã khớp liên tiếp
        long globalOffset = 0;     // Chỉ số byte toàn cục trong stream (0-indexed)
        long matchCount = 0;       // Đếm số lần khớp tìm thấy
        int bytesRead;

        // Vòng lặp ngoài: Đọc từng khối (block) kích thước tối đa blockSize
        while ((bytesRead = input.read(buffer)) != -1) {
            // Vòng lặp trong: Duyệt từng byte trong khối hiện tại
            for (int b = 0; b < bytesRead; b++) {
                byte currentByte = buffer[b];

                // Nếu byte hiện tại không khớp, lui trạng thái j về pi[j - 1]
                while (j > 0 && currentByte != pattern[j]) {
                    j = pi[j - 1];
                }

                // Nếu byte hiện tại khớp với pattern[j], chuyển sang trạng thái kế tiếp
                if (currentByte == pattern[j]) {
                    j++;
                }

                // Khi đã khớp toàn bộ độ dài m của mẫu
                if (j == m) {
                    long matchStart = globalOffset - m + 1;
                    if (listener != null) {
                        listener.onMatch(matchStart);
                    }
                    matchCount++;

                    // Chuyển trạng thái để tiếp tục tìm các mẫu xuất hiện chồng lấn (overlapping)
                    j = pi[m - 1];
                }

                globalOffset++;
            }
        }

        return matchCount;
    }

    /**
     * Phương thức tiện ích: Tìm kiếm và trả về danh sách toàn bộ các vị trí bắt đầu của mẫu.
     * 
     * @param input luồng dữ liệu
     * @param pattern mẫu byte
     * @param blockSize kích thước khối buffer
     * @return danh sách các vị trí bắt đầu tìm thấy (0-indexed)
     */
    public static List<Long> findAllPositions(InputStream input, byte[] pattern, int blockSize) throws IOException {
        List<Long> positions = new ArrayList<>();
        searchStream(input, pattern, blockSize, positions::add);
        return positions;
    }

    /**
     * Phương thức tiện ích với chuỗi String và chuẩn mã hóa UTF-8 / ISO-8859-1.
     */
    public static List<Long> findAllPositions(String text, String pattern, int blockSize) throws IOException {
        byte[] textBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        byte[] patternBytes = pattern.getBytes(StandardCharsets.ISO_8859_1);
        try (InputStream stream = new ByteArrayInputStream(textBytes)) {
            return findAllPositions(stream, patternBytes, blockSize);
        }
    }

    /**
     * Thuật toán Brute Force (để đối chiếu thực nghiệm số phép so sánh và kết quả).
     */
    public static List<Long> bruteForceSearch(byte[] text, byte[] pattern) {
        List<Long> positions = new ArrayList<>();
        int n = text.length;
        int m = pattern.length;

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text[i + j] == pattern[j]) {
                j++;
            }
            if (j == m) {
                positions.add((long) i);
            }
        }
        return positions;
    }

    // =========================================================================
    // HÀM TEST ĐỘC LẬP (Dành riêng cho bạn Hải test thử nghiệm KMP qua Block)
    // Hoàn toàn tách biệt, KHÔNG can thiệp vào hàm main chính của KkmpExperiment.
    // =========================================================================
    public static void main(String[] args) throws IOException {
        System.out.println("========================================================================");
        System.out.println("  KIỂM THỬ THUẬT TOÁN KMP STREAMING THEO BLOCK - NHIỆM VỤ CẶP 2 (HẢI)   ");
        System.out.println("========================================================================");

        boolean allPassed = true;

        // TEST 1: Kiểm tra tính đúng của bảng tiền tố pi (Prefix Function)
        System.out.println("\n[TEST 1] Kiểm tra tính bảng tiền tố pi:");
        String testPattern1 = "ABABAC";
        int[] expectedPi1 = {0, 0, 1, 2, 3, 0};
        int[] actualPi1 = computePrefixTable(testPattern1.getBytes(StandardCharsets.ISO_8859_1));
        boolean pi1Pass = Arrays.equals(expectedPi1, actualPi1);
        System.out.println("Pattern: " + testPattern1);
        System.out.println("Expected pi: " + Arrays.toString(expectedPi1));
        System.out.println("Actual pi:   " + Arrays.toString(actualPi1));
        System.out.println("Kết quả TEST 1: " + (pi1Pass ? "PASS" : "FAIL"));
        allPassed &= pi1Pass;

        // TEST 2: Tính độc lập với kích thước block B (B=1, B=2, B=3, B=7, B=1024)
        System.out.println("\n[TEST 2] Kiểm tra tính nhất quán qua các kích thước block B (Boundary Test):");
        String text2 = "ABABDABACDABABCABAB";
        String pattern2 = "ABABCABAB";
        int[] blockSizes = {1, 2, 3, 5, 7, 8, 16, 1024};
        List<Long> baseline = bruteForceSearch(
                text2.getBytes(StandardCharsets.ISO_8859_1),
                pattern2.getBytes(StandardCharsets.ISO_8859_1)
        );
        System.out.println("Văn bản: \"" + text2 + "\", Mẫu: \"" + pattern2 + "\"");
        System.out.println("Kỳ vọng (Brute Force): " + baseline);

        boolean blockInvariant = true;
        for (int B : blockSizes) {
            List<Long> actual = findAllPositions(text2, pattern2, B);
            boolean match = baseline.equals(actual);
            System.out.printf("  - Block B = %4d byte(s) -> Kết quả: %-15s [%s]\n",
                    B, actual.toString(), match ? "PASS" : "FAIL");
            if (!match) blockInvariant = false;
        }
        allPassed &= blockInvariant;

        // TEST 3: Kiểm tra mẫu trùng lặp và chồng lấn (Overlapping Matches)
        System.out.println("\n[TEST 3] Kiểm tra khớp chồng lấn (Overlapping):");
        String text3 = "aaaaa";
        String pattern3 = "aaa";
        List<Long> expected3 = Arrays.asList(0L, 1L, 2L);
        List<Long> actual3 = findAllPositions(text3, pattern3, 2); // Block size = 2 (nhỏ hơn độ dài mẫu 3)
        boolean test3Pass = expected3.equals(actual3);
        System.out.println("Văn bản: \"" + text3 + "\", Mẫu: \"" + pattern3 + "\", Block size = 2");
        System.out.println("Expected: " + expected3 + " | Actual: " + actual3);
        System.out.println("Kết quả TEST 3: " + (test3Pass ? "PASS" : "FAIL"));
        allPassed &= test3Pass;

        // TEST 4: Mẫu nằm chính xác ngay ranh giới vắt ngang 2 block
        System.out.println("\n[TEST 4] Kiểm tra mẫu vắt ngang đúng ranh giới 2 block:");
        // Giả sử block 1 đọc "HELLO", block 2 đọc "WORLD". Mẫu cần tìm là "OWOR" (nằm vắt từ block 1 sang 2)
        String text4 = "HELLOWORLD";
        String pattern4 = "OWOR";
        int b4 = 5; // Block 1 = "HELLO", Block 2 = "WORLD"
        List<Long> actual4 = findAllPositions(text4, pattern4, b4);
        List<Long> expected4 = Arrays.asList(4L); // "OWOR" bắt đầu tại index 4 ('O' cuối HELLO)
        boolean test4Pass = expected4.equals(actual4);
        System.out.println("Văn bản: \"" + text4 + "\", Mẫu: \"" + pattern4 + "\", Block size = 5");
        System.out.println("Expected: " + expected4 + " | Actual: " + actual4);
        System.out.println("Kết quả TEST 4: " + (test4Pass ? "PASS" : "FAIL"));
        allPassed &= test4Pass;

        // TEST 5: Dữ liệu bất lợi T4 (Worst-Case Dataset: 'a...ab' trong 'a...a')
        System.out.println("\n[TEST 5] Kiểm tra tập dữ liệu bất lợi T4 (RQ2 Worst-case Demonstration):");
        int n5 = 50_000;
        int m5 = 100;
        char[] textChars = new char[n5];
        Arrays.fill(textChars, 'a');
        String text5 = new String(textChars);

        char[] patternChars = new char[m5];
        Arrays.fill(patternChars, 'a');
        patternChars[m5 - 1] = 'b'; // Mẫu 'aaaa...ab'
        String pattern5 = new String(patternChars);

        long startKmp = System.nanoTime();
        List<Long> kmpResult = findAllPositions(text5, pattern5, 4096);
        long endKmp = System.nanoTime();
        double kmpTimeMs = (endKmp - startKmp) / 1_000_000.0;

        long startBf = System.nanoTime();
        List<Long> bfResult = bruteForceSearch(
                text5.getBytes(StandardCharsets.ISO_8859_1),
                pattern5.getBytes(StandardCharsets.ISO_8859_1)
        );
        long endBf = System.nanoTime();
        double bfTimeMs = (endBf - startBf) / 1_000_000.0;

        System.out.printf("Tập dữ liệu T4: n = %d, m = %d, Pattern = 'a'^(%d) + 'b'\n", n5, m5, m5 - 1);
        System.out.printf("  - KMP time:         %8.3f ms (Số matches: %d)\n", kmpTimeMs, kmpResult.size());
        System.out.printf("  - Brute Force time: %8.3f ms (Số matches: %d)\n", bfTimeMs, bfResult.size());
        System.out.printf("  -> Tốc độ KMP nhanh hơn: %.2fx lần so với Brute Force trên T4!\n", bfTimeMs / Math.max(0.001, kmpTimeMs));
        boolean test5Pass = kmpResult.equals(bfResult);
        allPassed &= test5Pass;

        // TEST 6: Xử lý ngoại lệ tham số rỗng
        System.out.println("\n[TEST 6] Kiểm tra xử lý ngoại lệ mẫu rỗng:");
        boolean exceptionThrown = false;
        try {
            findAllPositions("text", "", 10);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
            System.out.println("Bắt ngoại lệ thành công: " + e.getMessage());
        }
        allPassed &= exceptionThrown;

        System.out.println("\n========================================================================");
        if (allPassed) {
            System.out.println(">>> TẤT CẢ CÁC BÀI TEST KMP STREAMING BLOCK ĐỀU ĐẠT (100% PASS)! <<<");
        } else {
            System.out.println(">>> CÓ BÀI TEST THẤT BẠI! VUI LÒNG KIỂM TRA LẠI. <<<");
        }
        System.out.println("========================================================================");
    }
}
