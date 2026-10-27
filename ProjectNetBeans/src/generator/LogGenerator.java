package generator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * LogGenerator - Sinh log mô phỏng T1 theo đúng quy chuẩn Đề tài 3 (Mục 4 Bảng 4):
 * - Định dạng từng dòng: yyyy-MM-dd HH:mm:ss.SSS LEVEL [component] message
 * - Tỷ lệ LEVEL: INFO 80%, WARN 15%, ERROR 5%
 * - Gồm 50 components: [component_0] đến [component_49]
 * - Message: Lấy từ 200 câu mẫu kỹ thuật, chèn ID và mã lỗi E+4 chữ số (1.000 mã phân phối Zipf s=1)
 */
public class LogGenerator {

    private static final int NUM_COMPONENTS = 50;
    private static final int NUM_ERROR_CODES = 1000;
    private static final int NUM_SAMPLE_MESSAGES = 200;

    // 200 mẫu thông điệp log kỹ thuật hệ thống
    private static final String[] SAMPLE_MESSAGES = new String[NUM_SAMPLE_MESSAGES];

    static {
        for (int i = 0; i < NUM_SAMPLE_MESSAGES; i++) {
            switch (i % 8) {
                case 0:
                    SAMPLE_MESSAGES[i] = "User session authenticated successfully for client connection pool";
                    break;
                case 1:
                    SAMPLE_MESSAGES[i] = "Database connection query timeout during batch transaction commit";
                    break;
                case 2:
                    SAMPLE_MESSAGES[i] = "Network packet transmission dropped across network interface adapter";
                    break;
                case 3:
                    SAMPLE_MESSAGES[i] = "File system cache flush completed for transaction write-ahead log";
                    break;
                case 4:
                    SAMPLE_MESSAGES[i] = "Memory allocation threshold reached on heap garbage collection thread";
                    break;
                case 5:
                    SAMPLE_MESSAGES[i] = "Service discovery heartbeat ping sent to cluster supervisor node";
                    break;
                case 6:
                    SAMPLE_MESSAGES[i] = "SSL TLS handshake renegotiation requested from remote proxy endpoint";
                    break;
                default:
                    SAMPLE_MESSAGES[i] = "Worker thread executing background periodic maintenance and cleanup task";
                    break;
            }
        }
    }

    // Bảng xác suất tích lũy phân phối Zipf với tham số s = 1 trên 1.000 mã lỗi
    private static final double[] ZIPF_CDF = new double[NUM_ERROR_CODES];

    static {
        double sum = 0.0;
        for (int k = 1; k <= NUM_ERROR_CODES; k++) {
            sum += 1.0 / k; // s = 1
        }
        double cumulative = 0.0;
        for (int k = 1; k <= NUM_ERROR_CODES; k++) {
            cumulative += (1.0 / k) / sum;
            ZIPF_CDF[k - 1] = cumulative;
        }
    }

    /**
     * Lấy mã lỗi ngẫu nhiên theo phân phối Zipf s = 1 (từ 0 đến 999).
     */
    private static int sampleZipfErrorCode(Random rng) {
        double p = rng.nextDouble();
        int low = 0;
        int high = NUM_ERROR_CODES - 1;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (ZIPF_CDF[mid] >= p) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    /**
     * Chọn LEVEL theo tỷ lệ: INFO 80%, WARN 15%, ERROR 5%.
     */
    private static String sampleLevel(Random rng) {
        int r = rng.nextInt(100);
        if (r < 80) {
            return "INFO";
        } else if (r < 95) {
            return "WARN";
        } else {
            return "ERROR";
        }
    }

    /**
     * Sinh dòng log mô phỏng tiếp theo.
     */
    private static String generateLogLine(Random rng, long timestampMs, int lineId) {
        // yyyy-MM-dd HH:mm:ss.SSS
        long sec = (timestampMs / 1000) % 86400;
        long millis = timestampMs % 1000;
        long hour = sec / 3600;
        long minute = (sec % 3600) / 60;
        long second = sec % 60;

        String level = sampleLevel(rng);
        int componentId = rng.nextInt(NUM_COMPONENTS);
        int msgIndex = rng.nextInt(NUM_SAMPLE_MESSAGES);
        int errorCode = sampleZipfErrorCode(rng);

        return String.format("2026-10-27 %02d:%02d:%02d.%03d %-5s [component_%02d] %s (ReqID: %d, Code: E%04d)\n",
                hour, minute, second, millis, level, componentId, SAMPLE_MESSAGES[msgIndex], lineId, errorCode);
    }

    /**
     * Sinh mảng byte dung lượng chính xác sizeBytes.
     */
    public static byte[] generateBytes(long sizeBytes, long seed) {
        if (sizeBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Không thể tạo mảng byte trong bộ nhớ vượt quá Integer.MAX_VALUE. Dùng generateToFile cho file lớn.");
        }
        int total = (int) sizeBytes;
        byte[] buffer = new byte[total];
        Random rng = new Random(seed);

        long timestamp = 1793080000000L; // Mock base timestamp
        int lineId = 1;
        int written = 0;

        while (written < total) {
            String line = generateLogLine(rng, timestamp, lineId++);
            timestamp += 10 + rng.nextInt(90);
            byte[] lineBytes = line.getBytes(StandardCharsets.ISO_8859_1);
            int toCopy = Math.min(lineBytes.length, total - written);
            System.arraycopy(lineBytes, 0, buffer, written, toCopy);
            written += toCopy;
        }
        return buffer;
    }

    /**
     * Sinh luồng file trực tiếp ra đĩa (hỗ trợ file quy mô lớn 1 GiB không tốn heap RAM).
     */
    public static void generateToFile(File destination, long sizeBytes, long seed) throws IOException {
        if (destination.getParentFile() != null) {
            destination.getParentFile().mkdirs();
        }
        Random rng = new Random(seed);
        long timestamp = 1793080000000L;
        int lineId = 1;
        long written = 0;

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destination), 1024 * 1024)) {
            while (written < sizeBytes) {
                String line = generateLogLine(rng, timestamp, lineId++);
                timestamp += 10 + rng.nextInt(90);
                byte[] lineBytes = line.getBytes(StandardCharsets.ISO_8859_1);
                int toWrite = (int) Math.min((long) lineBytes.length, sizeBytes - written);
                out.write(lineBytes, 0, toWrite);
                written += toWrite;
            }
            out.flush();
        }
    }
}
