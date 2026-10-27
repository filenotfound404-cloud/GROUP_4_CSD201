package kkmp.experiment;

/**
 * Đối tượng tiêu thụ (Consumer) kết quả khớp mẫu dùng chung cho toàn bộ dự án.
 * Hỗ trợ cả thuật toán đơn mẫu (Single-Pattern) và đa mẫu (Multi-Pattern).
 * 
 * Mục đích kỹ thuật:
 * 1. Chống JIT Dead-Code Elimination: Ngăn không cho trình biên dịch tối ưu (JIT Compiler)
 *    của JVM nhận định vòng lặp tìm kiếm là vô hiệu và loại bỏ (khiến thời gian đo sai về 0).
 * 2. Triệt tiêu chi phí I/O: Không sử dụng System.out.println hay ghi đĩa trong luồng đo hiệu năng,
 *    đảm bảo đo đạc phản ánh trung thực tốc độ thuần của thuật toán.
 * 3. Tính mã kiểm tra (Checksum) tích lũy: Giúp đối chiếu tính đúng tuyệt đối giữa các thuật toán
 *    (ví dụ: KMP, k-KMP, Aho-Corasick và Brute Force phải cho ra cùng checksum trên cùng dữ liệu).
 */
public class ChecksumConsumer {

    private long matchCount = 0;
    private long checksum = 0;

    public ChecksumConsumer() {
    }

    /**
     * Phương thức callback cho thuật toán đơn mẫu (Single-Pattern).
     * 
     * @param byteOffset vị trí bắt đầu của mẫu trong toàn bộ luồng dữ liệu (0-indexed byte offset)
     */
    public void onMatch(long byteOffset) {
        matchCount++;
        checksum = 31L * checksum + byteOffset;
    }

    /**
     * Phương thức callback cho thuật toán đa mẫu (Multi-Pattern: k-KMP, Aho-Corasick).
     * 
     * @param patternIndex chỉ số (ID) của mẫu khớp (0 đến k - 1)
     * @param byteOffset vị trí bắt đầu của mẫu trong toàn bộ luồng dữ liệu (0-indexed byte offset)
     */
    public void onMatch(int patternIndex, long byteOffset) {
        matchCount++;
        // Kết hợp patternIndex và byteOffset vào checksum bằng các số nguyên tố lớn
        long term = ((long) patternIndex + 1L) * 1_000_003L + byteOffset;
        checksum = 31L * checksum + term;
    }

    /**
     * Đặt lại trạng thái bộ tích lũy về 0.
     */
    public void reset() {
        matchCount = 0;
        checksum = 0;
    }

    /**
     * Lấy tổng số lần mẫu xuất hiện trong file.
     * 
     * @return số lượng matches
     */
    public long getMatchCount() {
        return matchCount;
    }

    /**
     * Lấy mã checksum tích lũy của toàn bộ các vị trí tìm thấy.
     * 
     * @return giá trị checksum dạng long
     */
    public long getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        return "ChecksumConsumer{matches=" + matchCount + ", checksum=" + checksum + "}";
    }
}
