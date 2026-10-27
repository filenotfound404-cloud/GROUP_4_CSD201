package algorithm.ahocorasick;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Thuật toán Aho-Corasick biến thể DFA (bảng chuyển trạng thái đầy đủ int[states][256]).
 * Hỗ trợ duy trì chỉ số trạng thái (currState) liên tục qua các ranh giới block B.
 */
public class ACDFAMatcher implements PatternMatching {
    private int[][] dfa;
    private int[][] statePatternIds;
    private int[][] statePatternLens;
    private List<byte[]> patterns = Collections.emptyList();
    private int currState = 0;
    private long globalOffset = 0;

    public ACDFAMatcher() {
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null) {
            throw new IllegalArgumentException("Patterns cannot be null");
        }
        this.patterns = new ArrayList<>(patterns);

        // 1. Dựng cây Trie với danh sách các nút
        List<Node> nodes = new ArrayList<>();
        Node root = new Node(0);
        nodes.add(root);

        for (int pId = 0; pId < patterns.size(); pId++) {
            byte[] pat = patterns.get(pId);
            if (pat == null || pat.length == 0) {
                throw new IllegalArgumentException("Pattern cannot be null or empty");
            }
            Node curr = root;
            for (byte b : pat) {
                int ub = b & 0xFF;
                if (curr.children[ub] == -1) {
                    Node child = new Node(nodes.size());
                    nodes.add(child);
                    curr.children[ub] = child.id;
                }
                curr = nodes.get(curr.children[ub]);
            }
            curr.matchedPids.add(pId);
            curr.matchedLens.add(pat.length);
        }

        int numStates = nodes.size();
        dfa = new int[numStates][256];
        int[] fail = new int[numStates];

        // 2. BFS xây dựng DFA theo Algorithm 4 (Aho-Corasick 1975)
        Queue<Integer> queue = new ArrayDeque<>();

        for (int c = 0; c < 256; c++) {
            int child = root.children[c];
            if (child != -1) {
                dfa[0][c] = child;
                fail[child] = 0;
                queue.add(child);
            } else {
                dfa[0][c] = 0;
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            Node uNode = nodes.get(u);
            int uFail = fail[u];

            // Gộp danh sách output từ trạng thái fail
            Node failNode = nodes.get(uFail);
            uNode.matchedPids.addAll(failNode.matchedPids);
            uNode.matchedLens.addAll(failNode.matchedLens);

            for (int c = 0; c < 256; c++) {
                int v = uNode.children[c];
                if (v != -1) {
                    fail[v] = dfa[uFail][c];
                    dfa[u][c] = v;
                    queue.add(v);
                } else {
                    dfa[u][c] = dfa[uFail][c];
                }
            }
        }

        // 3. Tiền tối ưu hóa output sang mảng nguyên thủy để tra cứu cực nhanh
        statePatternIds = new int[numStates][];
        statePatternLens = new int[numStates][];
        for (int i = 0; i < numStates; i++) {
            Node nd = nodes.get(i);
            int size = nd.matchedPids.size();
            statePatternIds[i] = new int[size];
            statePatternLens[i] = new int[size];
            for (int k = 0; k < size; k++) {
                statePatternIds[i][k] = nd.matchedPids.get(k);
                statePatternLens[i][k] = nd.matchedLens.get(k);
            }
        }

        this.currState = 0;
        this.globalOffset = 0;
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || consumer == null) {
            throw new IllegalArgumentException("Data and consumer cannot be null");
        }
        reset();
        searchBlock(data, 0, consumer);
    }

    /**
     * Quét từng khối block dữ liệu liên tiếp mà không reset trạng thái currState.
     */
    public void searchBlock(byte[] block, long baseOffset, ChecksumConsumer consumer) {
        if (block == null || consumer == null) {
            throw new IllegalArgumentException("Block and consumer cannot be null");
        }
        if (dfa == null || patterns.isEmpty()) {
            return;
        }

        int state = this.currState;
        int blockLen = block.length;

        for (int i = 0; i < blockLen; i++) {
            int ub = block[i] & 0xFF;
            state = dfa[state][ub];

            int[] pids = statePatternIds[state];
            if (pids.length > 0) {
                int[] lens = statePatternLens[state];
                for (int k = 0; k < pids.length; k++) {
                    long matchOffset = (baseOffset + i) - lens[k] + 1;
                    consumer.onMatch(matchOffset, pids[k]);
                }
            }
        }
        this.currState = state;
        this.globalOffset = baseOffset + blockLen;
    }

    @Override
    public String getAlgorithmName() {
        return "AhoCorasick-DFA";
    }

    @Override
    public void reset() {
        this.currState = 0;
        this.globalOffset = 0;
    }

    public int getCurrState() {
        return currState;
    }

    private static class Node {
        final int id;
        final int[] children = new int[256];
        final List<Integer> matchedPids = new ArrayList<>();
        final List<Integer> matchedLens = new ArrayList<>();

        Node(int id) {
            this.id = id;
            java.util.Arrays.fill(children, -1);
        }
    }
}
