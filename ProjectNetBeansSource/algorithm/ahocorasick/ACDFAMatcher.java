package algorithm.ahocorasick;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Thuật toán Aho-Corasick biến thể Bảng chuyển đầy đủ DFA (ac, variant dfa).
 * 
 * Đặc tả kỹ thuật & Quy ước đề tài:
 * - Cài đặt Algorithm 4 trong bài báo gốc của Aho và Corasick (1975).
 * - Tiền tính toán bảng chuyển trạng thái hoàn chỉnh: int[stateCount][256].
 * - Khi quét văn bản: mỗi byte chỉ tốn đúng 1 phép tra mảng O(1), không duyệt failure link lúc runtime.
 * - Tiền gộp tất cả các mẫu trùng (gồm cả Dictionary Suffix Link) vào từng trạng thái.
 * - Khi đo --count: failure_link_traversals lúc quét luôn bằng 0.
 * - Đánh đổi: tốc độ quét tối ưu O(1) nhưng tốn ít nhất (stateCount * 256 * 4) byte cho bảng int.
 * - Duy trì trạng thái int currentState liên tục qua các ranh giới block kích thước B.
 */
public class ACDFAMatcher implements PatternMatching {

    private int stateCount;
    private int[][] dfa; // Bảng chuyển trạng thái Algorithm 4: dfa[state][byteVal]
    
    // Mảng phẳng tiền gộp danh sách mẫu trùng cho từng trạng thái
    private int[][] matchedPatternIds;
    private int[][] matchedPatternLengths;

    // Trạng thái quét duy trì qua các block
    private int currentState;
    private long globalOffset;

    // Bộ đếm thao tác phục vụ cờ --count
    private boolean countMode = false;
    private long stateTransitions = 0;
    private final long failureTraversals = 0; // Luôn bằng 0 trong biến thể DFA khi quét

    public ACDFAMatcher() {
        this.stateCount = 1;
        this.dfa = new int[1][256];
        this.matchedPatternIds = new int[1][0];
        this.matchedPatternLengths = new int[1][0];
        this.currentState = 0;
        this.globalOffset = 0;
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Pattern list cannot be null or empty");
        }

        ACTrieNode root = new ACTrieNode(0);
        this.stateCount = 1;

        // 1. Nạp các mẫu vào Cây Trie
        for (int id = 0; id < patterns.size(); id++) {
            byte[] pat = patterns.get(id);
            if (pat == null || pat.length == 0) {
                throw new IllegalArgumentException("Pattern at index " + id + " cannot be empty or null");
            }
            insertPattern(root, pat, id);
        }

        // 2. Dựng Failure Link & Output Link bằng BFS
        List<ACTrieNode> allNodes = buildFailureAndOutputLinks(root);

        // 3. Xây dựng Bảng chuyển trạng thái đầy đủ (Algorithm 4 - Aho & Corasick 1975)
        buildDFATable(root, allNodes);

        // 4. Tiền gộp danh sách mẫu trùng cho từng trạng thái (để không cần duyệt outputLink lúc quét)
        precomputeMergedOutputs(allNodes);

