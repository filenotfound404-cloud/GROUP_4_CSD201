# GROUP 4 - CSD201 (FALL 2026)
# ĐỀ TÀI 3: TÌM MẪU TRONG LOG LỚN: BRUTE FORCE, KMP VÀ AHO–CORASICK
**Lớp:** SE20C | **Nhóm:** Nhóm 4  
**Phân công Cặp 3:** **Lương Triệu Vỹ** *(Aho-Corasick Lead & Visualizer)* & **Nguyễn Bảo Trâm** *(Paper Lead & DFA Optimizer)*

---

## 📌 MỤC LỤC
1. [Tổng quan Đề tài & Kiến trúc Nhóm](#1-tổng-quan-đề-tài--kiến-trúc-nhóm)
2. [Cấu trúc Thư mục Dự án](#2-cấu-trúc-thư-mục-dự-án)
3. [Phân công Nhiệm vụ Thành viên: Lương Triệu Vỹ](#3-phân-công-nhiệm-vụ-thành-viên-lương-triệu-vỹ)
4. [Báo cáo Kiểm tra 6 Subtask của Cặp 3 (Asana)](#4-báo-cáo-kiểm-tra-6-subtask-của-cặp-3-asana)
5. [Giải thích Chi tiết Logic Thuật toán Aho–Corasick](#5-giải-thích-chi-tiết-logic-thuật-toán-aho-corasick)
6. [Nhiệm vụ & Chức năng Từng Class của Cặp 3](#6-nhiệm-vụ--chức-năng-từng-class-của-cặp-3)
7. [Hướng dẫn Chạy Test & Sinh Mẫu](#7-hướng-dẫn-chạy-test--sinh-mẫu)
8. [Hướng dẫn Chạy Script Vẽ Đồ thị Python (plot_results.py)](#8-hướng-dẫn-chạy-script-vẽ-đồ-thị-python-plot_resultspy)
9. [Cẩm nang Vấn đáp Cá nhân 1-1 cho Triệu Vỹ (4.0 Điểm)](#9-cẩm-nang-vấn-đáp-cá-nhân-1-1-cho-triệu-vỹ-40-điểm)

---

## 1. Tổng quan Đề tài & Kiến trúc Nhóm

Dự án nghiên cứu và so sánh hiệu năng của 3 thuật toán tìm kiếm mẫu chuỗi trên file log dung lượng lớn ($16\text{ MiB} \to 1\text{ GiB}$):
1. **Brute Force:** So khớp trượt tuần tự từng vị trí.
2. **KMP (Knuth-Morris-Pratt):** Tối ưu bước nhảy đơn mẫu dựa trên bảng tiền tố $\pi$.
3. **Aho-Corasick:** Otômat tìm kiếm đa mẫu ($k \in \{1, 10, 100, 1000\}$ mẫu) trong đúng 1 lượt quét $O(n)$.

Nhóm chia làm **3 cặp làm việc chéo**:
- **Cặp 1 (Phương Khánh & Văn Sang):** Hạ tầng, Brute Force, Bộ đo Runner và Dữ liệu lớn (RQ1).
- **Cặp 2 (Trường Hải & Hoài Nhi):** KMP, Đa mẫu k-KMP, Bộ kiểm thử tính đúng Oracle (RQ2).
- **Cặp 3 (Triệu Vỹ & Bảo Trâm):** Otômat Aho-Corasick (Fail & DFA), Bộ sinh mẫu RQ3, Trực quan hóa dữ liệu và Paper IEEE (RQ3).

---

## 2. Cấu trúc Thư mục Dự án

```text
GROUP_4_CSD201/
├── PHAN_CONG.md                     # Bảng phân công chi tiết D0 - D14
├── HUONG_DAN_GIT_BRANCH.md          # Quy chuẩn nhánh Git và commit
├── README.md                        # Hướng dẫn kỹ thuật và logic thuật toán (file này)
├── ProjectNetBeansSource/           # Khung source code NetBeans chuẩn
│   └── algorithm/
│       ├── PatternMatching.java     # Interface chuẩn: compilePattern, searchPattern, reset
│       ├── ChecksumConsumer.java    # Bộ tích lũy kết quả: count và checksum
│       └── ProjectTestSuite.java    # Test tổng thể của dự án
├── src/                             # Mã nguồn triển khai thuật toán
│   ├── algorithm/
│   │   └── ahocorasick/
│   │       ├── ACTrieNode.java      # Nút trên Cây Trie (Byte-level)
│   │       ├── ACFailMatcher.java   # Aho-Corasick biến thể Fail Link (duyệt BFS)
│   │       └── ACDFAMatcher.java    # Aho-Corasick biến thể DFA (Algorithm 4)
│   ├── generator/
│   │   └── PatternExtractor.java    # Bộ trích xuất mẫu log và sinh mẫu âm tính
│   └── test/
│       └── AhoCorasickTest.java     # Test Suite chuyên biệt: ushers, equivalence, block boundary
├── bench/
│   └── plot_scripts/
│       └── plot_results.py          # Script Python vẽ 4 biểu đồ IEEE (Pie, Bar, Line)
└── results/
    └── raw/
        ├── benchmark_results.csv    # File CSV chứa số liệu đo đạc chuẩn 22 cột (sinh ở D8-D10)
        └── README.md                # Quy định định dạng CSV
```

---

## 3. Phân công Nhiệm vụ Thành viên: Lương Triệu Vỹ

Căn cứ theo tài liệu [PHAN_CONG.md](PHAN_CONG.md), nhiệm vụ của bạn **Lương Triệu Vỹ** bao gồm:

### Giai đoạn D0 – D2 (Khởi động):
- Thống nhất cấu trúc **22 cột dữ liệu CSV** (20 cột chuẩn Bảng 4 + 2 cột riêng `build_ms`, `throughput_mibs`).
- Đọc tài liệu **Aho–Corasick (1975)**, phân tích cấu trúc Cây Trie trên byte, thuật toán BFS dựng Failure Link và Dictionary Suffix Link.
- Chủ trì viết **Giả thuyết RQ3**: Phân tích sự vượt trội của Aho-Corasick so với $k$-KMP khi $k$ tăng; sự đánh đổi tốc độ $O(1)$ và bộ nhớ RAM của bảng DFA `int[states][256]`.

### Giai đoạn D3 – D6 (Cài đặt & Kiểm thử mã nguồn):
- **Đầu việc 5.1:** Xây dựng cấu trúc Cây Trie trên byte (`ACTrieNode.java`).
- **Đầu việc 5.2:** Cài đặt thuật toán Aho-Corasick biến thể `fail` (`ACFailMatcher.java`).
- **Đầu việc 5.3:** Cài đặt bộ trích xuất và sinh mẫu (`PatternExtractor.java`).
- **Đầu việc 5.4:** Viết script trực quan hóa Python (`bench/plot_scripts/plot_results.py`).
- Phối hợp cùng **Nguyễn Bảo Trâm** hoàn thiện biến thể `dfa` (`ACDFAMatcher.java`) và bộ test (`AhoCorasickTest.java`).

### Giai đoạn D7 – D14 (Thực nghiệm, Paper IEEE & Bảo vệ):
- **Ngày D7:** Trình bày kiến trúc Aho-Corasick và tài liệu tóm tắt 1 trang trước Giảng viên.
- **Đầu việc 5.5:** Chủ trì chạy đo đạc thực nghiệm RQ3 ($k \in \{1, 10, 100, 1000\}$ mẫu) trên file log $T_1$ dung lượng $256\text{ MiB}$ và $1\text{ GiB}$.
- **Đầu việc 5.6:** Vận hành script `plot_results.py` xuất 4–5 biểu đồ khoa học chuẩn IEEE đưa vào `results/plots/`.
- **Đầu việc 5.7:** Soạn thảo mục **V. Results (Subsection RQ3)** và phối hợp viết mục Thảo luận (Discussion).
- **Ngày D14 (Bảo vệ cá nhân):** Trả lời vấn đáp 1-1 với giảng viên: truy vết Trie/Failure link trên chuỗi `ushers`, giải thích sự đánh đổi RAM/Tốc độ của DFA, và thực hiện sửa code trong 2 phút.

---

## 4. Báo cáo Kiểm tra 6 Subtask của Cặp 3 (Asana)

| STT | Tên Subtask trên Asana | File mã nguồn | Người phụ trách | Trạng thái kỹ thuật | Đánh giá chi tiết |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **1** | Xây dựng cấu trúc Cây Trie trên byte | `ACTrieNode.java` | Lương Triệu Vỹ | ✅ **HOÀN THÀNH 100%** | Lưu trữ cạnh con bằng `Map<Byte, ACTrieNode>`, con trỏ `fail`, con trỏ `outputLink`, danh sách `patternIds` và `patternLengths`. |
| **2** | Cài đặt thuật toán Aho-Corasick biến thể fail | `ACFailMatcher.java` | Lương Triệu Vỹ | ✅ **HOÀN THÀNH 100%** | Dựng Trie, BFS xây dựng Failure link và Dictionary Suffix Link; hàm `searchBlock` giữ biến con trỏ `currNode` liên tục qua các block $B$. |
| **3** | Cài đặt thuật toán Aho-Corasick biến thể dfa | `ACDFAMatcher.java` | Lương Triệu Vỹ / Bảo Trâm | ✅ **HOÀN THÀNH 100%** | Algorithm 4 (1975) ma trận `int[numStates][256]`, tra cứu $O(1)$ mỗi byte, tối ưu output sang mảng nguyên thủy `int[][]`. |
| **4** | Viết bộ kiểm thử tính đúng Aho-Corasick | `AhoCorasickTest.java` | Triệu Vỹ / Bảo Trâm | ✅ **HOÀN THÀNH 100%** | Pass cả 3 bài test: ca `"ushers"` (3 match), test tương đương Fail vs DFA trên $k \in \{1, 10, 20\}$, và test ranh giới block $B \in \{1, 2, 3, 7\text{ bytes}\}$. |
| **5** | Cài đặt bộ trích xuất và sinh mẫu | `PatternExtractor.java` | Triệu Vỹ | ✅ **HOÀN THÀNH 100%** | Trích xuất mẫu có trong text cho RQ1/RQ2; sinh tập $k$ mẫu cho RQ3 với tỷ lệ chuẩn 50% có trong log + 50% âm tính (`#!@...`). |
| **6** | Viết biểu đồ bằng code Python từ file CSV | `plot_results.py` | Triệu Vỹ | ✅ **HOÀN THÀNH 100%** | Tự động đọc CSV (hoặc sinh mock chuẩn lý thuyết), xuất 4 biểu đồ: Tròn (RAM footprint), Cột (Throughput), Đường RQ1, Đường RQ3. |

> **KẾT LUẬN:** Toàn bộ **6/6 subtask** của Cặp 3 đã được lập trình đầy đủ, đã qua biên dịch và chạy kiểm thử thực tế thành công 100%.

---

## 5. Giải thích Chi tiết Logic Thuật toán Aho–Corasick

Aho-Corasick là thuật toán tìm kiếm đa mẫu tối ưu nhất, giải quyết bài toán: *Tìm đồng thời $k$ mẫu trong văn bản độ dài $n$*.

### 5.1. Khởi tạo Cây Trie trên Byte (Byte-level Trie)
- Mỗi cạnh nối giữa nút cha và nút con tương ứng với một giá trị byte (`byte` trong Java, giá trị từ $-128 \to 127$, khi chuyển sang chỉ số không dấu là `b & 0xFF` từ $0 \to 255$).
- Nút gốc (Root) có `id = 0`.
- Các nút kết thúc một mẫu sẽ lưu lại `patternId` và `patternLength`.

### 5.2. Thuật toán BFS xây dựng Failure Link & Dictionary Suffix Link
Sau khi dựng cây Trie, thuật toán dùng hàng đợi (Queue) duyệt theo chiều rộng (BFS):
1. **Các nút tầng 1 (con trực tiếp của Root):** Có `fail = root` và `outputLink = null`. Đẩy các nút này vào hàng đợi.
2. **Các nút tầng sâu hơn (`child` của nút `curr` qua byte `b`):**
   - Tìm kiếm trạng thái fail hợp lệ: lần ngược `f = curr.fail`. Nếu `f` không có cạnh chuyển qua byte `b` và `f != root`, tiếp tục nhảy `f = f.fail`.
   - Nếu `f` có cạnh chuyển qua `b`, ta gán `child.fail = f.children.get(b)`. Ngược lại, gán `child.fail = root`.
3. **Dictionary Suffix Link (`outputLink`):**
   - Đây là con trỏ tắt cực kỳ quan trọng giúp không bỏ sót các mẫu lồng nhau (sub-patterns).
   - Nếu nút `child.fail` bản thân nó là một nút kết thúc mẫu (`isOutput() == true`), thì `child.outputLink = child.fail`.
   - Ngược lại, `child.outputLink = child.fail.outputLink`.

### 5.3. Biến thể 1: `ACFailMatcher` (Bộ nhớ Thưa)
- **Cơ chế:** Lưu các cạnh chuyển thực tế bằng bảng băm `Map<Byte, ACTrieNode>`.
- **Lúc quét văn bản:** Khi gặp một byte `b`:
  - Nếu nút hiện tại không có cạnh `b`, lặp con trỏ lùi lại `curr = curr.fail` cho đến khi tìm được nút có cạnh hoặc về đến `root`.
  - Nếu có cạnh thì bước tiếp: `curr = curr.children.get(b)`.
  - Thu thập kết quả: Duyệt `temp = curr; temp != null; temp = temp.outputLink` để kích hoạt tất cả các mẫu kết thúc tại trạng thái này.
- **Ưu điểm:** Bộ nhớ RAM rất nhỏ $O(\text{numNodes})$. Thích hợp hệ thống nhúng ít tài nguyên.
- **Nhược điểm:** Tốc độ quét có thể tốn nhiều hơn 1 bước nhảy cho mỗi byte văn bản.

### 5.4. Biến thể 2: `ACDFAMatcher` (Bảng Chuyển Trạng thái Đầy đủ $O(1)$)
- **Cơ chế:** Áp dụng **Algorithm 4** trong bài báo Aho-Corasick (1975) để phẳng hóa toàn bộ cây Trie thành một ma trận chuyển 2 chiều:
  $$\text{dfa}[numStates][256]$$
- **Lúc tiền xử lý (BFS):**
  - Mọi trạng thái thất bại được tính gộp sẵn vào ma trận: $\text{dfa}[u][c] = \text{dfa}[uFail][c]$ nếu không có nhánh con trực tiếp.
  - Mọi danh sách mẫu khớp được gộp sẵn vào mảng trạng thái `statePatternIds[state]`.
- **Lúc quét văn bản:** Với mỗi byte `b` đọc vào:
  ```java
  state = dfa[state][b & 0xFF]; // Đúng 1 phép tra mảng O(1) duy nhất!
  ```
- **Ưu điểm:** Tốc độ quét đạt trần cực hạn, độc lập tuyệt đối với số lượng mẫu $k$.
- **Đánh đổi (Trade-off):** Ma trận `int[states][256]` tiêu tốn 1 KB RAM cho mỗi trạng thái. Khi $k = 1.000$, số trạng thái lên đến hàng chục nghìn, mảng này tốn hàng chục MB RAM.

### 5.5. Cơ chế Quét Block không bị Reset Trạng thái
- Khi file log lớn hàng trăm MB hoặc 1 GB, hệ thống đọc file thành từng block $B = 1\text{ MiB}$.
- Hàm `searchBlock(byte[] block, long baseOffset, ChecksumConsumer consumer)` nhận vào mảng byte của block và vị trí bắt đầu `baseOffset`.
- Con trỏ trạng thái (`currNode` trong Fail, hoặc `currState` trong DFA) **được giữ nguyên vẹn giữa các lần gọi block**, không bị reset về 0.
- Offset của kết quả khớp được tính tuyệt đối:
  $$\text{matchOffset} = (\text{baseOffset} + i) - \text{patternLength} + 1$$

---

## 6. Nhiệm vụ & Chức năng Từng Class của Cặp 3

### 1. `ACTrieNode.java`
- **Gói:** `algorithm.ahocorasick`
- **Vai trò:** Cấu trúc dữ liệu nền tảng đại diện cho một trạng thái trong Automaton.
- **Thuộc tính chính:**
  - `id`: Mã số định danh nút.
  - `children`: Bản đồ chuyển cạnh `Map<Byte, ACTrieNode>`.
  - `fail`: Con trỏ liên kết thất bại (Failure Link).
  - `outputLink`: Con trỏ liên kết hậu tố từ điển (Dictionary Suffix Link).
  - `patternIds`, `patternLengths`: Danh sách ID và độ dài các mẫu kết thúc tại nút.

### 2. `ACFailMatcher.java`
- **Gói:** `algorithm.ahocorasick`, kế thừa `algorithm.PatternMatching`
- **Vai trò:** Triển khai thuật toán Aho-Corasick dạng cây thưa duyệt BFS.
- **Phương thức chính:**
  - `compilePattern(List<byte[]> patterns)`: Dựng Trie và tính BFS Failure Link + Output Link.
  - `searchPattern(byte[] data, ChecksumConsumer consumer)`: Quét dữ liệu một lần.
  - `searchBlock(byte[] block, long baseOffset, ChecksumConsumer consumer)`: Quét theo từng block 1 MiB liên tục.
  - `reset()`: Đưa con trỏ `currNode` về `root`.

### 3. `ACDFAMatcher.java`
- **Gói:** `algorithm.ahocorasick`, kế thừa `algorithm.PatternMatching`
- **Vai trò:** Triển khai Otômat hữu hạn tiền định (DFA) theo Algorithm 4.
- **Phương thức chính:**
  - `compilePattern(List<byte[]> patterns)`: Dựng ma trận `int[numStates][256]` và gộp output vào mảng nguyên thủy `int[][]`.
  - `searchBlock(byte[] block, long baseOffset, ChecksumConsumer consumer)`: Quét cực nhanh bằng tra mảng 1 dòng `state = dfa[state][b & 0xFF]`.

### 4. `PatternExtractor.java`
- **Gói:** `generator`
- **Vai trò:** Bộ sinh mẫu kiểm thử khoa học phục vụ chạy đo đạc.
- **Phương thức chính:**
  - `extractSinglePattern(byte[] text, int len, Random rng)`: Cắt mẫu ngẫu nhiên có thật trong log (dùng cho RQ1, RQ2).
  - `generateNegativePattern(byte[] text, int len, Random rng)`: Sinh chuỗi âm tính có tiền tố đặc biệt `#!@...` đảm bảo không xuất hiện trong log.
  - `generatePatternsForRQ3(byte[] text, int k, int minLen, int maxLen, long seed)`: Sinh $k \in \{1, 10, 100, 1000\}$ mẫu theo tỷ lệ 50% log + 50% âm tính, trộn ngẫu nhiên.

### 5. `AhoCorasickTest.java`
- **Gói:** `test`
- **Vai trò:** Bộ kiểm thử tính đúng độc lập và nghiêm ngặt cho Aho-Corasick.
- **3 Ca kiểm thử chuẩn:**
  1. `testUshers()`: Kiểm tra ca kinh điển `"ushers"` với 4 từ `{"he", "she", "his", "hers"}` để khẳng định Dictionary Suffix Link hoạt động chính xác (bắt được cả `"he"` lồng trong `"she"`).
  2. `testEquivalence()`: Kiểm tra tương đương: chạy đồng thời `ACFailMatcher` và `ACDFAMatcher` trên $k \in \{1, 10, 20\}$ mẫu, đối chiếu `count:checksum` trùng khớp 100%.
  3. `testBlockBoundary()`: Quét thử nghiệm với các block cực nhỏ $B \in \{1, 2, 3, 7\text{ bytes}\}$ so với quét toàn bộ một lần, khẳng định không bị mất mẫu tại ranh giới block.

### 6. `plot_results.py`
- **Thư mục:** `bench/plot_scripts/`
- **Vai trò:** Trực quan hóa dữ liệu nghiên cứu khoa học đạt chuẩn bài báo IEEE (300 DPI).
- **4 Biểu đồ sinh ra:**
  - `fig1_pie_memory_comparison.png`: Biểu đồ tròn so sánh tỷ trọng tiêu thụ RAM (Heap Footprint) khi $k = 1.000$ giữa Brute Force, KMP, AC-Fail và AC-DFA (làm nổi bật chi phí bộ nhớ của ma trận DFA).
  - `fig2_bar_throughput_comparison.png`: Biểu đồ cột so sánh thông lượng Throughput (MiB/s) trên file log lớn.
  - `fig3_line_rq1_scaling.png`: Biểu đồ đường RQ1 so sánh sự tăng trưởng thời gian khi file log tăng từ $16\text{ MiB} \to 1.024\text{ MiB}$.
  - `fig4_line_rq3_multi_pattern.png`: Biểu đồ đường RQ3 chứng minh thời gian của $k$-KMP tăng vọt khi $k = 1.000$, trong khi AC-Fail và AC-DFA giữ đường nằm ngang ổn định.

---

## 7. Hướng dẫn Chạy Test & Sinh Mẫu

Dự án tương thích hoàn toàn với JDK 1.8 và các phiên bản Java mới hơn.

### 7.1. Chạy Bộ kiểm thử Tính đúng Aho-Corasick:
Mở PowerShell tại thư mục gốc của repository:
```powershell
# Tạo thư mục biên dịch tạm
mkdir -Force build_temp

# Biên dịch các class liên quan
javac -encoding UTF-8 -cp "ProjectNetBeansSource;src" -d "build_temp" ProjectNetBeansSource/algorithm/*.java src/algorithm/ahocorasick/*.java src/test/AhoCorasickTest.java

# Chạy kiểm thử
java -cp "build_temp" test.AhoCorasickTest

# Dọn dẹp
Remove-Item -Recurse -Force build_temp
```
*Kết quả kỳ vọng:* Xuất ra dòng chữ `>>> [100% PASS] TẤT CẢ CÁC BÀI TEST TÍNH ĐÚNG AHO-CORASICK ĐÃ ĐẠT! <<<`.

### 7.2. Chạy Demo Bộ sinh mẫu (PatternExtractor):
```powershell
mkdir -Force build_temp
javac -encoding UTF-8 -cp "src" -d "build_temp" src/generator/PatternExtractor.java
java -cp "build_temp" generator.PatternExtractor
Remove-Item -Recurse -Force build_temp
```
*Kết quả kỳ vọng:* Sinh mẫu ngẫu nhiên từ log và mẫu âm tính với tỷ lệ 50-50 chuẩn xác.

---

## 8. Vai trò của Script Vẽ Đồ thị Python (plot_results.py)

Script `bench/plot_scripts/plot_results.py` là **công cụ chuẩn bị sẵn cho giai đoạn D11–D12** (Trực quan hóa dữ liệu sau khi hoàn thành đo đạc benchmark ở D8–D10).

> [!NOTE]
> - **Giai đoạn hiện tại (D3–D6):** Chỉ tập trung vào mã nguồn thuật toán và Unit Test. **Tuyệt đối không commit các file hình ảnh PNG vào Git** ở giai đoạn này để tránh chứa dữ liệu giả lập (mock data), đảm bảo tính liêm chính học thuật theo quy định CSD201.
> - **Giai đoạn D11–D12:** Sau khi Cặp 1 và Cặp 3 chạy xong bộ đo Runner và có file CSV đo thực tế trên máy, script này sẽ được chạy để tự động sinh 5 biểu đồ khoa học đưa trực tiếp vào thư mục chuẩn `paper/figures/` (hoặc `results/figures/`) phục vụ chèn vào bài báo IEEE.

---

## 9. Cẩm nang Vấn đáp Cá nhân 1-1 cho Triệu Vỹ (4.0 Điểm)

Khi bảo vệ cá nhân 1-1 với Giảng viên (5 phút, chiếm 4/10 điểm), bạn Triệu Vỹ cần nắm chắc 4 nội dung sau:

### Câu 1: Truy vết Cây Trie & Failure Link trên giấy nháp
- **Đề bài mẫu:** *“Vẽ cây Trie và mũi tên Failure Link, Output Link cho tập mẫu `he`, `she`, `his`, `hers` trên chuỗi `ushers`”*.
- **Cách trả lời:**
  1. Vẽ các nhánh từ root: nhánh `h-e`, nhánh `s-h-e`, nhánh `h-i-s`, nhánh `h-e-r-s`.
  2. Nút `s` (con root) có fail về `root`. Nút `sh` có fail về nút `h`. Nút `she` có fail về nút `he`.
  3. Chỉ rõ: Nút `she` có **Dictionary Suffix Link (outputLink)** trỏ thẳng tới nút `he`, vì vậy khi quét đến ký tự `e` thứ 4 của `"ushers"`, thuật toán nhận ra trạng thái `she` đồng thời kích hoạt tìm thấy cả từ con `"he"` tại cùng vị trí!

### Câu 2: Sự đánh đổi giữa `AC-Fail` và `AC-DFA` (Trade-off)
- **Hỏi:** *“Tại sao nhóm lại làm cả 2 biến thể Fail và DFA? Biến thể nào tốt hơn?”*
- **Trả lời:**
  - Không có cái nào hoàn toàn tốt hơn, mà là **sự đánh đổi giữa Tốc độ và Bộ nhớ RAM**:
  - `AC-DFA`: Tốc độ nhanh nhất vì chỉ tốn đúng 1 phép tra mảng `int[state][byte]`, nhưng bộ nhớ phình to khi $k = 1.000$ (tốn hàng chục MB RAM).
  - `AC-Fail`: Tiết kiệm RAM tối đa vì chỉ lưu các nhánh thực tế, nhưng lúc quét có thể phải lùi con trỏ qua Failure Link nhiều lần.
  - **Ứng dụng thực tế:** Hệ thống mạng hiệu năng cao như Snort IDS hoặc công cụ ripgrep chọn DFA; thiết bị nhúng/IoT ít RAM chọn Fail.

### Câu 3: So sánh Aho-Corasick với $k$-KMP (RQ3)
- **Hỏi:** *“Tại sao khi $k = 1$ KMP có thể nhanh, nhưng $k = 1.000$ thì KMP lại thua xa Aho-Corasick?”*
- **Trả lời:**
  - $k$-KMP phải duy trì $k$ biến trạng thái độc lập. Khi đọc 1 byte, nó phải chạy vòng lặp `for` từ $1 \to k$, độ phức tạp quét là $O(n \times k)$.
  - Aho-Corasick gom toàn bộ $k$ mẫu vào 1 Automaton duy nhất, thời gian quét là $O(n)$ hoàn toàn độc lập với $k$.

### Câu 4: Thao tác sửa code trực tiếp trong 2 phút trên NetBeans
- **Tình huống thường gặp:** Giảng viên yêu cầu đổi cách tính offset hoặc thêm biến đếm số lần nhảy failure link.
- **Điểm cần nhớ:**
  - Trong `ACFailMatcher.java`, vị trí tính offset là dòng 140:
    `long matchOffset = (baseOffset + i) - pLen + 1;`
  - Vòng lặp nhảy failure link lúc quét là dòng 125:
    `while (curr != root && !curr.children.containsKey(b)) { curr = curr.fail; }`
