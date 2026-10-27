package generator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * RandomByteGenerator - Sinh tập dữ liệu T3 theo đúng yêu cầu Mục 4 Bảng 4:
 * - Kích thước bảng chữ cái sigma thuộc {2, 4, 26, 256}.
 * - sigma ký hiệu đầu tiên của bảng chữ:
 *   + sigma = 2   : 'a', 'b'
 *   + sigma = 4   : 'a', 'b', 'c', 'd'
 *   + sigma = 26  : 'a' đến 'z'
 *   + sigma = 256 : toàn bộ 256 giá trị byte (0 đến 255)
 */
public class RandomByteGenerator {

    /**
     * Sinh mảng byte ngẫu nhiên theo sigma và seed.
     */
    public static byte[] generateBytes(long sizeBytes, int alphabetSize, long seed) {
        if (sizeBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Không thể tạo mảng byte trong bộ nhớ vượt quá Integer.MAX_VALUE. Dùng generateToFile cho file lớn.");
        }
        int total = (int) sizeBytes;
        byte[] buffer = new byte[total];
        Random rng = new Random(seed);

        fillBuffer(buffer, total, alphabetSize, rng);
        return buffer;
    }

    /**
     * Sinh file ngẫu nhiên ra đĩa mà không tốn nhiều heap RAM.
     */
    public static void generateToFile(File destination, long sizeBytes, int alphabetSize, long seed) throws IOException {
        if (destination.getParentFile() != null) {
            destination.getParentFile().mkdirs();
        }
        Random rng = new Random(seed);
        byte[] chunk = new byte[1024 * 1024]; // 1 MiB chunk

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destination), 1024 * 1024)) {
            long written = 0;
            while (written < sizeBytes) {
                int toWrite = (int) Math.min((long) chunk.length, sizeBytes - written);
                fillBuffer(chunk, toWrite, alphabetSize, rng);
                out.write(chunk, 0, toWrite);
                written += toWrite;
            }
            out.flush();
        }
    }

    private static void fillBuffer(byte[] buffer, int length, int sigma, Random rng) {
        if (sigma == 256) {
            rng.nextBytes(buffer);
            // nextBytes điền đầy buffer, nếu length < buffer.length thì chỉ cần điền tới length:
            return;
        }

        int s = Math.max(2, Math.min(256, sigma));
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) ('a' + rng.nextInt(s));
        }
    }
}
