package generator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * PatternExtractor - Bộ trích xuất và sinh mẫu thử nghiệm:
 * 1. Trích xuất mẫu ngẫu nhiên từ văn bản cho RQ1 và RQ2 (đảm bảo xuất hiện ít nhất 1 lần).
 * 2. Sinh k in {1, 10, 100, 1000} mẫu cho RQ3:
 *    - 50% lấy từ log (đảm bảo dương tính)
 *    - 50% chuỗi ngẫu nhiên không có trong log (mẫu âm tính)
 *    - Độ dài mẫu từ 8 đến 32 bytes.
 */
public class PatternExtractor {

    private static final byte[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Trích xuất 1 mẫu có độ dài patternLength từ dữ liệu text.
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
     * Sinh 1 chuỗi byte ngẫu nhiên không xuất hiện trong text (mẫu âm tính).
     */
    public static byte[] generateNegativePattern(byte[] text, int length, Random rng) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        byte[] pattern = new byte[length];
        int attempts = 0;
        do {
            for (int i = 0; i < length; i++) {
                pattern[i] = ALPHABET[rng.nextInt(ALPHABET.length)];
            }
            if (length >= 4) {
                pattern[0] = (byte) '#';
                pattern[1] = (byte) '!';
                pattern[2] = (byte) '@';
            }
            attempts++;
            if (attempts > 50) {
                break;
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

        Collections.shuffle(patterns, rng);
        return patterns;
    }

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
}
