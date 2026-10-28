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
import org.junit.Test;

/** Fixed correctness cases required for the pattern-matching project. */
public class FixedCasesTest {
    private static final int[] BLOCK_SIZES = {1, 2, 3, 7};

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(FixedCasesTest.class.getName());
    }

    @Test
    public void testSinglePatternFixedCases() {
        runSingleCase("TC01", "aaaaa", new String[] {"aaa"},
                matches(0, 0, 0, 1, 0, 2));
        runSingleCase("TC02", "abababab", new String[] {"abab"},
                matches(0, 0, 0, 2, 0, 4));
        runSingleCase("TC03", "abc", new String[] {"abcd"},
                Collections.<Match>emptyList());
    }

    @Test
    public void testEmptyPatternRejected() {
        runEmptyPatternCase();
    }

    @Test
    public void testMultiPatternUshersCase() {
        runMultiPatternCase();
    }

    @Test
    public void testAdversarialT4Case() {
        runT4Case();
    }

    private static void runSingleCase(String id, String text, String[] patternStrings,
            List<Match> expected) {
        byte[] data = bytes(text);
        List<byte[]> patterns = encode(patternStrings);
        for (int blockSize : BLOCK_SIZES) {
            verify(id, "Brute Force", expected, runBrute(data, patterns, blockSize));
            verify(id, "KMP", expected, runKmpPerPattern(data, patterns, blockSize));
            verify(id, "k-KMP", expected, runKkmp(data, patterns, blockSize));
            verify(id, "Aho-Corasick failure", expected,
                    runAcFail(data, patterns, blockSize));
            verify(id, "Aho-Corasick DFA", expected,
                    runAcDfa(data, patterns, blockSize));
        }
    }

    private static void runEmptyPatternCase() {
        byte[] data = bytes("abc");
        List<byte[]> patterns = Collections.singletonList(bytes(""));
        expectIllegalArgument("TC04 Brute Force", new Runnable() {
            public void run() { compile(new BruteForceMatcher(), patterns); }
        });
        expectIllegalArgument("TC04 KMP", new Runnable() {
            public void run() { compile(new KMPMatcher(), patterns); }
        });
        expectIllegalArgument("TC04 k-KMP", new Runnable() {
            public void run() { compile(new KKMPMatcher(), patterns); }
        });
        expectIllegalArgument("TC04 AC failure", new Runnable() {
            public void run() { compile(new ACFailMatcher(), patterns); }
        });
        expectIllegalArgument("TC04 AC DFA", new Runnable() {
            public void run() { compile(new ACDFAMatcher(), patterns); }
        });
        if (data.length == 0) throw new AssertionError("TC04 setup error");
    }

    private static void runMultiPatternCase() {
        byte[] data = bytes("ushers");
        List<byte[]> patterns = encode("he", "she", "his", "hers");
        List<Match> expected = matches(1, 1, 0, 2, 3, 2);
        for (int blockSize : BLOCK_SIZES) {
            verify("TC05", "Brute Force", expected, runBrute(data, patterns, blockSize));
            verify("TC05", "KMP per pattern", expected,
                    runKmpPerPattern(data, patterns, blockSize));
            verify("TC05", "k-KMP", expected, runKkmp(data, patterns, blockSize));
            verify("TC05", "Aho-Corasick failure", expected,
                    runAcFail(data, patterns, blockSize));
            verify("TC05", "Aho-Corasick DFA", expected,
                    runAcDfa(data, patterns, blockSize));
        }
    }

    /** Adversarial T4: repeated a's followed by b, searched with a^r b. */
    private static void runT4Case() {
        runSingleCase("T4", "aaaaaaaaab", new String[] {"aaaaab"},
                matches(0, 4));
    }

    private static List<Match> runBrute(byte[] data, List<byte[]> patterns, int blockSize) {
        BruteForceMatcher matcher = new BruteForceMatcher(blockSize);
        compile(matcher, patterns);
        Recorder recorder = new Recorder();
        matcher.searchPattern(data, recorder);
        return recorder.sorted();
    }

    private static List<Match> runKmpPerPattern(byte[] data, List<byte[]> patterns,
            int blockSize) {
        List<Match> all = new ArrayList<>();
        for (int p = 0; p < patterns.size(); p++) {
            KMPMatcher matcher = new KMPMatcher();
            compile(matcher, Collections.singletonList(patterns.get(p)));
            Recorder recorder = new Recorder();
            // Repeated calls represent sequential input blocks; KMP retains matched/globalOffset.
            for (int start = 0; start < data.length; start += blockSize) {
                int end = Math.min(data.length, start + blockSize);
                matcher.searchPattern(Arrays.copyOfRange(data, start, end), recorder.withPatternId(p));
            }
            all.addAll(recorder.sorted());
        }
        return sorted(all);
    }

    private static List<Match> runKkmp(byte[] data, List<byte[]> patterns, int blockSize) {
        KKMPMatcher matcher = new KKMPMatcher();
        compile(matcher, patterns);
        Recorder recorder = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchPattern(Arrays.copyOfRange(data, start, end), recorder);
        }
        return recorder.sorted();
    }

    private static List<Match> runAcFail(byte[] data, List<byte[]> patterns, int blockSize) {
        ACFailMatcher matcher = new ACFailMatcher();
        compile(matcher, patterns);
        Recorder recorder = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchBlock(Arrays.copyOfRange(data, start, end), start, recorder);
        }
        return recorder.sorted();
    }

    private static List<Match> runAcDfa(byte[] data, List<byte[]> patterns, int blockSize) {
        ACDFAMatcher matcher = new ACDFAMatcher();
        compile(matcher, patterns);
        Recorder recorder = new Recorder();
        for (int start = 0; start < data.length; start += blockSize) {
            int end = Math.min(data.length, start + blockSize);
            matcher.searchBlock(Arrays.copyOfRange(data, start, end), start, recorder);
        }
        return recorder.sorted();
    }

    private static void compile(PatternMatching matcher, List<byte[]> patterns) {
        matcher.compilePattern(patterns);
    }

    private static void verify(String id, String algorithm, List<Match> expected,
            List<Match> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(id + " / " + algorithm + ": expected " + expected
                    + ", got " + actual);
        }
    }

    private static void expectIllegalArgument(String id, Runnable action) {
        try {
            action.run();
            throw new AssertionError(id + ": expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Correct behavior for the empty-pattern input.
        }
    }

    private static List<byte[]> encode(String... values) {
        List<byte[]> result = new ArrayList<>();
        for (String value : values) result.add(bytes(value));
        return result;
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static List<Match> matches(int... patternIdThenOffsets) {
        List<Match> result = new ArrayList<>();
        for (int i = 0; i < patternIdThenOffsets.length; i += 2) {
            int pid = patternIdThenOffsets[i];
            int offset = patternIdThenOffsets[i + 1];
            result.add(new Match(pid, offset));
        }
        return sorted(result);
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
        @Override public String toString() { return "(pattern=" + patternId + ", offset=" + offset + ")"; }
    }

    private static final class Recorder extends ChecksumConsumer {
        private final List<Match> found = new ArrayList<>();
        private int forcedPatternId = -1;
        Recorder withPatternId(int id) { forcedPatternId = id; return this; }
        @Override public void onMatch(long offset, int patternId) {
            int id = forcedPatternId >= 0 ? forcedPatternId : patternId;
            found.add(new Match(id, offset));
            super.onMatch(offset, id);
        }
        List<Match> sorted() { return FixedCasesTest.sorted(found); }
    }
}
