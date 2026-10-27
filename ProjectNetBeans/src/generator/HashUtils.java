package generator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashUtils - Tiện ích tính mã băm SHA-256 cho dữ liệu và file.
 * Đáp ứng yêu cầu ghi nhận tính toàn vẹn của dữ liệu trong bảng CSV (cột input_sha256).
 */
public class HashUtils {

    /**
     * Tính mã băm SHA-256 của mảng byte và trả về chuỗi Hex viết thường.
     *
     * @param data mảng byte cần băm
     * @return chuỗi hex SHA-256 64 ký tự
     */
    public static String sha256Hex(byte[] data) {
        if (data == null) {
            return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // Empty hash
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
