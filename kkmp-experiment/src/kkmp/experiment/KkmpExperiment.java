package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * k-KMP: doc input MOT luot theo block, moi byte cap nhat lan luot k trang thai KMP.
 *
 * Quy uoc theo de (Pattern Matching):
 *  - Tim tren mang byte, vi tri la offset byte (long), tinh tu 0.
 *  - Tim moi lan xuat hien, ke ca chong lan. Mau rong -> bao loi.
 *  - Khong luu danh sach vi tri: moi lan khop count++ va
 *    check += (vi tri bat dau + 1) * 1000003 + ma mau (cong long, cho phep tran).
 *  - Trang thai matched cua tung mau duoc giu nguyen giua cac block.
 *
 * Tuong thich Java 8.
 */
public class KkmpExperiment {

    static final long MULT = 1000003L;
    static final int DEFAULT_BLOCK = 1 << 20; // 1 MiB

    // ------------------------------------------------------------------
    // Trang thai cua tung mau
    // ------------------------------------------------------------------
    static class PatternState {

        final byte[] pattern;
        final int[] pi;
        final int id;
        int matched; // so ky tu da khop (giu qua block)

        PatternState(byte[] pattern, int id) {
            if (pattern == null || pattern.length == 0) {
                throw new IllegalArgumentException("Pattern khong duoc rong");
            }
            this.pattern = pattern;
            this.id = id;
            this.pi = buildPrefix(pattern);
        }
    }

    /** Ket qua: count + check (+ ops chi co y nghia o luot --count). */
    static class Result {

        long count;
        long check;
        long ops; // so phep so sanh byte (chi dem o luot --count)

        @Override
        public String toString() {
            return "count=" + count + ", check=" + check;
        }
    }

    // ------------------------------------------------------------------
    // Dung bang prefix (failure) theo giao trinh
    // ------------------------------------------------------------------
    static int[] buildPrefix(byte[] pattern) {
        int[] pi = new int[pattern.length];
        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[i] != pattern[j]) {
                j = pi[j - 1];
            }
            if (pattern[i] == pattern[j]) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    /** Dung k trang thai. Thoi gian ham nay chinh la build_ms. */
    static PatternState[] build(byte[][] patterns) {
        PatternState[] states = new PatternState[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            states[i] = new PatternState(patterns[i], i);
        }
        return states;
    }

    static byte[][] toBytes(String[] patterns) {
        byte[][] out = new byte[patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            out[i] = patterns[i].getBytes(StandardCharsets.ISO_8859_1);
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Quet 1 luot theo block. countOps = false: luot do thoi gian (khong dem).
    // ------------------------------------------------------------------
    static Result search(InputStream input, PatternState[] states,
            int blockSize, boolean countOps) throws IOException {

        if (blockSize <= 0) {
            throw new IllegalArgumentException("Block size phai lon hon 0");
        }
        for (PatternState s : states) {
            s.matched = 0; // moi lan quet bat dau tu trang thai 0
        }

        Result r = new Result();
        byte[] buffer = new byte[blockSize];
        long position = 0; // offset byte cua byte dang xet
        int bytesRead;

        while ((bytesRead = input.read(buffer)) != -1) {
            if (countOps) {
                position = scanCounting(buffer, bytesRead, states, position, r);
            } else {
                position = scan(buffer, bytesRead, states, position, r);
            }
        }
        return r;
    }

    /** Vong quet khong bo dem (dung khi do thoi gian). */
    private static long scan(byte[] buf, int len, PatternState[] states,
            long position, Result r) {
        for (int b = 0; b < len; b++) {
            byte cur = buf[b];
            for (PatternState s : states) {
                int m = s.matched;
                while (m > 0 && cur != s.pattern[m]) {
                    m = s.pi[m - 1];
                }
                if (cur == s.pattern[m]) {
                    m++;
                }
                if (m == s.pattern.length) {
                    long start = position - s.pattern.length + 1;
                    r.count++;
                    r.check += (start + 1) * MULT + s.id;
                    m = s.pi[m - 1]; // cho phep ket qua chong lan
                }
                s.matched = m;
            }
            position++;
        }
        return position;
    }

    /** Vong quet co bo dem phep so sanh byte (chi dung o luot --count). */
    private static long scanCounting(byte[] buf, int len, PatternState[] states,
            long position, Result r) {
        long ops = 0;
        for (int b = 0; b < len; b++) {
            byte cur = buf[b];
            for (PatternState s : states) {
                int m = s.matched;
                while (m > 0) {
                    ops++;
                    if (cur == s.pattern[m]) {
                        break;
                    }
                    m = s.pi[m - 1];
                }
                ops++;
                if (cur == s.pattern[m]) {
                    m++;
                }
                if (m == s.pattern.length) {
                    long start = position - s.pattern.length + 1;
                    r.count++;
                    r.check += (start + 1) * MULT + s.id;
                    m = s.pi[m - 1];
                }
                s.matched = m;
            }
            position++;
        }
        r.ops += ops;
        return position;
    }

    /** Doc file bang FileInputStream.read(byte[]) theo block, khong doc ca file vao RAM. */
    static Result searchFile(File file, PatternState[] states, int blockSize,
            boolean countOps) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return search(in, states, blockSize, countOps);
        }
    }

