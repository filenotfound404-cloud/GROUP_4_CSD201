package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Cài đặt giải thuật k-KMP (Multi-Pattern KMP) tối ưu cho việc đọc file lớn theo từng block (1 lượt - Single Pass).
 * Triển khai interface MultiPatternMatcher phục vụ kiểm thử và benchmark.
 * 
 * Mục tiêu kỹ thuật (Nhiệm vụ Cặp 2 - Hải & Nhi):
 * 1. Đọc stream 1 lượt duy nhất (Single-pass streaming).
 * 2. Lưu trữ mảng k trạng thái (PatternState) cho k mẫu cần tìm.
 * 3. Với mỗi byte đọc được trong block buffer, cập nhật đồng thời k trạng thái KMP.
 * 4. Giữ nguyên toàn bộ k biến trạng thái qua các ranh giới block kích thước B.
 * 5. Tích hợp ChecksumConsumer chung để đo kiểm hiệu năng và đối chiếu tính đúng.
 */
public class KkmpBlockMatcher implements MultiPatternMatcher {

    /**
     * Interface callback nhận kết quả khớp đa mẫu (ID mẫu và vị trí byte).
     */
    @FunctionalInterface
    public interface MultiMatchListener {
        void onMatch(int patternIndex, long byteOffset);
    }

    /**
     * Lớp lưu trữ kết quả một lần khớp mẫu.
     */
    public static class MatchRecord {
        public final int patternIndex;
        public final long byteOffset;

        public MatchRecord(int patternIndex, long byteOffset) {
            this.patternIndex = patternIndex;
            this.byteOffset = byteOffset;
        }

