package kkmp.experiment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface dùng chung cho tất cả các thuật toán so khớp mẫu (Pattern Matching)
 * trong đồ án CSD201 (Brute Force, KMP, k-KMP, Aho-Corasick).
 * 
 * Các thuật toán triển khai interface này để phục vụ đo kiểm Benchmark tự động
 * và kiểm tra tính đúng đắn (Correctness Verification) qua Checksum.
 */
public interface PatternMatcher {

    /**
     * Tên định danh của thuật toán (ví dụ: "KMP", "BruteForce", "AhoCorasick").
     * 
     * @return tên thuật toán dạng chuỗi
     */
    String getName();

    /**
     * Thực hiện tìm kiếm mẫu pattern trong luồng dữ liệu input theo từng block buffer.
     * 
     * @param input luồng dữ liệu nhị phân cần quét (InputStream từ file log)
     * @param pattern mảng byte của mẫu cần tìm kiếm
     * @param blockSize kích thước khối bộ đệm (byte) mỗi lần đọc từ input (B bytes)
     * @param consumer đối tượng tiếp nhận vị trí khớp và tích lũy checksum
     * @return tổng số lần xuất hiện của mẫu trong toàn bộ luồng
     * @throws IOException khi phát sinh lỗi đọc luồng dữ liệu
     */
    long search(InputStream input, byte[] pattern, int blockSize, ChecksumConsumer consumer) throws IOException;
}