    static Result searchBytes(byte[] text, byte[][] patterns, int blockSize)
            throws IOException {
        try (InputStream in = new ByteArrayInputStream(text)) {
            return search(in, build(patterns), blockSize, false);
        }
    }

    // ------------------------------------------------------------------
    // Oracle: String.indexOf (chuoi tao bang ISO_8859_1 -> 1 byte = 1 ky tu)
    // ------------------------------------------------------------------
    static Result oracle(byte[] text, byte[][] patterns) {
        String t = new String(text, StandardCharsets.ISO_8859_1);
        Result r = new Result();
        for (int id = 0; id < patterns.length; id++) {
            String p = new String(patterns[id], StandardCharsets.ISO_8859_1);
            int from = 0;
            int idx;
            while ((idx = t.indexOf(p, from)) != -1) {
                r.count++;
                r.check += ((long) idx + 1) * MULT + id;
                from = idx + 1; // +1 de lay ca ket qua chong lan
            }
        }
        return r;
    }

    /** Tinh Result tu danh sach vi tri ky vong cho tung mau (ca co dinh cua de). */
    static Result expected(int[][] positionsPerPattern) {
        Result r = new Result();
        for (int id = 0; id < positionsPerPattern.length; id++) {
            for (int pos : positionsPerPattern[id]) {
                r.count++;
                r.check += ((long) pos + 1) * MULT + id;
            }
        }
        return r;
    }

    // ------------------------------------------------------------------
    // Khung test
    // ------------------------------------------------------------------
    static int failures = 0;

    static final int[] BLOCKS = {1, 2, 3, 7, DEFAULT_BLOCK};

    static boolean same(Result a, Result b) {
        return a.count == b.count && a.check == b.check;
    }

    /** Ca co dinh: so voi vi tri ky vong, moi B trong BLOCKS, ca luot do lan luot --count. */
    static void fixedCase(String name, String text, String[] patterns, int[][] expectedPos)
            throws IOException {
        byte[] t = text.getBytes(StandardCharsets.ISO_8859_1);
        byte[][] p = toBytes(patterns);
        Result exp = expected(expectedPos);
        Result orc = oracle(t, p);
        boolean ok = same(exp, orc); // chinh oracle cung phai khop dap an de bai
        if (!ok) {
            System.out.println("  [!] Oracle khac dap an ky vong: " + orc + " vs " + exp);
        }
        for (int B : BLOCKS) {
            Result r1 = searchBytes(t, p, B);
            Result r2;
            try (InputStream in = new ByteArrayInputStream(t)) {
                r2 = search(in, build(p), B, true); // luot --count
            }
            if (!same(r1, exp) || !same(r2, exp)) {
                ok = false;
                System.out.println("  FAIL B=" + B + ": got " + r1 + " / counting " + r2
                        + " | expected " + exp);
            }
        }
        report(name, ok);
    }

    static void report(String name, boolean ok) {
        if (ok) {
            System.out.println("PASS: " + name);
        } else {
            failures++;
            System.out.println("FAIL: " + name);
        }
    }

    static void randomTest(String name, int trials, long seed, boolean multi)
            throws IOException {
        Random rnd = new Random(seed);
        boolean ok = true;
        int[] blocks = {1, 2, 3, 7, DEFAULT_BLOCK};
        for (int t = 0; t < trials && ok; t++) {
            byte[] text = new byte[rnd.nextInt(201)]; // 0..200 byte
            for (int i = 0; i < text.length; i++) {
                text[i] = (byte) ('a' + rnd.nextInt(2)); // sigma = 2
            }
            int k = multi ? 1 + rnd.nextInt(20) : 1; // 1..20 mau
            byte[][] pats = new byte[k][];
            for (int i = 0; i < k; i++) {
                pats[i] = new byte[1 + rnd.nextInt(8)]; // 1..8 byte
                for (int j = 0; j < pats[i].length; j++) {
                    pats[i][j] = (byte) ('a' + rnd.nextInt(2));
                }
            }
            Result orc = oracle(text, pats);
            int B = blocks[t % blocks.length]; // luan phien cac B de moi B deu duoc thu
            Result got = searchBytes(text, pats, B);
            if (!same(got, orc)) {
                ok = false;
                System.out.println("  FAIL trial=" + t + " B=" + B + " k=" + k
                        + ": got " + got + " | oracle " + orc);
            }
        }
        report(name, ok);
    }

