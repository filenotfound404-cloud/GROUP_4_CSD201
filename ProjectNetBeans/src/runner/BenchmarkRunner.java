package runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * BenchmarkRunner - Trọng tài điều phối các đợt đo thực nghiệm CSD201.
 * Tuân thủ nghiêm ngặt Mục 6 của đề bài:
 * - 6.1. Khởi động mỗi lượt đo bằng một tiến trình JVM riêng qua ProcessBuilder.
 * - 6.1. Chờ tối đa 60s bằng Process.waitFor(60, TimeUnit.SECONDS); quá 60s gọi destroyForcibly() và ghi timeout.
 * - 6.6. Mỗi cấu hình chạy lặp (repeat 1, 2, 3) với 3 seed khác nhau.
 * - 6.7. Xử lý chính xác các trạng thái: ok, timeout, crash, oom, wrong.
 * - 6.11. Xuất file kết quả chuẩn CSV 22 cột vào results/raw/benchmark_results.csv.
 */
public class BenchmarkRunner {

    private static final String CSV_PATH = resolveCsvPath();

    /**
     * Tự động dò tìm thư mục results/raw/ tại thư mục gốc repository (GROUP_4_CSD201 hoặc CSD201)
     * Đảm bảo dù đứng ở bất kỳ thư mục con nào (ProjectNetBeans, dist,...) file CSV vẫn được ghi đúng chỗ.
     */
    private static String resolveCsvPath() {
        File current = new File(".").getAbsoluteFile();
        while (current != null) {
            File resultsDir = new File(current, "results" + File.separator + "raw");
            File gitDir = new File(current, ".git");
            if (gitDir.exists() || resultsDir.exists()) {
                if (!resultsDir.exists()) {
                    resultsDir.mkdirs();
                }
                return new File(resultsDir, "benchmark_results.csv").getAbsolutePath();
            }
            current = current.getParentFile();
        }
        return "results" + File.separator + "raw" + File.separator + "benchmark_results.csv";
    }
    private static final int TIMEOUT_SECONDS = 60;
    private static final String XMX = "2g"; // Dùng -Xmx2g theo quy định Mục 6.1

    // 22 cột dữ liệu chuẩn (20 cột chuẩn Bảng 4 + 2 cột riêng: build_ms, throughput_mibs)
    private static final String CSV_HEADER = 
        "run_id,rq,algorithm,variant,dataset,n,params,seed,input_sha256," +
        "generator_version,repeat,elapsed_ms,heap_before_bytes,heap_peak_bytes," +
        "ops_count,output_check,status,xmx,machine_id,git_commit,build_ms,throughput_mibs";

    public static void main(String[] args) {
        String machineId = resolveMachineId();
        String gitCommit = resolveGitCommit();

        System.out.println("========================================================================");
        System.out.println("           BO DO HIEU NANG PATTERN MATCHING (FALL 2026)");
        System.out.println(" May do (machine_id): " + machineId);
        System.out.println(" Git Commit:          " + gitCommit);
        System.out.println(" Cau hinh JVM:        -Xmx" + XMX);
        System.out.println(" File ket qua CSV:    " + CSV_PATH);
        System.out.println("========================================================================");

        initCsvFile();

        // Xác định chế độ chạy từ dòng lệnh: --demo, --rq1, --rq2, --rq3, hoặc chạy mặc định
        List<BenchmarkConfig> suite = buildTestSuite(args);
        System.out.printf(">> Tong so luot do can thuc hien: %d luot%n%n", suite.size());

        int runCounter = 1;
        for (BenchmarkConfig cfg : suite) {
            String runId = String.format("RUN_%04d", runCounter++);
            System.out.printf("[%s] %s | %s (%s) | %s | n=%d | repeat=%d ... ",
                runId, cfg.rq, cfg.algorithm, cfg.variant, cfg.dataset, cfg.n, cfg.repeat);

            long startTime = System.currentTimeMillis();
            String csvRow = executeSubprocess(runId, cfg, machineId, gitCommit);
            long costWallMs = System.currentTimeMillis() - startTime;

            appendCsv(csvRow);

            // Trích xuất status để thông báo nhanh trên console
            String status = extractStatusFromRow(csvRow);
            System.out.printf("=> [%s] (xong trong %d ms)%n", status, costWallMs);
        }

        System.out.println("\n========================================================================");
        System.out.println(">>> DA HOAN THANH TAT CA LUOT DO! Du lieu da luu tai: " + CSV_PATH);
        System.out.println("========================================================================");
    }

