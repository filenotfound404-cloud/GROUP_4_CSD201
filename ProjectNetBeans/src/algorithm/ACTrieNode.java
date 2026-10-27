package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Đại diện cho một nút/trạng thái trong Cây Trie Aho-Corasick.
 */
public class ACTrieNode {
    public final int id;
    public final Map<Byte, ACTrieNode> children = new HashMap<>();
    public ACTrieNode fail;
    public ACTrieNode outputLink; // Dictionary Suffix Link
    public final List<Integer> patternIds = new ArrayList<>();
    public final List<Integer> patternLengths = new ArrayList<>();

    public ACTrieNode(int id) {
        this.id = id;
    }

    public boolean isOutput() {
        return !patternIds.isEmpty();
    }
}
