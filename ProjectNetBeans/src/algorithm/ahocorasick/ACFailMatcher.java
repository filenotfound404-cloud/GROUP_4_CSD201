package algorithm.ahocorasick;

import algorithm.ChecksumConsumer;
import algorithm.PatternMatching;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Thuật toán Aho-Corasick biến thể Failure Link (thưa) duyệt BFS.
 * Hỗ trợ duy trì trạng thái (currNode) liên tục qua các ranh giới block B.
 */
public class ACFailMatcher implements PatternMatching {
    private ACTrieNode root;
    private List<byte[]> patterns = Collections.emptyList();
    private ACTrieNode currNode;
    private long globalOffset = 0;

    public ACFailMatcher() {
        this.root = new ACTrieNode(0);
        this.currNode = this.root;
    }

    @Override
    public void compilePattern(List<byte[]> patterns) {
        if (patterns == null) {
            throw new IllegalArgumentException("Patterns cannot be null");
        }
        this.patterns = new ArrayList<>(patterns);
        this.root = new ACTrieNode(0);
        int nodeIdSeq = 1;

        // 1. Dựng cây Trie
        for (int pId = 0; pId < patterns.size(); pId++) {
            byte[] pat = patterns.get(pId);
            if (pat == null || pat.length == 0) {
                throw new IllegalArgumentException("Pattern cannot be null or empty");
            }
            ACTrieNode curr = root;
            for (byte b : pat) {
                ACTrieNode child = curr.children.get(b);
                if (child == null) {
                    child = new ACTrieNode(nodeIdSeq++);
                    curr.children.put(b, child);
                }
                curr = child;
            }
            curr.patternIds.add(pId);
            curr.patternLengths.add(pat.length);
        }

        // 2. Duyệt BFS dựng Failure Link và Dictionary Suffix Link (outputLink)
        Queue<ACTrieNode> queue = new ArrayDeque<>();
        root.fail = root;
        root.outputLink = null;

        for (ACTrieNode child : root.children.values()) {
            child.fail = root;
            child.outputLink = null;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            ACTrieNode curr = queue.poll();

            for (java.util.Map.Entry<Byte, ACTrieNode> entry : curr.children.entrySet()) {
                byte b = entry.getKey();
                ACTrieNode child = entry.getValue();

                ACTrieNode f = curr.fail;
                while (f != root && !f.children.containsKey(b)) {
                    f = f.fail;
                }
                if (f.children.containsKey(b) && f.children.get(b) != child) {
                    child.fail = f.children.get(b);
                } else {
                    child.fail = root;
                }

                if (child.fail.isOutput()) {
                    child.outputLink = child.fail;
                } else {
                    child.outputLink = child.fail.outputLink;
                }

                queue.add(child);
            }
        }

        this.currNode = this.root;
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
     * Tìm kiếm trên từng khối block dữ liệu liên tiếp mà không reset con trỏ trạng thái currNode.
     * @param block Mảng byte của block hiện tại
     * @param baseOffset Vị trí bắt đầu của block trong toàn bộ file
     * @param consumer Đối tượng nhận kết quả khớp
     */
    public void searchBlock(byte[] block, long baseOffset, ChecksumConsumer consumer) {
        if (block == null || consumer == null) {
            throw new IllegalArgumentException("Block and consumer cannot be null");
        }
        if (root == null || patterns.isEmpty()) {
            return;
        }

        ACTrieNode curr = this.currNode != null ? this.currNode : root;
        int blockLen = block.length;

        for (int i = 0; i < blockLen; i++) {
            byte b = block[i];

            while (curr != root && !curr.children.containsKey(b)) {
                curr = curr.fail;
            }
            if (curr.children.containsKey(b)) {
                curr = curr.children.get(b);
            } else {
                curr = root;
            }

            // Thu thập kết quả khớp tại nút hiện tại và qua outputLink
            for (ACTrieNode temp = curr; temp != null; temp = temp.outputLink) {
                if (temp.isOutput()) {
                    for (int k = 0; k < temp.patternIds.size(); k++) {
                        int pId = temp.patternIds.get(k);
                        int pLen = temp.patternLengths.get(k);
                        long matchOffset = (baseOffset + i) - pLen + 1;
                        consumer.onMatch(matchOffset, pId);
                    }
                }
            }
        }
        this.currNode = curr;
        this.globalOffset = baseOffset + blockLen;
    }

    @Override
    public String getAlgorithmName() {
        return "AhoCorasick-Fail";
    }

    @Override
    public void reset() {
        this.currNode = this.root;
        this.globalOffset = 0;
    }

    public ACTrieNode getCurrentNode() {
        return currNode;
    }
}