        reset();
    }

    private void insertPattern(ACTrieNode root, byte[] pat, int patternId) {
        ACTrieNode curr = root;
        for (byte b : pat) {
            int byteVal = b & 0xFF;
            ACTrieNode next = curr.getChild(byteVal);
            if (next == null) {
                next = new ACTrieNode(stateCount++);
                curr.putChild(byteVal, next);
            }
            curr = next;
        }
        curr.addPattern(patternId, pat.length);
    }

    private List<ACTrieNode> buildFailureAndOutputLinks(ACTrieNode root) {
        List<ACTrieNode> allNodes = new ArrayList<>(stateCount);
        allNodes.add(root);

        Queue<ACTrieNode> queue = new ArrayDeque<>();
        for (Map.Entry<Integer, ACTrieNode> entry : root.getChildren().entrySet()) {
            ACTrieNode child = entry.getValue();
            child.setFail(root);
            child.setOutputLink(null);
            queue.add(child);
            allNodes.add(child);
        }

        while (!queue.isEmpty()) {
            ACTrieNode u = queue.poll();

            for (Map.Entry<Integer, ACTrieNode> entry : u.getChildren().entrySet()) {
                int byteVal = entry.getKey();
                ACTrieNode v = entry.getValue();

                ACTrieNode f = u.getFail();
                while (f != null && !f.hasChild(byteVal)) {
                    f = f.getFail();
                }

                if (f != null) {
                    v.setFail(f.getChild(byteVal));
                } else {
                    v.setFail(root);
                }

                ACTrieNode failNode = v.getFail();
                if (failNode.isEndOfPattern()) {
                    v.setOutputLink(failNode);
                } else {
                    v.setOutputLink(failNode.getOutputLink());
                }

                queue.add(v);
                allNodes.add(v);
            }
        }

        return allNodes;
    }

    /**
     * Algorithm 4 (Aho & Corasick 1975):
     * Tính toán bảng chuyển trạng thái ma trận dfa[state][byteVal].
     */
    private void buildDFATable(ACTrieNode root, List<ACTrieNode> allNodes) {
        this.dfa = new int[stateCount][256];

        // 1. Khởi tạo cho Root (State 0)
        for (int c = 0; c < 256; c++) {
            ACTrieNode child = root.getChild(c);
            if (child != null) {
                dfa[0][c] = child.getStateId();
            } else {
                dfa[0][c] = 0;
            }
        }

        // 2. Tính cho các trạng thái u theo thứ tự BFS (đảm bảo fail(u) đã được tính trước)
        for (ACTrieNode u : allNodes) {
            int uId = u.getStateId();
            if (uId == 0) continue;

            int failId = u.getFail() != null ? u.getFail().getStateId() : 0;

            for (int c = 0; c < 256; c++) {
                ACTrieNode child = u.getChild(c);
                if (child != null) {
                    dfa[uId][c] = child.getStateId();
                } else {
                    // Kế thừa chuyển tiếp từ trạng thái fail
                    dfa[uId][c] = dfa[failId][c];
                }
            }
        }
    }

    /**
     * Tiền gộp tất cả các mẫu trùng khớp cho từng trạng thái.
     * Khi quét chỉ cần lặp qua mảng phẳng này mà không tốn công duyệt danh sách liên kết.
     */
    private void precomputeMergedOutputs(List<ACTrieNode> allNodes) {
        this.matchedPatternIds = new int[stateCount][];
        this.matchedPatternLengths = new int[stateCount][];

        for (ACTrieNode node : allNodes) {
            int stateId = node.getStateId();
            List<Integer> ids = new ArrayList<>();
            List<Integer> lens = new ArrayList<>();

            // Thu thập mẫu tại nút
            ids.addAll(node.getMatchedPatternIds());
            lens.addAll(node.getMatchedPatternLengths());

            // Thu thập mẫu từ các nút trên đường đi outputLink
            ACTrieNode out = node.getOutputLink();
            while (out != null) {
                ids.addAll(out.getMatchedPatternIds());
                lens.addAll(out.getMatchedPatternLengths());
                out = out.getOutputLink();
            }

            if (!ids.isEmpty()) {
                int size = ids.size();
                matchedPatternIds[stateId] = new int[size];
                matchedPatternLengths[stateId] = new int[size];
                for (int i = 0; i < size; i++) {
                    matchedPatternIds[stateId][i] = ids.get(i);
                    matchedPatternLengths[stateId][i] = lens.get(i);
                }
            } else {
                matchedPatternIds[stateId] = null;
                matchedPatternLengths[stateId] = null;
            }
        }
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        if (data == null || data.length == 0) return;
        searchBlock(data, data.length, consumer);
    }

    /**
     * Quét theo khối block kích thước B.
     * Mỗi byte chỉ tốn đúng 1 phép tra mảng O(1), duy trì currentState liên tục qua các block.
     */
    public void searchBlock(byte[] block, int length, ChecksumConsumer consumer) {
        if (block == null || length <= 0) return;

        for (int i = 0; i < length; i++) {
            int byteVal = block[i] & 0xFF;

            // 1 PHÉP TRA BẢNG O(1) DUY NHẤT
            currentState = dfa[currentState][byteVal];

            if (countMode) stateTransitions++;

            // Thu hoạch kết quả đã được tiền gộp sẵn
            int[] ids = matchedPatternIds[currentState];
            if (ids != null) {
                int[] lens = matchedPatternLengths[currentState];
                for (int j = 0; j < ids.length; j++) {
                    long startOffset = globalOffset - lens[j] + 1;
                    consumer.onMatch(startOffset, ids[j]);
                }
            }

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

    @Override
    public String getAlgorithmName() {
        return "ac, variant dfa";
    }

    @Override
    public void reset() {
        this.currentState = 0;
        this.globalOffset = 0;
        this.stateTransitions = 0;
    }

    // Các hàm phục vụ đo lường chỉ số (RQ3 Benchmark)
    public void setCountMode(boolean countMode) {
        this.countMode = countMode;
    }

    public long getStateTransitions() {
        return stateTransitions;
    }

    public long getFailureTraversals() {
        // Luôn luôn là 0 trong giai đoạn quét của biến thể DFA
        return failureTraversals;
    }

    public int getStateCount() {
        return stateCount;
    }

    /**
     * Ước tính dung lượng bộ nhớ dành riêng cho bảng chuyển DFA (byte).
     */
    public long getEstimatedMemoryBytes() {
        return (long) stateCount * 256 * 4;
    }
}
