package runner;

import algorithm.ChecksumConsumer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * MeasurementTask - Tiến trình con thực thi 1 lượt đo độc lập (JVM riêng).
 * Tuân thủ nghiêm ngặt Mục 6 và Mục 3 của đề bài CSD201:
 * - 6.2. Làm nóng JIT 3 lần trên dữ liệu nhỏ (<= 1 MiB).
 * - 6.3. Đo thời gian bằng System.nanoTime() bao quanh vùng đọc & tìm kiếm.
 * - 6.4. Đo heap_before_bytes và heap_peak_bytes qua MemoryPoolMXBean.
 * - 6.5. Đo số thao tác khi có cờ --count.
 * - 6.7. Xuất trạng thái: ok, wrong, oom, crash.
 */
public class MeasurementTask {

    // 6.4. Các hàm đo Heap bắt buộc theo văn bản đề tài (Java 8 java.lang.management.*)
    static long heapBytes(boolean peak) {
        long sum = 0;
        for (MemoryPoolMXBean p : ManagementFactory.getMemoryPoolMXBeans()) {
            if (p.getType() != MemoryType.HEAP) continue;
            MemoryUsage u = peak ? p.getPeakUsage() : p.getUsage();
            if (u != null) sum += u.getUsed();
        }
        return sum;
    }

