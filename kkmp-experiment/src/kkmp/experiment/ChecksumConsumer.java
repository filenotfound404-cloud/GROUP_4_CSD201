package kkmp.experiment;

/**
 * Đối tượng tiêu thụ (Consumer) kết quả khớp mẫu dùng chung cho toàn bộ dự án.
 * 
 * Mục đích kỹ thuật:
 * 1. Chống JIT Dead-Code Elimination: Ngăn không cho trình biên dịch tối ưu (JIT Compiler)
 *    của JVM nhận định vòng lặp tìm kiếm là vô hiệu và loại bỏ (khiến thời gian đo sai về 0).
 * 2. Triệt tiêu chi phí I/O: Không sử dụng System.out.println hay ghi đĩa trong luồng đo hiệu năng,
 *    đảm bảo đo đạc phản ánh trung thực tốc độ thuần của thuật toán.
 * 3. Tính mã kiểm tra (Checksum) tích lũy: Giúp đối chiếu tính đúng tuyệt đối giữa các thuật toán
 *    (ví dụ: KMP và Brute Force phải cho ra cùng checksum và cùng số lượng match trên cùng một file log).
 */
public class ChecksumConsumer {

    private long matchCount = 0;
    private long checksum = 0;

    public ChecksumConsumer() {
    }

    /**
     * Phương thức callback được gọi mỗi khi thuật toán tìm thấy một lần xuất hiện của mẫu.
     * 
     * @param byteOffset vị trí bắt đầu của mẫu trong toàn bộ luồng dữ liệu (0-indexed byte offset)
     */
    public void onMatch(long byteOffset) {
        matchCount++;
        // Cập nhật mã kiểm tra tích lũy bằng hằng số nguyên tố 31 (chuẩn tính hash trong Java)
        checksum = 31L * checksum + byteOffset;
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
