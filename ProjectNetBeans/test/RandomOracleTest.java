package test;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import algorithm.ahocorasick.ACDFAMatcher;
import algorithm.ahocorasick.ACFailMatcher;
import algorithm.bruteforce.BruteForceMatcher;
import algorithm.kmp.KKMPMatcher;
import algorithm.kmp.KMPMatcher;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.Test;

/** Deterministic randomized comparison of all matchers against String.indexOf. */
public class RandomOracleTest {
    private static final int CASE_COUNT = 104;
    private static final int[] BLOCK_SIZES = {1, 2, 3, 7, 16};

    @Test
    public void testAlgorithmsAgainstRandomOracle() {
        Random random = new Random(20261027L);
        for (int caseNo = 1; caseNo <= CASE_COUNT; caseNo++) {
            byte[] text = randomBytes(random, random.nextInt(201));
            byte[] pattern = randomBytes(random, 1 + random.nextInt(8));
            List<Match> expected = oracle(text, pattern);
            for (int blockSize : BLOCK_SIZES) {
                assertMatches(caseNo, blockSize, "Brute Force", expected,
                        runBrute(text, pattern, blockSize));
                assertMatches(caseNo, blockSize, "KMP", expected,
                        runKmp(text, pattern, blockSize));
                assertMatches(caseNo, blockSize, "k-KMP", expected,
                        runKkmp(text, pattern, blockSize));
                assertMatches(caseNo, blockSize, "Aho-Corasick failure", expected,
                        runAcFail(text, pattern, blockSize));
                assertMatches(caseNo, blockSize, "Aho-Corasick DFA", expected,
                        runAcDfa(text, pattern, blockSize));
            }
        }
    }

    private static byte[] randomBytes(Random random, int length) {
        byte[] value = new byte[length];
        for (int i = 0; i < length; i++) value[i] = (byte) random.nextInt(2); // sigma = 2
        return value;
    }

    private static List<Match> oracle(byte[] text, byte[] pattern) {
        String t = new String(text, StandardCharsets.ISO_8859_1);
        String p = new String(pattern, StandardCharsets.ISO_8859_1);
        List<Match> found = new ArrayList<>();
        int from = 0;
        while (from <= t.length() - p.length()) {
            int at = t.indexOf(p, from);
            if (at < 0) break;
            found.add(new Match(0, at));
            from = at + 1; // Include overlapping occurrences.
        }
        return sorted(found);
    }

    private static List<Match> runBrute(byte[] data, byte[] pattern, int blockSize) {
        BruteForceMatcher matcher = new BruteForceMatcher(blockSize);
        matcher.compilePattern(Collections.singletonList(pattern));
        Recorder result = new Recorder();
        matcher.searchPattern(data, result);
        return result.sorted();
    }

    private static List<Match> runKmp(byte[] data, byte[] pattern, int blockSize) {
        KMPMatcher matcher = new KMPMatcher();
        matcher.compilePattern(Collections.singletonList(pattern));
        Recorder result = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchPattern(Arrays.copyOfRange(data, start, end), result);
        }
        return result.sorted();
    }

    private static List<Match> runKkmp(byte[] data, byte[] pattern, int blockSize) {
        KKMPMatcher matcher = new KKMPMatcher();
        matcher.compilePattern(Collections.singletonList(pattern));
        Recorder result = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchPattern(Arrays.copyOfRange(data, start, end), result);
        }
        return result.sorted();
    }

    private static List<Match> runAcFail(byte[] data, byte[] pattern, int blockSize) {
        ACFailMatcher matcher = new ACFailMatcher();
        matcher.compilePattern(Collections.singletonList(pattern));
        Recorder result = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchBlock(Arrays.copyOfRange(data, start, end), start, result);
        }
        return result.sorted();
    }

    private static List<Match> runAcDfa(byte[] data, byte[] pattern, int blockSize) {
        ACDFAMatcher matcher = new ACDFAMatcher();
        matcher.compilePattern(Collections.singletonList(pattern));
        Recorder result = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchBlock(Arrays.copyOfRange(data, start, end), start, result);
        }
        return result.sorted();
    }

    private static void assertMatches(int caseNo, int blockSize, String algorithm,
            List<Match> expected, List<Match> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Case " + caseNo + ", B=" + blockSize + ", "
                    + algorithm + ": oracle=" + expected + ", actual=" + actual);
        }
    }

    private static List<Match> sorted(List<Match> input) {
        List<Match> result = new ArrayList<>(input);
        Collections.sort(result, new Comparator<Match>() {
            public int compare(Match a, Match b) {
                int byOffset = Long.compare(a.offset, b.offset);
                return byOffset != 0 ? byOffset : Integer.compare(a.patternId, b.patternId);
            }
        });
        return result;
    }

    private static final class Match {
        final int patternId;
        final long offset;
        Match(int patternId, long offset) { this.patternId = patternId; this.offset = offset; }
        @Override public boolean equals(Object other) {
            if (!(other instanceof Match)) return false;
            Match m = (Match) other;
            return patternId == m.patternId && offset == m.offset;
        }
        @Override public int hashCode() { return 31 * patternId + (int) (offset ^ (offset >>> 32)); }
        @Override public String toString() { return "(" + patternId + "," + offset + ")"; }
    }

    private static final class Recorder extends ChecksumConsumer {
        private final List<Match> found = new ArrayList<>();
        @Override public void onMatch(long offset, int patternId) {
            found.add(new Match(patternId, offset));
            super.onMatch(offset, patternId);
        }
        List<Match> sorted() { return RandomOracleTest.sorted(found); }
    }
}
