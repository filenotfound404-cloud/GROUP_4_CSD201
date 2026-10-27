package algorithm.bruteforce;

import algorithm.PatternMatching;
import algorithm.ChecksumConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * BruteForceMatcher - Thuật toán tìm kiếm chuỗi vét cạn (Brute Force).
 * Đáp ứng Đầu việc 1.1 theo PHAN_CONG.md và Mục 3.1 & 3.2 Quy định Đề tài 3:
 * - So khớp trượt từng byte từ trái sang phải, dừng ngay ở byte sai khớp đầu tiên.
 * - Hỗ trợ đọc luồng/dữ liệu theo từng khối block B (mặc định 1 MiB).
 * - Sử dụng kỹ thuật đệm giữ lại m - 1 byte cuối của block trước nối vào đầu block sau
 *   để không bị bỏ sót các lần xuất hiện của mẫu vắt qua ranh giới 2 block kề nhau.
 * - Đo số lượng phép so sánh byte phục vụ chỉ số ops_count (--count).
 */
public class BruteForceMatcher implements PatternMatching {

    private List<byte[]> patterns;
    private int blockSize = 1024 * 1024; // Mặc định 1 MiB theo Mục 3.2
    private long comparisonsCount = 0;

    public BruteForceMatcher() {
    }

    public BruteForceMatcher(int blockSize) {
        if (blockSize > 0) {
            this.blockSize = blockSize;
        }
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mẫu không được rỗng");
        }
        for (byte[] p : patterns) {
            if (p == null || p.length == 0) {
                throw new IllegalArgumentException("Mẫu rỗng không hợp lệ theo Mục 3.1");
            }
        }
        this.patterns = patterns;
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (patterns == null || patterns.isEmpty()) {
            return;
        }
        if (data == null || data.length == 0) {
            return;
        }

        // Lặp qua từng mẫu độc lập
        for (int pIdx = 0; pIdx < patterns.size(); pIdx++) {
            byte[] pattern = patterns.get(pIdx);
            int m = pattern.length;
            if (m == 0 || data.length < m) {
                continue;
            }

            byte[] overlap = new byte[m - 1];
            int overlapLen = 0;
            long globalOffset = 0;
            int pos = 0;

            while (pos < data.length) {
                int readLen = Math.min(blockSize, data.length - pos);
                int combinedLen = overlapLen + readLen;
                byte[] block = new byte[combinedLen];

                if (overlapLen > 0) {
                    System.arraycopy(overlap, 0, block, 0, overlapLen);
                }
                System.arraycopy(data, pos, block, overlapLen, readLen);

                int limit = combinedLen - m;
                for (int i = 0; i <= limit; i++) {
                    boolean match = true;
                    for (int j = 0; j < m; j++) {
                        comparisonsCount++;
                        if (block[i + j] != pattern[j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        consumer.onMatch(globalOffset - overlapLen + i, pIdx);
                    }
                }

                // Lưu lại m - 1 byte cuối cho block sau
                int toKeep = Math.min(m - 1, combinedLen);
                System.arraycopy(block, combinedLen - toKeep, overlap, 0, toKeep);
                overlapLen = toKeep;

                pos += readLen;
                globalOffset += readLen;
            }
        }
    }

    /**
     * Tìm kiếm trực tiếp trên luồng InputStream theo từng khối block B.
     */
    public void searchStream(InputStream in, ChecksumConsumer consumer) throws IOException {
        if (patterns == null || patterns.isEmpty() || in == null) {
            return;
        }

        for (int pIdx = 0; pIdx < patterns.size(); pIdx++) {
            byte[] pattern = patterns.get(pIdx);
            int m = pattern.length;
            if (m == 0) continue;

            byte[] overlap = new byte[m - 1];
            int overlapLen = 0;
            long globalOffset = 0;

            byte[] readBuffer = new byte[blockSize];
            int bytesRead;

            while ((bytesRead = in.read(readBuffer)) != -1) {
                int combinedLen = overlapLen + bytesRead;
                byte[] block = new byte[combinedLen];

                if (overlapLen > 0) {
                    System.arraycopy(overlap, 0, block, 0, overlapLen);
                }
                System.arraycopy(readBuffer, 0, block, overlapLen, bytesRead);

                int limit = combinedLen - m;
                for (int i = 0; i <= limit; i++) {
                    boolean match = true;
                    for (int j = 0; j < m; j++) {
                        comparisonsCount++;
                        if (block[i + j] != pattern[j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        consumer.onMatch(globalOffset - overlapLen + i, pIdx);
                    }
                }

                int toKeep = Math.min(m - 1, combinedLen);
                System.arraycopy(block, combinedLen - toKeep, overlap, 0, toKeep);
                overlapLen = toKeep;

                globalOffset += bytesRead;
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "bf";
    }

    @Override
    public void reset() {
        this.comparisonsCount = 0;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public void setBlockSize(int blockSize) {
        if (blockSize > 0) {
            this.blockSize = blockSize;
        }
    }
}