    /** Byte >= 128 (Java byte co dau): so sanh truc tiep van phai dung. */
    static void highByteTest() throws IOException {
        byte[] text = {(byte) 0xE9, (byte) 0xFF, (byte) 0xE9, (byte) 0xFF, (byte) 0xE9, 0x00};
        byte[][] pats = {
            {(byte) 0xE9, (byte) 0xFF},
            {(byte) 0xFF, (byte) 0xE9, 0x00},
            {(byte) 0xE9}
        };
        Result orc = oracle(text, pats);
        boolean ok = true;
        for (int B : BLOCKS) {
            if (!same(searchBytes(text, pats, B), orc)) {
                ok = false;
                System.out.println("  FAIL B=" + B);
            }
        }
        report("Byte >= 128 (0xE9, 0xFF)", ok);
    }

    /** Doc that tu file tam, kiem tra khop voi oracle. */
    static void fileTest() throws IOException {
        File f = File.createTempFile("kkmp", ".bin");
        try {
            Random rnd = new Random(42);
            byte[] text = new byte[50000];
            for (int i = 0; i < text.length; i++) {
                text[i] = (byte) ('a' + rnd.nextInt(3));
            }
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(f)) {
                out.write(text);
            }
            byte[][] pats = toBytes(new String[]{"abca", "aa", "cbacb"});
            Result orc = oracle(text, pats);
            boolean ok = true;
            for (int B : new int[]{1, 7, 4096, DEFAULT_BLOCK}) {
                Result r = searchFile(f, build(pats), B, false);
                Result rc = searchFile(f, build(pats), B, true);
                if (!same(r, orc) || !same(rc, orc)) {
                    ok = false;
                    System.out.println("  FAIL B=" + B + ": " + r + " | oracle " + orc);
                }
            }
            report("Doc tu file (FileInputStream) khop oracle", ok);
        } finally {
            f.delete();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=== Ca co dinh cua de (B = 1, 2, 3, 7, 1 MiB) ===");

        fixedCase("aaaaa / aaa -> 0,1,2",
                "aaaaa", new String[]{"aaa"},
                new int[][]{{0, 1, 2}});

        fixedCase("abababab / abab -> 0,2,4",
                "abababab", new String[]{"abab"},
                new int[][]{{0, 2, 4}});

        fixedCase("abc / abcd -> khong co",
                "abc", new String[]{"abcd"},
                new int[][]{{}});

        // Ca k mau: ushers voi {he, she, his, hers}: she@1, he@2, hers@2
        fixedCase("ushers / {he, she, his, hers}",
                "ushers", new String[]{"he", "she", "his", "hers"},
                new int[][]{{2}, {1}, {}, {2}});

        fixedCase("abababab / {abab, bab, xyz}",
                "abababab", new String[]{"abab", "bab", "xyz"},
                new int[][]{{0, 2, 4}, {1, 3, 5}, {}});

        fixedCase("Hai mau trung nhau (dem rieng tung ma mau)",
                "aaaa", new String[]{"aa", "aa"},
                new int[][]{{0, 1, 2}, {0, 1, 2}});

        fixedCase("Van ban rong",
                "", new String[]{"a"},
                new int[][]{{}});

        fixedCase("Mau dai bang van ban",
                "abcd", new String[]{"abcd"},
                new int[][]{{0}});

        fixedCase("Mau 1 ky tu",
                "banana", new String[]{"a", "n"},
                new int[][]{{1, 3, 5}, {2, 4}});

        fixedCase("T4 nho: aaaaaaaab / aaab (lat bat loi)",
                "aaaaaaaab", new String[]{"aaab"},
                new int[][]{{5}});

        // Mau rong phai bao loi
        boolean ok = false;
        try {
            new PatternState(new byte[0], 0);
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        report("Mau rong -> bao loi", ok);

        ok = false;
        try {
            search(new ByteArrayInputStream(new byte[]{1}), build(toBytes(new String[]{"a"})), 0, false);
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        report("Block size <= 0 -> bao loi", ok);

        System.out.println("\n=== Byte co dau va doc file ===");
        highByteTest();
        fileTest();

        System.out.println("\n=== Ngau nhien so voi oracle String.indexOf ===");
        randomTest("10^4 cap, 1 mau (sigma=2, van ban 0-200, mau 1-8)", 10000, 1L, false);
        randomTest("10^4 cap, 1-20 mau (k-KMP so voi tong oracle)", 10000, 2L, true);

        System.out.println();
        if (failures == 0) {
            System.out.println("TAT CA TEST DEU QUA");
        } else {
            System.out.println("CO " + failures + " TEST THAT BAI");
            System.exit(1);
        }
    }
}