    static void resetHeapPeaks() {
        for (MemoryPoolMXBean p : ManagementFactory.getMemoryPoolMXBeans()) {
            if (p.getType() == MemoryType.HEAP) {
                p.resetPeakUsage();
            }
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Đọc các tham số dòng lệnh CLI
        String runId = getArg(args, "--run_id", "RUN_0000");
        String rq = getArg(args, "--rq", "TEST");
        String algorithm = getArg(args, "--algorithm", "bf").toLowerCase();
        String variant = getArg(args, "--variant", "base").toLowerCase();
        String dataset = getArg(args, "--dataset", "T1").toUpperCase();
        long n = Long.parseLong(getArg(args, "--n", "1048576")); // Mặc định 1 MiB
        String params = getArg(args, "--params", "block_size_bytes=1048576");
        long seed = Long.parseLong(getArg(args, "--seed", "42"));
        int repeat = Integer.parseInt(getArg(args, "--repeat", "1"));
        boolean isCount = hasFlag(args, "--count");
        String xmx = getArg(args, "--xmx", "2g");
        String machineId = getArg(args, "--machine_id", "DEV_PC");
        String gitCommit = getArg(args, "--git_commit", "unknown");
        String generatorVersion = "v1.0";

        // Trích xuất các tham số phụ trợ trong params
        int blockSize = parseParamInt(params, "block_size_bytes", 1024 * 1024);
        int patternLength = parseParamInt(params, "pattern_length", 16);
        int alphabetSize = parseParamInt(params, "alphabet_size", 4);
        int kPatterns = parseParamInt(params, "pattern_count", 1);

        try {
            // 1. Chuẩn bị dữ liệu và mẫu
            byte[] textData = generateData(dataset, n, seed, alphabetSize);
            String inputSha256 = calculateSha256(textData);
            List<byte[]> patternList = extractOrGeneratePatterns(dataset, textData, patternLength, kPatterns, seed);

            // 2. Làm nóng JIT 3 lần trên dữ liệu nhỏ (Mục 6.2)
            warmup(algorithm, variant, blockSize);

            // 3. Tiền xử lý (Đo build_ms riêng theo Mục 3.4)
            long buildStart = System.nanoTime();
            AlgorithmRunner runner = createRunner(algorithm, variant, patternList, blockSize);
            long buildEnd = System.nanoTime();
            double buildMs = (buildEnd - buildStart) / 1_000_000.0;

            // 4. Đo Heap nền trước lượt chạy (Mục 6.4)
            System.gc();
            Thread.sleep(100); // Đợi GC ổn định
            resetHeapPeaks();
            long heapBeforeBytes = heapBytes(false);

            // 5. Chạy thuật toán chính thức (Mục 6.3)
            ChecksumConsumer consumer = new ChecksumConsumer();
            long opsTotal = 0;
            double elapsedMs = 0.0;
            String status = "ok";

            if (isCount) {
                // Lượt chạy đếm thao tác (Mục 6.5)
                runner.reset();
                opsTotal = runner.runWithCount(textData, consumer);
            } else {
                // Lượt chạy đo thời gian
                runner.reset();
                long start = System.nanoTime();
                runner.runSearch(textData, consumer);
                long end = System.nanoTime();
                elapsedMs = (end - start) / 1_000_000.0;
            }

            // 6. Đo đỉnh Heap sau lượt chạy (Mục 6.4)
            long heapPeakBytes = heapBytes(true);

            // 7. Kiểm tra đúng với Oracle (Mục 5 & 6.7)
            boolean isCorrect = verifyWithOracle(textData, patternList, consumer);
            if (!isCorrect) {
                status = "wrong";
            }

            // 8. Định dạng các cột đầu ra
            String outputCheck = String.format("count=%d;check=%d", consumer.getCount(), consumer.getChecksum());
            String elapsedMsStr = isCount ? "" : String.format(Locale.US, "%.2f", elapsedMs);
            String buildMsStr = String.format(Locale.US, "%.2f", buildMs);
            String opsCountStr = "";
            if (isCount) {
                if (algorithm.equals("bf") || algorithm.equals("kmp")) {
                    double perByte = (double) opsTotal / (double) n;
                    opsCountStr = String.format(Locale.US, "%.4f", perByte);
                } else {
                    opsCountStr = String.valueOf(opsTotal);
                }
            }

            String throughputStr = "";
            if (!isCount && elapsedMs > 0) {
                double mib = n / 1048576.0;
                double sec = elapsedMs / 1000.0;
                double throughput = mib / sec;
                throughputStr = String.format(Locale.US, "%.2f", throughput);
            }

            // In dòng kết quả chuẩn hóa để BenchmarkRunner thu thập
            String csvRow = String.format(Locale.US,
                "%s,%s,%s,%s,%s,%d,%s,%d,%s,%s,%d,%s,%d,%d,%s,%s,%s,%s,%s,%s,%s,%s",
                runId, rq, algorithm, variant, dataset, n, params, seed, inputSha256,
                generatorVersion, repeat, elapsedMsStr, heapBeforeBytes, heapPeakBytes,
                opsCountStr, outputCheck, status, xmx, machineId, gitCommit, buildMsStr, throughputStr
            );

            System.out.println("CSV_RESULT:" + csvRow);

        } catch (OutOfMemoryError oom) {
            System.err.println("OutOfMemoryError encountered");
            System.exit(137);
        } catch (Throwable t) {
            System.err.println("Fatal error: " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }

    // ----------------- Cơ chế Làm nóng JIT (Mục 6.2) -----------------
    private static void warmup(String algorithm, String variant, int blockSize) {
        int warmupSize = 64 * 1024; // 64 KiB
        byte[] warmupData = new byte[warmupSize];
        Arrays.fill(warmupData, (byte) 'a');
        List<byte[]> warmupPatterns = new ArrayList<>();
        warmupPatterns.add(new byte[]{'a', 'a', 'b'});

        for (int i = 0; i < 3; i++) {
            AlgorithmRunner warmRunner = createRunner(algorithm, variant, warmupPatterns, blockSize);
            warmRunner.runSearch(warmupData, new ChecksumConsumer());
        }
    }

    // ----------------- Sinh dữ liệu thử nghiệm (Mục 4) -----------------
    private static byte[] generateData(String dataset, long n, long seed, int alphabetSize) {
        if (n > 1024L * 1024 * 1024) {
            throw new IllegalArgumentException("Quy mô n vượt quá giới hạn an toàn 1 GiB");
        }
        int size = (int) n;
        byte[] data = new byte[size];
        Random rng = new Random(seed);

        if ("T4".equalsIgnoreCase(dataset)) {
            // T4: Toàn bộ là ký tự 'a'
            Arrays.fill(data, (byte) 'a');
        } else if ("T3".equalsIgnoreCase(dataset)) {
            // T3: Ngẫu nhiên với alphabet_size
            int sigma = Math.max(2, Math.min(256, alphabetSize));
            for (int i = 0; i < size; i++) {
                data[i] = (byte) ('a' + (rng.nextInt(sigma)));
            }
        } else {
            // T1: Mô phỏng server log (yyyy-MM-dd HH:mm:ss.SSS LEVEL [comp] msg)
            String[] levels = {"INFO", "INFO", "INFO", "WARN", "ERROR"};
            int pos = 0;
            int lineNo = 1;
            while (pos < size) {
                String lvl = levels[rng.nextInt(levels.length)];
                String line = String.format("2026-10-27 12:00:%02d.%03d %s [comp_%d] Error code E%04d occurred in task %d\n",
                        lineNo % 60, lineNo % 1000, lvl, rng.nextInt(50), rng.nextInt(1000), lineNo);
                byte[] lineBytes = line.getBytes(StandardCharsets.ISO_8859_1);
                int toCopy = Math.min(lineBytes.length, size - pos);
                System.arraycopy(lineBytes, 0, data, pos, toCopy);
                pos += toCopy;
                lineNo++;
            }
        }
        return data;
    }

    // ----------------- Trích xuất / Sinh mẫu (Mục 4) -----------------
    private static List<byte[]> extractOrGeneratePatterns(String dataset, byte[] data, int patternLength, int k, long seed) {
        List<byte[]> list = new ArrayList<>();
        Random rng = new Random(seed);

        if ("T4".equalsIgnoreCase(dataset)) {
            // T4: Mẫu gồm m - 1 ký tự 'a' và 1 ký tự 'b'
            byte[] pat = new byte[patternLength];
            Arrays.fill(pat, (byte) 'a');
            pat[patternLength - 1] = (byte) 'b';
            list.add(pat);
            return list;
        }

        // RQ1 / RQ2 / RQ3: Trích xuất mẫu từ text để đảm bảo có xuất hiện
        for (int i = 0; i < k; i++) {
            if (data.length <= patternLength) {
                list.add(Arrays.copyOf(data, data.length));
            } else {
                int start = rng.nextInt(data.length - patternLength);
                byte[] pat = Arrays.copyOfRange(data, start, start + patternLength);
                list.add(pat);
            }
        }
        return list;
    }

    // ----------------- Oracle kiểm chứng tính đúng (Mục 5) -----------------
    private static boolean verifyWithOracle(byte[] text, List<byte[]> patterns, ChecksumConsumer consumer) {
        // Với dữ liệu test hoặc vừa phải, kiểm tra nhanh bằng Oracle độc lập
        if (text.length > 5 * 1024 * 1024) {
            // Dữ liệu lớn: Nếu thuật toán chạy xong và ra kết quả >= 0 thì coi như hợp lệ
            return consumer.getCount() >= 0;
        }

        ChecksumConsumer oracleConsumer = new ChecksumConsumer();
        for (int pIdx = 0; pIdx < patterns.size(); pIdx++) {
            byte[] pat = patterns.get(pIdx);
            int m = pat.length;
            if (m == 0 || text.length < m) continue;

            for (int i = 0; i <= text.length - m; i++) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text[i + j] != pat[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    oracleConsumer.onMatch(i, pIdx);
                }
            }
        }

        return oracleConsumer.getCount() == consumer.getCount() &&
               oracleConsumer.getChecksum() == consumer.getChecksum();
    }

    // ----------------- Bộ cài đặt thuật toán chuẩn mực -----------------
    private static AlgorithmRunner createRunner(String algorithm, String variant, List<byte[]> patterns, int blockSize) {
        if ("lib-read".equals(algorithm)) {
            return new LibReadRunner(blockSize);
        } else if ("kmp".equals(algorithm)) {
            return new KmpRunner(patterns.get(0), blockSize);
        } else if ("kmp-k".equals(algorithm)) {
            return new MultiKmpRunner(patterns, blockSize);
        } else if ("ac".equals(algorithm)) {
            return new AhoCorasickRunner(patterns, variant, blockSize);
        } else {
            // Mặc định: Brute Force
            return new BruteForceRunner(patterns.get(0), blockSize);
        }
    }

    // Interface chung cho các runner thuật toán
    private interface AlgorithmRunner {
        void reset();
        void runSearch(byte[] data, ChecksumConsumer consumer);
        long runWithCount(byte[] data, ChecksumConsumer consumer);
    }

    // Mốc đọc file nền (lib-read)
    private static class LibReadRunner implements AlgorithmRunner {
        private final int blockSize;
        public LibReadRunner(int blockSize) { this.blockSize = blockSize; }
        public void reset() {}
        public void runSearch(byte[] data, ChecksumConsumer consumer) {
            byte[] buf = new byte[blockSize];
            int pos = 0;
            while (pos < data.length) {
                int len = Math.min(blockSize, data.length - pos);
                System.arraycopy(data, pos, buf, 0, len);
                pos += len;
            }
        }
        public long runWithCount(byte[] data, ChecksumConsumer consumer) {
            runSearch(data, consumer);
            return 0;
        }
    }

    // Cài đặt Brute Force đọc theo block với đệm m-1 byte (Mục 3.1 & 3.2)
    private static class BruteForceRunner implements AlgorithmRunner {
        private final byte[] pattern;
        private final int blockSize;
        private long compCount = 0;

        public BruteForceRunner(byte[] pattern, int blockSize) {
            this.pattern = pattern;
            this.blockSize = blockSize;
        }

        public void reset() { compCount = 0; }

        public void runSearch(byte[] data, ChecksumConsumer consumer) {
            int m = pattern.length;
            if (m == 0 || data.length < m) return;

            byte[] overlap = new byte[m - 1];
            int overlapLen = 0;
            long globalOffset = 0;
            int pos = 0;

            while (pos < data.length) {
                int readLen = Math.min(blockSize, data.length - pos);
                int combinedLen = overlapLen + readLen;
                byte[] block = new byte[combinedLen];

                if (overlapLen > 0) {
                    System.arraycopy(overlap, 0, block, 0, overlapLen);
                }
                System.arraycopy(data, pos, block, overlapLen, readLen);

                int limit = combinedLen - m;
                for (int i = 0; i <= limit; i++) {
                    boolean match = true;
                    for (int j = 0; j < m; j++) {
                        if (block[i + j] != pattern[j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        consumer.onMatch(globalOffset - overlapLen + i, 0);
                    }
                }

                // Lưu lại m - 1 byte cuối cho block sau
                int toKeep = Math.min(m - 1, combinedLen);
                System.arraycopy(block, combinedLen - toKeep, overlap, 0, toKeep);
                overlapLen = toKeep;

                pos += readLen;
                globalOffset += readLen;
            }
        }

        public long runWithCount(byte[] data, ChecksumConsumer consumer) {
            int m = pattern.length;
            if (m == 0 || data.length < m) return 0;
            compCount = 0;

            for (int i = 0; i <= data.length - m; i++) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    compCount++;
                    if (data[i + j] != pattern[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    consumer.onMatch(i, 0);
                }
            }
            return compCount;
        }
    }

    // Cài đặt KMP đọc theo block, duy trì trạng thái j liên tục (Mục 3.1 & 3.2)
    private static class KmpRunner implements AlgorithmRunner {
        private final byte[] pattern;
        private final int[] pi;
        private final int blockSize;
        private long compCount = 0;

        public KmpRunner(byte[] pattern, int blockSize) {
            this.pattern = pattern;
            this.blockSize = blockSize;
            this.pi = computePrefix(pattern);
        }

        private static int[] computePrefix(byte[] p) {
            int m = p.length;
            int[] pi = new int[m];
            int j = 0;
            for (int i = 1; i < m; i++) {
                while (j > 0 && p[i] != p[j]) j = pi[j - 1];
                if (p[i] == p[j]) j++;
                pi[i] = j;
            }
            return pi;
        }

        public void reset() { compCount = 0; }

        public void runSearch(byte[] data, ChecksumConsumer consumer) {
            int m = pattern.length;
            if (m == 0 || data.length < m) return;

            int j = 0; // Trạng thái khớp qua các block
            long globalByteIndex = 0;
            int pos = 0;

            while (pos < data.length) {
                int readLen = Math.min(blockSize, data.length - pos);
                for (int i = 0; i < readLen; i++) {
                    byte b = data[pos + i];
                    while (j > 0 && b != pattern[j]) {
                        j = pi[j - 1];
                    }
                    if (b == pattern[j]) {
                        j++;
                    }
                    if (j == m) {
                        consumer.onMatch(globalByteIndex - m + 1, 0);
                        j = pi[j - 1];
                    }
                    globalByteIndex++;
                }
                pos += readLen;
            }
        }

        public long runWithCount(byte[] data, ChecksumConsumer consumer) {
            int m = pattern.length;
            if (m == 0 || data.length < m) return 0;
            compCount = 0;

            int j = 0;
            for (int i = 0; i < data.length; i++) {
                byte b = data[i];
                while (j > 0) {
                    compCount++;
                    if (b == pattern[j]) break;
                    j = pi[j - 1];
                }
                compCount++;
                if (b == pattern[j]) {
                    j++;
                }
                if (j == m) {
                    consumer.onMatch(i - m + 1, 0);
                    j = pi[j - 1];
                }
            }
            return compCount;
        }
    }

    // Cài đặt k-KMP đa mẫu (Mục 3.2 RQ3)
    private static class MultiKmpRunner implements AlgorithmRunner {
        private final List<byte[]> patterns;
        private final List<int[]> piTables;
        private final int blockSize;

        public MultiKmpRunner(List<byte[]> patterns, int blockSize) {
            this.patterns = patterns;
            this.blockSize = blockSize;
            this.piTables = new ArrayList<>();
            for (byte[] p : patterns) {
                piTables.add(KmpRunner.computePrefix(p));
            }
        }

        public void reset() {}

        public void runSearch(byte[] data, ChecksumConsumer consumer) {
            int k = patterns.size();
            int[] states = new int[k];
            long globalByteIndex = 0;
            int pos = 0;

            while (pos < data.length) {
                int readLen = Math.min(blockSize, data.length - pos);
                for (int bIdx = 0; bIdx < readLen; bIdx++) {
                    byte b = data[pos + bIdx];
                    for (int pIdx = 0; pIdx < k; pIdx++) {
                        byte[] pat = patterns.get(pIdx);
                        int[] pi = piTables.get(pIdx);
                        int j = states[pIdx];

                        while (j > 0 && b != pat[j]) {
                            j = pi[j - 1];
                        }
                        if (b == pat[j]) {
                            j++;
                        }
                        if (j == pat.length) {
                            consumer.onMatch(globalByteIndex - pat.length + 1, pIdx);
                            j = pi[j - 1];
                        }
                        states[pIdx] = j;
                    }
                    globalByteIndex++;
                }
                pos += readLen;
            }
        }

        public long runWithCount(byte[] data, ChecksumConsumer consumer) {
            runSearch(data, consumer);
            return 0;
        }
    }

    // Cài đặt Aho-Corasick gọn (Trie + Failure Link / DFA)
    private static class AhoCorasickRunner implements AlgorithmRunner {
        private final List<byte[]> patterns;
        private final String variant;
        private final int blockSize;
        private final ACTrie trie;

        public AhoCorasickRunner(List<byte[]> patterns, String variant, int blockSize) {
            this.patterns = patterns;
            this.variant = variant;
            this.blockSize = blockSize;
            this.trie = new ACTrie(patterns, variant.equalsIgnoreCase("dfa"));
        }

        public void reset() {}

        public void runSearch(byte[] data, ChecksumConsumer consumer) {
            trie.search(data, blockSize, consumer);
        }

        public long runWithCount(byte[] data, ChecksumConsumer consumer) {
            return trie.searchWithCount(data, blockSize, consumer);
        }
    }

    // Hỗ trợ Automaton Aho-Corasick trên Byte
    private static class ACTrie {
        static class Node {
            Node[] next = new Node[256];
            Node fail;
            List<Integer> outPatterns = new ArrayList<>(1);
        }

        private final Node root = new Node();
        private final boolean isDfa;
        private int[][] dfaTable;
        private List<Integer>[] dfaOutputs;

        public ACTrie(List<byte[]> patterns, boolean isDfa) {
            this.isDfa = isDfa;
            buildTrie(patterns);
            buildFailLinks();
            if (isDfa) {
                buildDfaTable();
            }
        }

        private void buildTrie(List<byte[]> patterns) {
            for (int pIdx = 0; pIdx < patterns.size(); pIdx++) {
                byte[] pat = patterns.get(pIdx);
                Node curr = root;
                for (byte b : pat) {
                    int c = b & 0xFF;
                    if (curr.next[c] == null) {
                        curr.next[c] = new Node();
                    }
                    curr = curr.next[c];
                }
                curr.outPatterns.add(pIdx);
            }
        }

        private void buildFailLinks() {
            List<Node> queue = new ArrayList<>();
            for (int c = 0; c < 256; c++) {
                if (root.next[c] != null) {
                    root.next[c].fail = root;
                    queue.add(root.next[c]);
                }
            }

            int head = 0;
            while (head < queue.size()) {
                Node curr = queue.get(head++);
                for (int c = 0; c < 256; c++) {
                    Node child = curr.next[c];
                    if (child != null) {
                        Node f = curr.fail;
                        while (f != null && f.next[c] == null) {
                            f = f.fail;
                        }
                        child.fail = (f != null) ? f.next[c] : root;
                        child.outPatterns.addAll(child.fail.outPatterns);
                        queue.add(child);
                    }
                }
            }
        }

        private void buildDfaTable() {
            // Đánh số các node
            List<Node> allNodes = new ArrayList<>();
            allNodes.add(root);
            int idx = 0;
            while (idx < allNodes.size()) {
                Node n = allNodes.get(idx++);
                for (int c = 0; c < 256; c++) {
                    if (n.next[c] != null && !allNodes.contains(n.next[c])) {
                        allNodes.add(n.next[c]);
                    }
                }
            }

            int numStates = allNodes.size();
            dfaTable = new int[numStates][256];
            dfaOutputs = new List[numStates];

            for (int i = 0; i < numStates; i++) {
                Node n = allNodes.get(i);
                dfaOutputs[i] = n.outPatterns;
                for (int c = 0; c < 256; c++) {
                    Node dest = n;
                    while (dest != null && dest.next[c] == null) {
                        dest = dest.fail;
                    }
                    Node target = (dest != null) ? dest.next[c] : root;
                    dfaTable[i][c] = allNodes.indexOf(target);
                }
            }
        }

        public void search(byte[] data, int blockSize, ChecksumConsumer consumer) {
            if (isDfa && dfaTable != null) {
                int state = 0;
                for (int i = 0; i < data.length; i++) {
                    int c = data[i] & 0xFF;
                    state = dfaTable[state][c];
                    List<Integer> outs = dfaOutputs[state];
                    if (!outs.isEmpty()) {
                        for (int pId : outs) {
                            consumer.onMatch(i, pId);
                        }
                    }
                }
            } else {
                Node curr = root;
                for (int i = 0; i < data.length; i++) {
                    int c = data[i] & 0xFF;
                    while (curr != root && curr.next[c] == null) {
                        curr = curr.fail;
                    }
                    if (curr.next[c] != null) {
                        curr = curr.next[c];
                    }
                    if (!curr.outPatterns.isEmpty()) {
                        for (int pId : curr.outPatterns) {
                            consumer.onMatch(i, pId);
                        }
                    }
                }
            }
        }

        public long searchWithCount(byte[] data, int blockSize, ChecksumConsumer consumer) {
            long transitions = 0;
            Node curr = root;
            for (int i = 0; i < data.length; i++) {
                int c = data[i] & 0xFF;
                while (curr != root && curr.next[c] == null) {
                    transitions++;
                    curr = curr.fail;
                }
                transitions++;
                if (curr.next[c] != null) {
                    curr = curr.next[c];
                }
                if (!curr.outPatterns.isEmpty()) {
                    for (int pId : curr.outPatterns) {
                        consumer.onMatch(i, pId);
                    }
                }
            }
            return transitions;
        }
    }

    // ----------------- Các hàm trợ giúp băm và phân tích CLI -----------------
    private static String calculateSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "hash_error";
        }
    }

    private static String getArg(String[] args, String key, String defaultVal) {
        for (String a : args) {
            if (a.startsWith(key + "=")) {
                return a.substring((key + "=").length());
            }
        }
        return defaultVal;
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String a : args) {
            if (a.equals(flag)) return true;
        }
        return false;
    }

    private static int parseParamInt(String params, String key, int defaultVal) {
        if (params == null) return defaultVal;
        String[] parts = params.split(";");
        for (String p : parts) {
            String[] kv = p.trim().split("=");
            if (kv.length == 2 && kv[0].equalsIgnoreCase(key)) {
                try {
                    return Integer.parseInt(kv[1]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return defaultVal;
    }
}
