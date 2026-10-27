package test;

import algorithm.BruteForceMatcher;
import algorithm.ChecksumConsumer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * BlockBoundaryTest - Bộ kiểm thử ranh giới block.
 * Đáp ứng Đầu việc 2.3 theo PHAN_CONG.md và Mục 5 Quy định Đề tài 3:
 * - Chạy kiểm thử Brute Force với các kích thước block cực nhỏ: B in {1, 2, 3, 7 bytes}.
 * - Đối chiếu với kết quả khi quét với block chuẩn B = 1 MiB.
 * - Kết quả count và check phải trùng khớp 100%, không bị sót hoặc đếm trùng mẫu vắt qua block.
 */
public class BlockBoundaryTest {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("       KIỂM THỬ RANH GIỚI BLOCK (BlockBoundaryTest) - CẶP 1");
        System.out.println("========================================================================");

        boolean ok1 = testFixedBoundary();
        boolean ok2 = testRandomBoundary();

        if (ok1 && ok2) {
            System.out.println("\n==> TỔNG KẾT: 100% CÁC TEST RANH GIỚI BLOCK ĐÃ ĐẠT (PASS)!");
        } else {
            System.err.println("\n==> TỔNG KẾT: CÓ TEST THẤT BẠI (FAIL)!");
            System.exit(1);
        }
    }

    /**
     * Test trên chuỗi cố định chứa các mẫu nằm vắt qua các vị trí block 1, 2, 3, 7 byte.
     */
    private static boolean testFixedBoundary() {
        System.out.println("\n[1] Kiểm thử ranh giới block trên chuỗi cố định:");
        byte[] text = "2026-10-27 ERROR [component_12] Database connection failure pool timeout ushers"
                .getBytes(StandardCharsets.ISO_8859_1);

        List<byte[]> patterns = new ArrayList<>();
        patterns.add("ERROR".getBytes(StandardCharsets.ISO_8859_1));
        patterns.add("connection".getBytes(StandardCharsets.ISO_8859_1));
        patterns.add("ushers".getBytes(StandardCharsets.ISO_8859_1));
        patterns.add("he".getBytes(StandardCharsets.ISO_8859_1));

        // 1. Chạy mốc chuẩn với block lớn (1 MiB)
        BruteForceMatcher baseline = new BruteForceMatcher(1024 * 1024);
        baseline.compilePattern(patterns);
        ChecksumConsumer baseConsumer = new ChecksumConsumer();
        baseline.searchPattern(text, baseConsumer);

        long expCount = baseConsumer.getCount();
        long expCheck = baseConsumer.getChecksum();

        System.out.printf("  Mốc chuẩn Baseline (B = 1 MiB): count = %d, checksum = %d%n", expCount, expCheck);

        int[] tinyBlocks = {1, 2, 3, 7};
        boolean allPass = true;

        for (int b : tinyBlocks) {
            BruteForceMatcher matcher = new BruteForceMatcher(b);
            matcher.compilePattern(patterns);
            ChecksumConsumer c = new ChecksumConsumer();
            matcher.searchPattern(text, c);

            boolean match = (c.getCount() == expCount && c.getChecksum() == expCheck);
            if (match) {
                System.out.printf("  [PASS] Block B = %d bytes -> count: %d, checksum: %d (Khớp 100%%)%n",
                        b, c.getCount(), c.getChecksum());
            } else {
                System.err.printf("  [FAIL] Block B = %d bytes -> count: %d (exp %d), checksum: %d (exp %d)%n",
                        b, c.getCount(), expCount, c.getChecksum(), expCheck);
                allPass = false;
            }
        }
        return allPass;
    }

    /**
     * Test ngẫu nhiên 100 chuỗi với nhiều kích thước block khác nhau.
     */
    private static boolean testRandomBoundary() {
        System.out.println("\n[2] Kiểm thử ranh giới block trên 100 ca ngẫu nhiên:");
        Random rng = new Random(2026);
        boolean allPass = true;

        for (int t = 1; t <= 100; t++) {
            int textLen = 50 + rng.nextInt(200);
            byte[] text = new byte[textLen];
            rng.nextBytes(text);

            int patLen = 2 + rng.nextInt(8);
            byte[] pattern = new byte[patLen];
            // Lấy mẫu từ text để chắc chắn có ít nhất 1 match
            int start = rng.nextInt(textLen - patLen + 1);
            System.arraycopy(text, start, pattern, 0, patLen);

            List<byte[]> patterns = Arrays.asList(pattern);

            // Baseline
            BruteForceMatcher baseline = new BruteForceMatcher(1024 * 1024);
            baseline.compilePattern(patterns);
            ChecksumConsumer baseConsumer = new ChecksumConsumer();
            baseline.searchPattern(text, baseConsumer);

            // Thử nghiệm block B ngẫu nhiên từ 1 đến 9
            int b = 1 + rng.nextInt(9);
            BruteForceMatcher matcher = new BruteForceMatcher(b);
            matcher.compilePattern(patterns);
            ChecksumConsumer chunkConsumer = new ChecksumConsumer();
            matcher.searchPattern(text, chunkConsumer);

            if (chunkConsumer.getCount() != baseConsumer.getCount() ||
                chunkConsumer.getChecksum() != baseConsumer.getChecksum()) {
                System.err.printf("  [FAIL] Ca ngẫu nhiên #%d với B = %d: Lệch kết quả!%n", t, b);
                allPass = false;
                break;
            }
        }

        if (allPass) {
            System.out.println("  [PASS] 100/100 ca ngẫu nhiên với block B in [1..9] bytes cho kết quả trùng khớp tuyệt đối!");
        }
        return allPass;
    }
}
