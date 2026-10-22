package algorithm.ahocorasick;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Thuật toán Aho-Corasick biến thể Failure Link (ac, variant fail).
 * 
 * Đặc tả kỹ thuật & Quy ước đề tài:
 * - Dựng Cây Trie trên byte từ tập k mẫu; mỗi cạnh con lưu dạng thưa (sparse HashMap).
 * - Xây dựng Failure Link và Output Link (Dictionary Suffix Link) bằng duyệt BFS.
 * - Khi quét văn bản: nếu không có cạnh chuyển thì lần theo failure link lúc runtime.
 * - Duy trì con trỏ trạng thái (currentState) liên tục qua các ranh giới block kích thước B.
 * - Hỗ trợ đếm riêng số phép chuyển trạng thái và số lần nhảy failure link khi chạy --count.
 */
public class ACFailMatcher implements PatternMatching {

    private ACTrieNode root;
    private int stateCount;
    
    // Trạng thái duy trì qua các ranh giới block
    private ACTrieNode currentState;
    private long globalOffset;

    // Bộ đếm thao tác phục vụ cờ --count
    private boolean countMode = false;
    private long stateTransitions = 0;
    private long failureTraversals = 0;

    public ACFailMatcher() {
        this.root = new ACTrieNode(0);
        this.stateCount = 1;
        this.currentState = root;
        this.globalOffset = 0;
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Pattern list cannot be null or empty");
        }

        this.root = new ACTrieNode(0);
        this.stateCount = 1;

        // 1. Nạp tất cả các mẫu vào Cây Trie
        for (int id = 0; id < patterns.size(); id++) {
            byte[] pat = patterns.get(id);
            if (pat == null || pat.length == 0) {
                throw new IllegalArgumentException("Pattern at index " + id + " cannot be empty or null");
            }
            insertPattern(pat, id);
        }

        // 2. Dựng Failure Link và Output Link bằng thuật toán BFS
        buildFailureAndOutputLinks();

        // Đặt lại trạng thái ban đầu
        reset();
    }

    private void insertPattern(byte[] pat, int patternId) {
        ACTrieNode curr = root;
        for (byte b : pat) {
            int byteVal = b & 0xFF; // Chuẩn hóa byte không dấu 0..255
            ACTrieNode next = curr.getChild(byteVal);
            if (next == null) {
                next = new ACTrieNode(stateCount++);
                curr.putChild(byteVal, next);
            }
            curr = next;
        }
        curr.addPattern(patternId, pat.length);
    }

    private void buildFailureAndOutputLinks() {
        Queue<ACTrieNode> queue = new ArrayDeque<>();

        // Các con trực tiếp của Root có Failure Link trỏ về Root
        for (Map.Entry<Integer, ACTrieNode> entry : root.getChildren().entrySet()) {
            ACTrieNode child = entry.getValue();
            child.setFail(root);
            child.setOutputLink(null);
            queue.add(child);
        }

        // Duyệt BFS các tầng tiếp theo
        while (!queue.isEmpty()) {
            ACTrieNode u = queue.poll();

            for (Map.Entry<Integer, ACTrieNode> entry : u.getChildren().entrySet()) {
                int byteVal = entry.getKey();
                ACTrieNode v = entry.getValue();

                // Tìm failure link cho node v
                ACTrieNode f = u.getFail();
                while (f != null && !f.hasChild(byteVal)) {
                    f = f.getFail();
                }

                if (f != null) {
                    v.setFail(f.getChild(byteVal));
                } else {
                    v.setFail(root);
                }

                // Dựng Output Link (Dictionary Suffix Link)
                ACTrieNode failNode = v.getFail();
                if (failNode.isEndOfPattern()) {
                    v.setOutputLink(failNode);
                } else {
                    v.setOutputLink(failNode.getOutputLink());
                }

                queue.add(v);
            }
        }
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0) return;
        searchBlock(data, data.length, consumer);
    }

    /**
     * Quét một khối dữ liệu (block kích thước B) mà không bị mất dấu trạng thái giữa các block.
     */
    public void searchBlock(byte[] block, int length, ChecksumConsumer consumer) {
        if (block == null || length <= 0) return;

        for (int i = 0; i < length; i++) {
            int byteVal = block[i] & 0xFF;

            // Nếu không có cạnh chuyển, lần theo failure link
            while (currentState != null && !currentState.hasChild(byteVal)) {
                if (currentState == root) {
                    // Đã ở root và không có cạnh đi tiếp
                    break;
                }
                if (countMode) failureTraversals++;
                currentState = currentState.getFail();
            }

            if (currentState.hasChild(byteVal)) {
                if (countMode) stateTransitions++;
                currentState = currentState.getChild(byteVal);
            } else {
                currentState = root;
            }

            // Thu hoạch kết quả nếu trạng thái hiện tại là kết thúc của mẫu
            collectMatches(currentState, globalOffset, consumer);

            globalOffset++;
        }
    }

    @Override
    public void searchStream(InputStream in, int bufferSize, ChecksumConsumer consumer) throws IOException {
        if (in == null) return;
        byte[] buffer = new byte[bufferSize > 0 ? bufferSize : 1024 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            searchBlock(buffer, bytesRead, consumer);
        }
    }

    private void collectMatches(ACTrieNode node, long currentOffset, ChecksumConsumer consumer) {
        // 1. Mẫu kết thúc trực tiếp tại node
        if (node.isEndOfPattern()) {
            List<Integer> ids = node.getMatchedPatternIds();
            List<Integer> lens = node.getMatchedPatternLengths();
            for (int i = 0; i < ids.size(); i++) {
                long startOffset = currentOffset - lens.get(i) + 1;
                consumer.onMatch(startOffset, ids.get(i));
            }
        }

        // 2. Các mẫu lồng nhau thông qua Dictionary Suffix Link (outputLink)
        ACTrieNode out = node.getOutputLink();
        while (out != null) {
            List<Integer> ids = out.getMatchedPatternIds();
            List<Integer> lens = out.getMatchedPatternLengths();
            for (int i = 0; i < ids.size(); i++) {
                long startOffset = currentOffset - lens.get(i) + 1;
                consumer.onMatch(startOffset, ids.get(i));
            }
            out = out.getOutputLink();
        }
    }

    @Override
    public String getAlgorithmName() {
        return "ac, variant fail";
    }

    @Override
    public void reset() {
        this.currentState = root;
        this.globalOffset = 0;
        this.stateTransitions = 0;
        this.failureTraversals = 0;
    }

    // Các hàm phục vụ đo lường chỉ số (RQ3 Benchmark)
    public void setCountMode(boolean countMode) {
        this.countMode = countMode;
    }

    public long getStateTransitions() {
        return stateTransitions;
    }

    public long getFailureTraversals() {
        return failureTraversals;
    }

    public int getStateCount() {
        return stateCount;
    }

    public ACTrieNode getRoot() {
        return root;
    }
}
