package algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * LibReadMatcher - Mốc tham chiếu đọc file nền (lib-read).
 * Đáp ứng Đầu việc 2.2 theo PHAN_CONG.md và Mục 3.4 Bảng 3 Quy định Đề tài 3:
 * - Chỉ đọc luồng file theo block B từ đĩa/cache mà không tìm kiếm mẫu.
 * - Mục đích: Đo chi phí đọc file nền để xác định trần I/O của phần cứng / OS cache.
 */
public class LibReadMatcher implements PatternMatching {

    private int blockSize = 1024 * 1024; // Mặc định 1 MiB

    public LibReadMatcher() {
    }

    public LibReadMatcher(int blockSize) {
        if (blockSize > 0) {
            this.blockSize = blockSize;
        }
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        // LibRead không cần tiền xử lý mẫu
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0) {
            return;
        }
        byte[] buf = new byte[blockSize];
        int pos = 0;
        while (pos < data.length) {
            int len = Math.min(blockSize, data.length - pos);
            System.arraycopy(data, pos, buf, 0, len);
            pos += len;
        }
    }

    /**
     * Đọc trực tiếp luồng InputStream theo từng khối block B mà không tìm kiếm.
     */
    public void searchStream(InputStream in, ChecksumConsumer consumer) throws IOException {
        if (in == null) return;
        byte[] buf = new byte[blockSize];
        while (in.read(buf) != -1) {
            // Chỉ đọc dữ liệu để đo băng thông I/O
        }
    }

    @Override
    public String getAlgorithmName() {
        return "lib-read";
    }

    @Override
    public void reset() {
        // Không có trạng thái nội tại cần làm mới
    }

    public void setBlockSize(int blockSize) {
        if (blockSize > 0) {
            this.blockSize = blockSize;
        }
    }
}
