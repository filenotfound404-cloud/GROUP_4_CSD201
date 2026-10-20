/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package kkmpexperiment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class KKMPExperiment {

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
                        state.matched =
                                state.pi[state.matched - 1];
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
                        state.matched =
                                state.pi[state.matched - 1];
                    }
                }

                position++;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String text = "abababab";
        String[] patterns = {"abab", "bab", "xyz"};

        try (InputStream input = new ByteArrayInputStream(
                text.getBytes(StandardCharsets.ISO_8859_1))) {

            search(input, patterns, 3);
        }
    }
}

