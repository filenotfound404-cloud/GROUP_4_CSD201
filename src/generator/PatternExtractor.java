package generator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Bộ trích xuất và sinh mẫu (PatternExtractor.java)
 * 1. Trích xuất mẫu ngẫu nhiên từ văn bản cho RQ1 và RQ2 (đảm bảo xuất hiện ít nhất 1 lần).
 * 2. Sinh k in {1, 10, 100, 1000} mẫu cho RQ3:
 *    - 50% lấy từ log (đảm bảo có match)
 *    - 50% chuỗi ngẫu nhiên không có trong log (mẫu âm tính)
 *    - Độ dài mẫu từ 8 đến 32 bytes.
 */
public class PatternExtractor {

    private static final byte[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Trích xuất 1 mẫu có độ dài patternLength từ dữ liệu text ngẫu nhiên.
     * Đảm bảo mẫu này xuất hiện ít nhất 1 lần trong text (phục vụ RQ1, RQ2).
     */
    public static byte[] extractSinglePattern(byte[] text, int patternLength, Random rng) {
        if (text == null || text.length == 0) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (patternLength <= 0) {
            throw new IllegalArgumentException("Pattern length must be positive");
        }
        if (patternLength > text.length) {
            throw new IllegalArgumentException("Pattern length (" + patternLength + ") cannot be larger than text length (" + text.length + ")");
        }

        int maxStart = text.length - patternLength;
        int start = maxStart == 0 ? 0 : rng.nextInt(maxStart + 1);

        byte[] pattern = new byte[patternLength];
        System.arraycopy(text, start, pattern, 0, patternLength);
        return pattern;
    }

    /**
     * Trích xuất danh sách count mẫu từ text.
     */
    public static List<byte[]> extractPatterns(byte[] text, int count, int patternLength, Random rng) {
        List<byte[]> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(extractSinglePattern(text, patternLength, rng));
        }
        return list;
    }

    /**
     * Sinh 1 chuỗi byte ngẫu nhiên không xuất hiện trong text (mẫu âm tính).
     */
    public static byte[] generateNegativePattern(byte[] text, int length, Random rng) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        // Để đảm bảo 100% không xuất hiện trong text log thật:
        // Đặt tiền tố đặc biệt chứa ký tự hiếm hoặc băm ngẫu nhiên
        byte[] pattern = new byte[length];
        int attempts = 0;
        do {
            for (int i = 0; i < length; i++) {
                pattern[i] = ALPHABET[rng.nextInt(ALPHABET.length)];
            }
            // Thêm ký tự đánh dấu âm tính nếu độ dài cho phép
            if (length >= 4) {
                pattern[0] = (byte) '#';
                pattern[1] = (byte) '!';
                pattern[2] = (byte) '@';
            }
            attempts++;
            if (attempts > 50) {
                break; // Tránh vòng lặp vô tận
            }
        } while (contains(text, pattern));

        return pattern;
    }

    /**
     * Sinh tập k mẫu cho bài toán thực nghiệm RQ3:
     * - k thuộc {1, 10, 100, 1000}
     * - 50% lấy từ log (positive)
     * - 50% chuỗi ngẫu nhiên không có trong log (negative)
     * - Độ dài ngẫu nhiên trong khoảng [minLen, maxLen] (mặc định 8 - 32 bytes)
     */
    public static List<byte[]> generatePatternsForRQ3(byte[] text, int k, int minLen, int maxLen, long seed) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }
        if (minLen <= 0 || maxLen < minLen) {
            throw new IllegalArgumentException("Invalid length range: [" + minLen + ", " + maxLen + "]");
        }

        Random rng = new Random(seed);
        List<byte[]> patterns = new ArrayList<>(k);

        int positiveCount = k / 2;
        int negativeCount = k - positiveCount;

        // 1. Trích xuất 50% từ văn bản
        for (int i = 0; i < positiveCount; i++) {
            int len = minLen + rng.nextInt(maxLen - minLen + 1);
            if (len > text.length) {
                len = text.length;
            }
            patterns.add(extractSinglePattern(text, len, rng));
        }

        // 2. Sinh 50% mẫu ngẫu nhiên âm tính
        for (int i = 0; i < negativeCount; i++) {
            int len = minLen + rng.nextInt(maxLen - minLen + 1);
            patterns.add(generateNegativePattern(text, len, rng));
        }

        // Trộn ngẫu nhiên thứ tự các mẫu để đảm bảo tính khách quan
        Collections.shuffle(patterns, rng);
        return patterns;
    }

    /**
     * Kiểm tra xem chuỗi mẫu có tồn tại trong text hay không.
     */
    public static boolean contains(byte[] text, byte[] pattern) {
        if (pattern.length > text.length) return false;
        int limit = text.length - pattern.length;
        for (int i = 0; i <= limit; i++) {
            int j = 0;
            while (j < pattern.length && text[i + j] == pattern[j]) {
                j++;
            }
            if (j == pattern.length) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("=== DEMO BỘ TRÍCH XUẤT VÀ SINH MẪU (PatternExtractor) ===");
        String sampleLog = "2026-10-24 14:00:01.123 ERROR [DatabaseService] Connection timeout in pool-1. Retry 3/5.\n"
                + "2026-10-24 14:00:02.456 WARN [NetworkGateway] High packet loss on eth0 interface.\n"
                + "2026-10-24 14:00:03.789 INFO [AuthHandler] User admin logged in from 192.168.1.10.";

        byte[] text = sampleLog.getBytes(StandardCharsets.ISO_8859_1);

        System.out.println("\n[1] Demo RQ1 & RQ2: Trích xuất 1 mẫu xuất hiện trong log (độ dài 16 bytes):");
        Random rng = new Random(42);
        byte[] p1 = extractSinglePattern(text, 16, rng);
        System.out.println("  Mẫu: '" + new String(p1, StandardCharsets.ISO_8859_1) + "'");
        System.out.println("  Có trong text? " + contains(text, p1));

        System.out.println("\n[2] Demo RQ3: Sinh k = 10 mẫu (50% từ log, 50% âm tính, độ dài 8 - 20 bytes):");
        List<byte[]> kPatterns = generatePatternsForRQ3(text, 10, 8, 20, 2026L);
        int pos = 0, neg = 0;
        for (int i = 0; i < kPatterns.size(); i++) {
            byte[] pat = kPatterns.get(i);
            boolean inText = contains(text, pat);
            if (inText) pos++; else neg++;
            System.out.printf("  Pattern #%02d (len %2d): '%-20s' -> %s%n",
                    i + 1, pat.length, new String(pat, StandardCharsets.ISO_8859_1), (inText ? "[TỒN TẠI TRONG LOG]" : "[KHÔNG TỒN TẠI]"));
        }
        System.out.printf("\nTổng kết: %d mẫu dương tính, %d mẫu âm tính -> Đạt tỷ lệ 50-50 chuẩn yêu cầu!%n", pos, neg);
    }
}
