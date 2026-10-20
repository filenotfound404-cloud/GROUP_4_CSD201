/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


package kkmp.experiment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class KkmpExperiment {

    static class PatternState {

        byte[] pattern;
        int[] pi;
        int matched;
        int id;

        PatternState(String pattern, int id) {
            this.pattern = pattern.getBytes(StandardCharsets.ISO_8859_1);
            this.id = id;

            if (this.pattern.length == 0) {
                throw new IllegalArgumentException(
                        "Pattern khong duoc rong");
            }

            this.pi = buildPrefix(this.pattern);
        }
    }

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

    static void search(InputStream input,
            String[] patterns,
            int blockSize) throws IOException {

        if (blockSize <= 0) {
            throw new IllegalArgumentException(
                    "Block size phai lon hon 0");
        }

        PatternState[] states = new PatternState[patterns.length];

        for (int i = 0; i < patterns.length; i++) {
            states[i] = new PatternState(patterns[i], i);
        }

        byte[] buffer = new byte[blockSize];
        long position = 0;
        int bytesRead;

        while ((bytesRead = input.read(buffer)) != -1) {
            for (int b = 0; b < bytesRead; b++) {
                byte current = buffer[b];

                // Moi byte cap nhat tung trang thai KMP.
                for (PatternState state : states) {
                    while (state.matched > 0
                            && current != state.pattern[state.matched]) {
                        state.matched
                                = state.pi[state.matched - 1];
                    }

                    if (current == state.pattern[state.matched]) {
                        state.matched++;
                    }

                    if (state.matched == state.pattern.length) {
                        long start = position
                                - state.pattern.length + 1;

                        System.out.println(
                                "Pattern " + state.id
                                + " match at " + start);

                        // Cho phep tim cac ket qua chong lap.
                        state.matched
                                = state.pi[state.matched - 1];
                    }
                }

                position++;
            }
        }
    }

    static String captureSearch(String text, String[] patterns,
            int blockSize) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream captured = new PrintStream(output)) {
            System.setOut(captured);

            try (InputStream input = new ByteArrayInputStream(
                    text.getBytes(StandardCharsets.ISO_8859_1))) {
                search(input, patterns, blockSize);
            }
        } finally {
            System.setOut(originalOut);
        }

        return output.toString();
    }

    static void check(String testName, String text, String[] patterns,
            String expected, int[] blockSizes) throws IOException {
        for (int blockSize : blockSizes) {
            String actual = captureSearch(text, patterns, blockSize);

           if (!actual.replace("\r\n", "\n").equals(
        expected.replace("\r\n", "\n"))) { 
                System.out.println("FAIL: " + testName
                        + ", block=" + blockSize);
                System.out.println("Expected:\n" + expected);
                System.out.println("Actual:\n" + actual);
                return;
            }
        }

        System.out.println("PASS: " + testName);
    }

    public static void main(String[] args) throws IOException {
        int[] blockSizes = {1, 2, 3, 7};

        check(
                "Overlapping matches",
                "aaaaa",
                new String[]{"aaa"},
                "Pattern 0 match at 0\n"
                + "Pattern 0 match at 1\n"
                + "Pattern 0 match at 2\n",
                blockSizes
        );

        check(
                "Multiple patterns",
                "abababab",
                new String[]{"abab", "bab", "xyz"},
                "Pattern 0 match at 0\n"
                + "Pattern 1 match at 1\n"
                + "Pattern 0 match at 2\n"
                + "Pattern 1 match at 3\n"
                + "Pattern 0 match at 4\n"
                + "Pattern 1 match at 5\n",
                blockSizes
        );

        check(
                "Pattern longer than text",
                "abc",
                new String[]{"abcd"},
                "",
                blockSizes
        );

        check(
                "Pattern not found",
                "abcdefgh",
                new String[]{"xyz"},
                "",
                blockSizes
        );

        try {
            new PatternState("", 0);
            System.out.println("FAIL: Empty pattern");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Empty pattern rejected");
        }
    }

}
