package kkmp.experiment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface dùng chung cho các thuật toán tìm kiếm ĐA MẪU (Multi-Pattern Matching)
 * trong đồ án CSD201 (k-KMP, Aho-Corasick).
 * 
 * Cho phép tìm kiếm đồng thời k mẫu trong một lượt đọc luồng dữ liệu (Single Pass).
 */
public interface MultiPatternMatcher {

    /**
     * Tên định danh của thuật toán (ví dụ: "k-KMP", "AhoCorasick-Trie", "AhoCorasick-DFA").
     * 
     * @return tên thuật toán dạng chuỗi
     */
    String getName();

    /**
     * Thực hiện tìm kiếm đồng thời k mẫu trong luồng dữ liệu input theo từng block buffer.
     * 
     * @param input luồng dữ liệu nhị phân cần quét (InputStream từ file log)
     * @param patterns mảng 2 chiều chứa k mẫu byte cần tìm
     * @param blockSize kích thước khối bộ đệm (byte) mỗi lần đọc từ input (B bytes)
     * @param consumer đối tượng tiếp nhận ID mẫu, vị trí khớp và tích lũy checksum
     * @return tổng số lần xuất hiện của tất cả các mẫu trong toàn bộ luồng
     * @throws IOException khi phát sinh lỗi đọc luồng dữ liệu
     */
    long search(InputStream input, byte[][] patterns, int blockSize, ChecksumConsumer consumer) throws IOException;
}