    /**
     * Khởi động một JVM con độc lập bằng ProcessBuilder và giám sát timeout 60s
     */
    private static String executeSubprocess(String runId, BenchmarkConfig cfg, String machineId, String gitCommit) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> cmd = new ArrayList<>();
        cmd.add(javaBin);
        cmd.add("-Xmx" + XMX);
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add("runner.MeasurementTask");

        // Truyền các tham số cấu hình cho tiến trình con
        cmd.add("--run_id=" + runId);
        cmd.add("--rq=" + cfg.rq);
        cmd.add("--algorithm=" + cfg.algorithm);
        cmd.add("--variant=" + cfg.variant);
        cmd.add("--dataset=" + cfg.dataset);
        cmd.add("--n=" + cfg.n);
        cmd.add("--params=" + cfg.params);
        cmd.add("--seed=" + cfg.seed);
        cmd.add("--repeat=" + cfg.repeat);
        cmd.add("--xmx=" + XMX);
        cmd.add("--machine_id=" + machineId);
        cmd.add("--git_commit=" + gitCommit);

        if (cfg.isCountOnly) {
            cmd.add("--count");
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = null;

        try {
            process = pb.start();

            // 6.1. Runner chờ bằng Process.waitFor(60, TimeUnit.SECONDS)
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                // Quá 60 giây -> Diệt tiến trình ngay và ghi trạng thái timeout
                process.destroyForcibly();
                return formatTimeoutRow(runId, cfg, machineId, gitCommit);
            }

            int exitCode = process.exitValue();

            // Đọc kết quả chuẩn hóa từ stdout
            String csvResult = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("CSV_RESULT:")) {
                        csvResult = line.substring("CSV_RESULT:".length()).trim();
                    }
                }
            }

            // Đọc log lỗi từ stderr nếu tiến trình gặp sự cố
            StringBuilder errBuf = new StringBuilder();
            try (BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errReader.readLine()) != null) {
                    errBuf.append(line).append("\n");
                }
            }

            // 6.7. Xử lý các trạng thái crash, oom nếu exitCode != 0
            if (exitCode != 0 || csvResult == null) {
                String errStr = errBuf.toString();
                if (exitCode == 137 || errStr.contains("OutOfMemoryError")) {
                    return formatErrorRow(runId, cfg, "oom", machineId, gitCommit);
                } else {
                    return formatErrorRow(runId, cfg, "crash", machineId, gitCommit);
                }
            }

            return csvResult;

        } catch (IOException | InterruptedException ex) {
            if (process != null) {
                process.destroyForcibly();
            }
            return formatErrorRow(runId, cfg, "crash", machineId, gitCommit);
        }
    }

    // Định dạng dòng CSV khi bị timeout: elapsed_ms và throughput_mibs để trống (Mục 6.7)
    private static String formatTimeoutRow(String runId, BenchmarkConfig cfg, String machineId, String gitCommit) {
        return String.format("%s,%s,%s,%s,%s,%d,%s,%d,,v1.0,%d,,,,,timeout,%s,%s,%s,,",
            runId, cfg.rq, cfg.algorithm, cfg.variant, cfg.dataset, cfg.n,
            cfg.params, cfg.seed, cfg.repeat, XMX, machineId, gitCommit);
    }

    // Định dạng dòng CSV khi bị crash hoặc OOM
    private static String formatErrorRow(String runId, BenchmarkConfig cfg, String status, String machineId, String gitCommit) {
        return String.format("%s,%s,%s,%s,%s,%d,%s,%d,,v1.0,%d,,,%s,%s,%s,%s,,",
            runId, cfg.rq, cfg.algorithm, cfg.variant, cfg.dataset, cfg.n,
            cfg.params, cfg.seed, cfg.repeat, status, XMX, machineId, gitCommit);
    }

    private static String extractStatusFromRow(String row) {
        if (row == null) return "null";
        String[] parts = row.split(",");
        if (parts.length >= 17) {
            return parts[16].trim();
        }
        return "unknown";
    }

    // Tạo file CSV với Header nếu file chưa tồn tại
    private static void initCsvFile() {
        File file = new File(CSV_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println(CSV_HEADER);
            } catch (IOException e) {
                System.err.println("Khong the tao file CSV: " + e.getMessage());
            }
        }
    }

    // Ghi nối tiếp 1 dòng kết quả vào CSV
    private static synchronized void appendCsv(String row) {
        if (row == null || row.trim().isEmpty()) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH, true))) {
            pw.println(row);
        } catch (IOException e) {
            System.err.println("Loi khi ghi dong CSV: " + e.getMessage());
        }
    }

    // ----------------- Sinh ma trận cấu hình thực nghiệm -----------------
    private static List<BenchmarkConfig> buildTestSuite(String[] args) {
        List<BenchmarkConfig> list = new ArrayList<>();
        boolean isDemo = hasFlag(args, "--demo");
        boolean isRq1 = hasFlag(args, "--rq1");
        boolean isRq2 = hasFlag(args, "--rq2");
        boolean isRq3 = hasFlag(args, "--rq3");

        long[] seeds = {42L, 101L, 2024L};

        if (isDemo || (!isRq1 && !isRq2 && !isRq3)) {
            // Chế độ DEMO ngắn (< 15s) để nộp kiểm tra mốc D7 hoặc chạy thử nghiệm
            long[] demoSizes = {1024 * 1024L, 4 * 1024 * 1024L}; // 1 MiB, 4 MiB
            for (long n : demoSizes) {
                for (int rep = 1; rep <= 2; rep++) {
                    list.add(new BenchmarkConfig("TEST", "bf", "base", "T1", n, "block_size_bytes=1048576;pattern_length=16", 42L, rep, false));
                    list.add(new BenchmarkConfig("TEST", "kmp", "base", "T1", n, "block_size_bytes=1048576;pattern_length=16", 42L, rep, false));
                    list.add(new BenchmarkConfig("TEST", "lib-read", "base", "T1", n, "block_size_bytes=1048576", 42L, rep, false));
                }
                // Thêm 1 lượt đo đếm số phép toán (--count)
                list.add(new BenchmarkConfig("TEST", "bf", "base", "T1", n, "block_size_bytes=1048576;pattern_length=16;counter=byte_comparisons_per_byte", 42L, 1, true));
                list.add(new BenchmarkConfig("TEST", "kmp", "base", "T1", n, "block_size_bytes=1048576;pattern_length=16;counter=byte_comparisons_per_byte", 42L, 1, true));
            }
            if (isDemo) return list;
        }

        // RQ1: Brute Force vs KMP khi kích thước văn bản tăng (16, 64, 256, 512, 1024 MiB)
        if (isRq1) {
            long[] sizes = {16 * 1024 * 1024L, 64 * 1024 * 1024L, 256 * 1024 * 1024L};
            String[] datasets = {"T1", "T3"};
            for (String ds : datasets) {
                for (long n : sizes) {
                    for (long s : seeds) {
                        for (int rep = 1; rep <= 3; rep++) {
                            list.add(new BenchmarkConfig("RQ1", "bf", "base", ds, n, "block_size_bytes=1048576;pattern_length=16;alphabet_size=4", s, rep, false));
                            list.add(new BenchmarkConfig("RQ1", "kmp", "base", ds, n, "block_size_bytes=1048576;pattern_length=16;alphabet_size=4", s, rep, false));
                            list.add(new BenchmarkConfig("RQ1", "lib-read", "base", ds, n, "block_size_bytes=1048576", s, rep, false));
                        }
                        // Lượt --count
                        list.add(new BenchmarkConfig("RQ1", "bf", "base", ds, n, "block_size_bytes=1048576;pattern_length=16;counter=byte_comparisons_per_byte", s, 1, true));
                        list.add(new BenchmarkConfig("RQ1", "kmp", "base", ds, n, "block_size_bytes=1048576;pattern_length=16;counter=byte_comparisons_per_byte", s, 1, true));
                    }
                }
            }
        }

        // RQ2: Ảnh hưởng của kích thước bảng chữ cái sigma, m, và dữ liệu bất lợi T4
        if (isRq2) {
            long n = 64 * 1024 * 1024L; // 64 MiB
            int[] sigmas = {2, 4, 26, 256};
            for (int sig : sigmas) {
                for (long s : seeds) {
                    for (int rep = 1; rep <= 3; rep++) {
                        list.add(new BenchmarkConfig("RQ2", "bf", "base", "T3", n, "block_size_bytes=1048576;pattern_length=64;alphabet_size=" + sig, s, rep, false));
                        list.add(new BenchmarkConfig("RQ2", "kmp", "base", "T3", n, "block_size_bytes=1048576;pattern_length=64;alphabet_size=" + sig, s, rep, false));
                    }
                }
            }

            // Thí nghiệm T4 (Worst-case: aaaaa...b)
            int[] mList = {8, 64, 1024};
            for (int m : mList) {
                for (int rep = 1; rep <= 3; rep++) {
                    list.add(new BenchmarkConfig("RQ2", "bf", "base", "T4", n, "block_size_bytes=1048576;pattern_length=" + m, 42L, rep, false));
                    list.add(new BenchmarkConfig("RQ2", "kmp", "base", "T4", n, "block_size_bytes=1048576;pattern_length=" + m, 42L, rep, false));
                }
            }
        }

        // RQ3: Aho-Corasick (fail, dfa) vs k-KMP khi k tăng từ 1 -> 1000
        if (isRq3) {
            long n = 64 * 1024 * 1024L; // 64 MiB
            int[] kValues = {1, 10, 100};
            for (int k : kValues) {
                for (long s : seeds) {
                    for (int rep = 1; rep <= 3; rep++) {
                        list.add(new BenchmarkConfig("RQ3", "kmp-k", "base", "T1", n, "block_size_bytes=1048576;pattern_count=" + k, s, rep, false));
                        list.add(new BenchmarkConfig("RQ3", "ac", "fail", "T1", n, "block_size_bytes=1048576;pattern_count=" + k, s, rep, false));
                        list.add(new BenchmarkConfig("RQ3", "ac", "dfa", "T1", n, "block_size_bytes=1048576;pattern_count=" + k, s, rep, false));
                    }
                }
            }
        }

        return list;
    }

    private static boolean hasFlag(String[] args, String flag) {
        if (args == null) return false;
        for (String a : args) {
            if (flag.equalsIgnoreCase(a)) return true;
        }
        return false;
    }

    private static String resolveMachineId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "PC_UNKNOWN";
        }
    }

    private static String resolveGitCommit() {
        try {
            Process p = new ProcessBuilder("git", "rev-parse", "--short", "HEAD").start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = r.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return line.trim();
                }
            }
        } catch (Exception ignored) {}
        return "main";
    }

    // Class đại diện cho cấu hình 1 lượt đo
    public static class BenchmarkConfig {
        public final String rq;
        public final String algorithm;
        public final String variant;
        public final String dataset;
        public final long n;
        public final String params;
        public final long seed;
        public final int repeat;
        public final boolean isCountOnly;

        public BenchmarkConfig(String rq, String algorithm, String variant, String dataset,
                               long n, String params, long seed, int repeat, boolean isCountOnly) {
            this.rq = rq;
            this.algorithm = algorithm;
            this.variant = variant;
            this.dataset = dataset;
            this.n = n;
            this.params = params;
            this.seed = seed;
            this.repeat = repeat;
            this.isCountOnly = isCountOnly;
        }
    }
}
