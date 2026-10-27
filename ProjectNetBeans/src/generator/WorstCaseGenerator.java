package generator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * WorstCaseGenerator - Sinh tập dữ liệu bất lợi T4 theo đúng Mục 4 Bảng 4:
 * - Toàn bộ văn bản là byte 'a'
 * - Mẫu gồm m - 1 byte 'a' và 1 byte 'b' (aaaa...b)
 * - Làm cho Brute Force bị thoái hóa về O(n * m) trong khi KMP giữ O(n).
 */
public class WorstCaseGenerator {

    /**
     * Sinh mảng byte văn bản bất lợi T4 (toàn bộ là byte 'a').
     */
    public static byte[] generateTextBytes(long sizeBytes) {
        if (sizeBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Kích thước vượt quá Integer.MAX_VALUE");
        }
        int total = (int) sizeBytes;
        byte[] buffer = new byte[total];
        Arrays.fill(buffer, (byte) 'a');
        return buffer;
    }

    /**
     * Sinh mẫu bất lợi T4: m - 1 byte 'a' và kết thúc bằng 1 byte 'b'.
     */
    public static byte[] generateWorstPattern(int patternLength) {
        if (patternLength <= 0) {
            throw new IllegalArgumentException("Pattern length must be positive");
        }
        byte[] pattern = new byte[patternLength];
        Arrays.fill(pattern, (byte) 'a');
        pattern[patternLength - 1] = (byte) 'b';
        return pattern;
    }

    /**
     * Ghi văn bản T4 trực tiếp ra file đĩa.
     */
    public static void generateTextToFile(File destination, long sizeBytes) throws IOException {
        if (destination.getParentFile() != null) {
            destination.getParentFile().mkdirs();
        }
        byte[] chunk = new byte[1024 * 1024]; // 1 MiB chunk
        Arrays.fill(chunk, (byte) 'a');

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destination), 1024 * 1024)) {
            long written = 0;
            while (written < sizeBytes) {
                int toWrite = (int) Math.min((long) chunk.length, sizeBytes - written);
                out.write(chunk, 0, toWrite);
                written += toWrite;
            }
            out.flush();
        }
    }
}
