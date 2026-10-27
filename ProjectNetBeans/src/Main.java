import runner.BenchmarkRunner;
import runner.MeasurementTask;

/**
 * Main - Điểm khởi động trung tâm (Single Entry Point) của đồ án CSD201.
 * Đáp ứng Đầu việc 1.3 theo PHAN_CONG.md và Mục 4.1 của quy định:
 * Tiếp nhận các tham số dòng lệnh CLI để điều hướng chương trình linh hoạt:
 * - java -jar <app.jar> --demo          : Chạy thử nghiệm nhanh bộ đo (< 15s)
 * - java -jar <app.jar> --benchmark     : Chạy bộ đo đầy đủ (hoặc kèm --rq1, --rq2, --rq3)
 * - java -jar <app.jar> --measure <args>: Tiến trình JVM con thực thi 1 lượt đo độc lập
 * - java -jar <app.jar> --help          : Hiển thị bảng hướng dẫn sử dụng
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || hasFlag(args, "--help") || hasFlag(args, "-h")) {
            printHelp();
            return;
        }

        // 1. Chế độ đo đơn lẻ cho JVM con (gọi từ BenchmarkRunner qua ProcessBuilder)
        if (hasFlag(args, "--measure")) {
            // Lọc bỏ cờ --measure và chuyển tiếp toàn bộ tham số còn lại sang MeasurementTask
            String[] subArgs = filterOutFlag(args, "--measure");
            MeasurementTask.main(subArgs);
            return;
        }

        // 2. Chế độ chạy bộ đo Benchmark điều phối (Cha)
        if (hasFlag(args, "--demo") || hasFlag(args, "--benchmark") || 
            hasFlag(args, "--rq1") || hasFlag(args, "--rq2") || hasFlag(args, "--rq3")) {
            BenchmarkRunner.main(args);
            return;
        }

        // 3. Nếu truyền tham số không nhận diện được
        System.err.println("Lỗi: Tham số không hợp lệ. Gõ '--help' để xem hướng dẫn.");
        printHelp();
    }

    private static void printHelp() {
        System.out.println("========================================================================");
        System.out.println("       HƯỚNG DẪN DÒNG LỆNH (CLI) - NHÓM 4 ĐỒ ÁN CSD201 FA26");
        System.out.println("========================================================================");
        System.out.println("Cách dùng: java -Xmx2g -jar <ProjectJarFile>.jar [TÙY_CHỌN]\n");
        System.out.println("Các tùy chọn chính:");
        System.out.println("  --demo             Chạy kiểm thử nhanh bộ đo (1 MiB - 4 MiB, dưới 15 giây)");
        System.out.println("  --benchmark        Chạy bộ đo tự động mặc định và xuất file CSV");
        System.out.println("  --rq1              Chạy trọn bộ ma trận thực nghiệm cho RQ1 (Brute Force vs KMP)");
        System.out.println("  --rq2              Chạy trọn bộ ma trận thực nghiệm cho RQ2 (Bảng chữ cái & T4)");
        System.out.println("  --rq3              Chạy trọn bộ ma trận thực nghiệm cho RQ3 (Aho-Corasick vs k-KMP)");
        System.out.println("  --measure <args>   Chạy lượt đo đơn lẻ trong JVM độc lập (do Runner gọi ngầm)");
        System.out.println("  --help, -h         Hiển thị bảng hướng dẫn này\n");
        System.out.println("Ví dụ:");
        System.out.println("  java -Xmx2g -jar Group4_CSD201.jar --demo");
        System.out.println("  java -Xmx2g -jar Group4_CSD201.jar --rq1");
        System.out.println("========================================================================");
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String a : args) {
            if (flag.equalsIgnoreCase(a)) return true;
        }
        return false;
    }

    private static String[] filterOutFlag(String[] args, String flagToRemove) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String a : args) {
            if (!flagToRemove.equalsIgnoreCase(a)) {
                result.add(a);
            }
        }
        return result.toArray(new String[0]);
    }
}
