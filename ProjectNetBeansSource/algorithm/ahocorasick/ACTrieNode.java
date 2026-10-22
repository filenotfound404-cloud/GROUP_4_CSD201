package algorithm.ahocorasick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ACTrieNode đại diện cho một nút (trạng thái) trong Cây Trie trên byte của Otômat Aho-Corasick.
 * 
 * Đặc tả kỹ thuật:
 * - Thao tác trực tiếp trên giá trị byte không dấu (b & 0xFF, từ 0 đến 255).
 * - Lưu trữ các cạnh con dạng thưa (sparse) bằng HashMap<Integer, ACTrieNode> để tiết kiệm bộ nhớ.
 * - Con trỏ fail (Failure Link): trỏ tới tiền tố dài nhất trùng với hậu tố riêng.
 * - Con trỏ outputLink (Dictionary Suffix Link): trỏ tới nút gần nhất trên đường đi fail có kết thúc mẫu,
 *   giúp phát hiện đầy đủ các mẫu lồng nhau (như "he" trong "she").
 */
public class ACTrieNode {

    private final int stateId; // Định danh trạng thái (Root = 0)
    private final Map<Integer, ACTrieNode> children; // Cạnh con thưa (key: 0..255)
    
    private ACTrieNode fail; // Failure link
    private ACTrieNode outputLink; // Dictionary suffix link
    
    private final List<Integer> matchedPatternIds; // Danh sách ID mẫu kết thúc tại nút
    private final List<Integer> matchedPatternLengths; // Độ dài tương ứng của mẫu kết thúc tại nút

    public ACTrieNode(int stateId) {
        this.stateId = stateId;
        this.children = new HashMap<>();
        this.fail = null;
        this.outputLink = null;
        this.matchedPatternIds = new ArrayList<>();
        this.matchedPatternLengths = new ArrayList<>();
    }

    public int getStateId() {
        return stateId;
    }

    public Map<Integer, ACTrieNode> getChildren() {
        return children;
    }

    public ACTrieNode getChild(int byteVal) {
        return children.get(byteVal);
    }

    public boolean hasChild(int byteVal) {
        return children.containsKey(byteVal);
    }

    public void putChild(int byteVal, ACTrieNode child) {
        children.put(byteVal, child);
    }

    public ACTrieNode getFail() {
        return fail;
    }

    public void setFail(ACTrieNode fail) {
        this.fail = fail;
    }

    public ACTrieNode getOutputLink() {
        return outputLink;
    }

    public void setOutputLink(ACTrieNode outputLink) {
        this.outputLink = outputLink;
    }

    public List<Integer> getMatchedPatternIds() {
        return matchedPatternIds;
    }

    public List<Integer> getMatchedPatternLengths() {
        return matchedPatternLengths;
    }

    public void addPattern(int patternId, int length) {
        this.matchedPatternIds.add(patternId);
        this.matchedPatternLengths.add(length);
    }

    public boolean isEndOfPattern() {
        return !matchedPatternIds.isEmpty();
    }
}