        @Override
        public String toString() {
            return "MatchRecord{id=" + patternIndex + ", offset=" + byteOffset + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MatchRecord)) return false;
            MatchRecord that = (MatchRecord) o;
            return patternIndex == that.patternIndex && byteOffset == that.byteOffset;
        }

        @Override
        public int hashCode() {
            return 31 * patternIndex + (int) (byteOffset ^ (byteOffset >>> 32));
        }
    }

    /**
     * Cấu trúc lưu trạng thái KMP cho từng mẫu riêng biệt.
     */
    public static class PatternState {
        public final int id;
        public final byte[] pattern;
        public final int[] pi;
        public int matched; // Biến trạng thái j: số ký tự đã khớp hiện tại

        public PatternState(byte[] pattern, int id) {
            if (pattern == null || pattern.length == 0) {
                throw new IllegalArgumentException("Mẫu thứ " + id + " không được null hoặc rỗng");
            }
            this.id = id;
            this.pattern = pattern;
            this.pi = KmpBlockMatcher.computePrefixTable(pattern);
            this.matched = 0;
        }
    }

    @Override
    public String getName() {
        return "k-KMP";
    }

    /**
     * Thực hiện tìm kiếm k mẫu theo chuẩn MultiPatternMatcher.
     */
    @Override
    public long search(InputStream input, byte[][] patterns, int blockSize, ChecksumConsumer consumer) throws IOException {
        MultiMatchListener listener = (consumer != null) ? consumer::onMatch : null;
        return searchStream(input, patterns, blockSize, listener);
    }

    /**
     * Quét luồng dữ liệu 1 lượt theo từng khối, cập nhật đồng thời mảng k trạng thái KMP.
     * 
     * @param input luồng dữ liệu cần tìm
     * @param patterns mảng 2 chiều chứa k mẫu byte
     * @param blockSize kích thước buffer mỗi lần đọc
     * @param listener callback nhận kết quả
     * @return tổng số lần xuất hiện của tất cả các mẫu
     */
    public static long searchStream(InputStream input, byte[][] patterns, int blockSize, MultiMatchListener listener) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream không được null");
        }
        if (patterns == null || patterns.length == 0) {
            throw new IllegalArgumentException("Danh sách mẫu không được null hoặc rỗng");
        }
        if (blockSize <= 0) {
            throw new IllegalArgumentException("Block size phải lớn hơn 0");
        }

        int k = patterns.length;
        PatternState[] states = new PatternState[k];
        for (int i = 0; i < k; i++) {
            states[i] = new PatternState(patterns[i], i);
        }

        byte[] buffer = new byte[blockSize];
        long globalOffset = 0;
        long totalMatches = 0;
        int bytesRead;

        // Vòng lặp đọc từng block từ stream
        while ((bytesRead = input.read(buffer)) != -1) {
            // Duyệt từng byte trong block buffer
            for (int b = 0; b < bytesRead; b++) {
                byte currentByte = buffer[b];

                // Cập nhật đồng thời cho cả k mẫu
                for (int p = 0; p < k; p++) {
                    PatternState state = states[p];

                    // KMP Failure transition: lùi j về pi[j - 1] nếu mismatch
                    while (state.matched > 0 && currentByte != state.pattern[state.matched]) {
                        state.matched = state.pi[state.matched - 1];
                    }

                    // KMP Match transition: tăng j nếu khớp
                    if (currentByte == state.pattern[state.matched]) {
                        state.matched++;
                    }

                    // Phát hiện mẫu xuất hiện hoàn chỉnh
                    if (state.matched == state.pattern.length) {
                        long matchStart = globalOffset - state.pattern.length + 1;
                        if (listener != null) {
                            listener.onMatch(state.id, matchStart);
                        }
                        totalMatches++;

                        // Lui trạng thái để tiếp tục tìm kết quả chồng lấn (overlapping)
                        state.matched = state.pi[state.pattern.length - 1];
                    }
                }

                globalOffset++;
            }
            // HẾT BLOCK: Mọi state.matched của k mẫu ĐƯỢC GIỮ NGUYÊN cho block tiếp theo
        }

        return totalMatches;
    }

    /**
     * Tiện ích tìm kiếm với mảng String.
     */
    public static List<MatchRecord> findAllMatches(String text, String[] patterns, int blockSize) throws IOException {
        byte[] textBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        byte[][] patternBytes = new byte[patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            patternBytes[i] = patterns[i].getBytes(StandardCharsets.ISO_8859_1);
        }

        List<MatchRecord> matches = new ArrayList<>();
        try (InputStream stream = new ByteArrayInputStream(textBytes)) {
            searchStream(stream, patternBytes, blockSize, (id, offset) -> matches.add(new MatchRecord(id, offset)));
        }
        return matches;
    }

    // =========================================================================
    // HÀM TEST ĐỘC LẬP CHO THUẬT TOÁN ĐA MẪU k-KMP
    // =========================================================================
    public static void main(String[] args) throws IOException {
        System.out.println("========================================================================");
        System.out.println("  KIỂM THỬ THUẬT TOÁN ĐA MẪU k-KMP STREAMING - NHIỆM VỤ CẶP 2 (SUBTASK 2)");
        System.out.println("========================================================================");

        boolean allPassed = true;

        // Test 1: Đa mẫu trên chuỗi ushers (Sedgewick TC05)
        System.out.println("\n[TEST 1] Đa mẫu trên chuỗi kinh điển 'ushers' với 4 mẫu:");
        String text1 = "ushers";
        String[] patterns1 = {"he", "she", "his", "hers"};
        // "ushers":
        // she tại index 1 (ushers: 1..3 là 'she')
        // he  tại index 2 (ushers: 2..3 là 'he')
        // hers tại index 2 (ushers: 2..5 là 'hers')
        List<MatchRecord> actual1 = findAllMatches(text1, patterns1, 2);
        System.out.println("Text: " + text1);
        System.out.println("Matches tìm thấy: " + actual1);
        boolean t1Pass = actual1.size() == 3;
        System.out.println("Kết quả TEST 1: " + (t1Pass ? "PASS" : "FAIL"));
        allPassed &= t1Pass;

        // Test 2: Độc lập với block size trên k mẫu (B = 1, 2, 3, 7, 1024)
        System.out.println("\n[TEST 2] Kiểm tra tính bất biến qua các kích thước block B (B=1,2,3,7,1024):");
        String text2 = "abababab";
        String[] patterns2 = {"abab", "bab", "xyz"};
        int[] blockSizes = {1, 2, 3, 7, 1024};
        List<MatchRecord> base2 = findAllMatches(text2, patterns2, 1024);
        boolean t2Pass = true;
        for (int B : blockSizes) {
            List<MatchRecord> res = findAllMatches(text2, patterns2, B);
            boolean ok = base2.equals(res);
            System.out.printf("  - Block B = %4d -> Số matches: %d [%s]\n", B, res.size(), ok ? "PASS" : "FAIL");
            if (!ok) t2Pass = false;
        }
        allPassed &= t2Pass;

        // Test 3: Tích hợp ChecksumConsumer
        System.out.println("\n[TEST 3] Kiểm tra tích hợp ChecksumConsumer:");
        ChecksumConsumer consumer = new ChecksumConsumer();
        byte[] tBytes = text2.getBytes(StandardCharsets.ISO_8859_1);
        byte[][] pBytes = new byte[patterns2.length][];
        for (int i = 0; i < patterns2.length; i++) {
            pBytes[i] = patterns2[i].getBytes(StandardCharsets.ISO_8859_1);
        }
        MultiPatternMatcher matcher = new KkmpBlockMatcher();
        try (InputStream in = new ByteArrayInputStream(tBytes)) {
            matcher.search(in, pBytes, 4, consumer);
        }
        System.out.println("Consumer output: " + consumer);
        boolean t3Pass = consumer.getMatchCount() == base2.size() && consumer.getChecksum() != 0;
        System.out.println("Kết quả TEST 3: " + (t3Pass ? "PASS" : "FAIL"));
        allPassed &= t3Pass;

        System.out.println("\n========================================================================");
        if (allPassed) {
            System.out.println(">>> TẤT CẢ CÁC BÀI TEST k-KMP ĐỀU ĐẠT (100% PASS)! <<<");
        } else {
            System.out.println(">>> CÓ BÀI TEST k-KMP THẤT BẠI! <<<");
        }
        System.out.println("========================================================================");
    }
}
