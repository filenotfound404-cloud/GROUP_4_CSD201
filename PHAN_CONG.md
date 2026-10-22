# BẢNG PHÂN CÔNG CÔNG VIỆC CHI TIẾT - ĐỒ ÁN CSD201 (FALL 2026)
**Đề tài 3:** Tìm mẫu trong log lớn: Brute Force, KMP và Aho–Corasick  
**Lớp:** SE20C | **Nhóm thực hiện:** Nhóm 4

---

## I. NGUYÊN TẮC VẬN HÀNH NHÓM:
1. **Quy mô & Mô hình:** Nhóm gồm **6 sinh viên**, chia thành **3 cặp làm việc chéo**.
2. **Cơ chế phản biện chéo (Cross-Review):**
   * **Cặp 1** kiểm tra và review code của **Cặp 2**.
   * **Cặp 2** kiểm tra và review code của **Cặp 3**.
   * **Cặp 3** kiểm tra và review code của **Cặp 1**.
3. **Trách nhiệm cá nhân khi bảo vệ (Chiếm 4.0/10 điểm):** Tự tay cài đặt, kiểm thử một phần kỹ thuật và nắm chắc câu hỏi nghiên cứu (RQ) của cặp mình để trả lời vấn đáp cá nhân 1-1 với Giảng viên.

---

## II. BẢNG PHÂN CÔNG CHI TIẾT GIAI ĐOẠN D0–D2 (3 NGÀY ĐẦU - ĐÃ HOÀN THÀNH)

| Cặp | Thành viên & Vai trò | Nhiệm vụ cụ thể trong 3 ngày (D0 - D2) | Sản phẩm phải nộp cuối D2 |
| :---: | :--- | :--- | :--- |
| **CẶP 1** | **Đinh Vũ Phương Khánh**<br>|- Khởi tạo GitHub repository `GROUP_4_CSD201` và mời Giảng viên làm Collaborator.<br>- Thiết kế file Java chuẩn: Interface `PatternMatcher.java` và lớp `ChecksumConsumer.java`.<br>- **Chủ trì viết Giả thuyết cho RQ1** (So sánh Brute Force vs KMP khi kích thước log tăng từ 16 MB đến 1 GB).<br>| - File code `PatternMatcher.java` & `ChecksumConsumer.java`.<br>- Link GitHub đã add GV.<br>|
| **CẶP 1** | **Hà Văn Sang** |- Cùng Phương Khánh phân tích lý do khoa học tại sao Brute Force có thể chạy nhanh hơn KMP trên log thực tế để hoàn thiện giả thuyết RQ1.<br>- Viết file `PHAN_CONG.md`. | <br>- File `PHAN_CONG.md`.<br>- Đoạn phân tích giả thuyết RQ1. |
| **CẶP 2** | **Nguyễn Hoài Nhi**<br>*|- Đọc tài liệu **[1] KMP gốc (Knuth 1977)** và **[3] Sedgewick §5.3**, nắm vững cách tính bảng tiền tố $\pi$.<br>- Thiết kế giải pháp kỹ thuật: Cách giữ biến trạng thái $j$ (số ký tự đã khớp) liên tục khi đọc qua ranh giới các block kích thước $B$.<br>- **Chủ trì viết Giả thuyết cho RQ2** (Ảnh hưởng của kích thước bảng chữ cái $\sigma$, độ dài mẫu $m$, và tập dữ liệu bất lợi $T_4$ 'aaaa...b'). | - Bản thiết kế thuật toán giữ trạng thái KMP qua block.<br>- Đoạn phân tích giả thuyết RQ2. |
| **CẶP 2** | **Nguyễn Đặng Trường Hải**| - Cùng TV3 làm rõ điều kiện nào khiến Brute Force bị thoái hóa về $O(n \times m)$ còn KMP giữ vững $O(n)$ trên dữ liệu $T_4$. | - Danh sách kịch bản test tính đúng cho ngày D3.<br>- Bản thiết kế mảng trạng thái của k-KMP. |
| **CẶP 3** | **[Lương Triều Vỹ]**<br>*(Kỹ sư Aho-Corasick Lead & Trực quan hóa)*<br>MSSV: `[Điền MSSV 5]` |- Thống nhất danh sách **22 cột dữ liệu của file CSV** (20 cột chuẩn Bảng 4 + 2 cột riêng `build_ms`, `throughput_mibs`).<br> - Đọc tài liệu **[2] Aho–Corasick gốc (1975)**, phân tích cấu trúc cây Trie trên byte, thuật toán BFS dựng Failure Link và Dictionary Suffix Link.<br>- **Chủ trì viết Giả thuyết cho RQ3** (So sánh Aho-Corasick vs k-KMP khi $k$ tăng từ 1 đến 1.000 mẫu; sự đánh đổi giữa tốc độ $O(1)$ và bộ nhớ RAM của DFA). | - Bản mô tả định dạng 22 cột CSV.<br>- Đoạn phân tích giả thuyết RQ3. |
| **CẶP 3** | **[Nguyễn Bảo Trâm]**<br>*(Chủ biên Paper IEEE & Tối ưu DFA)*<br>MSSV: `[Điền MSSV 6]` | - Tải mẫu bài báo hội nghị IEEE Conference Template (`.docx` 2 cột chuẩn từ IEEE Author Center).<br>- Tạo khung bài báo (Paper Skeleton): Điền Title tiếng Anh, danh sách 6 tác giả (FPT University), Abstract nháp, Keywords, và 7 đề mục lớn (I -> VII).<br>- Cùng Vỹ tính toán lý thuyết mức độ phình to bộ nhớ của bảng DFA (`int[states][256]`) khi $k=1.000$ để đưa vào giả thuyết RQ3.<br>- Soạn thảo mục Khai báo sử dụng AI (AI Declaration) theo chuẩn IEEE. | - File Word khung bài báo IEEE (`paper/Group4_Paper.docx`).<br>- Đoạn phân tích bộ nhớ DFA cho giả thuyết RQ3. |

---

## III. BẢNG PHÂN CÔNG CHI TIẾT GIAI ĐOẠN D3–D6 (4 NGÀY TIẾP THEO - TRỌNG TÂM HIỆN TẠI)
**Mục tiêu giai đoạn D3–D6:** Hoàn thành 100% mã nguồn thuật toán, vượt qua toàn bộ unit test, bộ đo Runner xuất được file CSV hợp lệ để sẵn sàng cho buổi kiểm tra tiến độ D7.

---

### 🔵 CẶP 1: HẠ TẦNG HỆ THỐNG, THUẬT TOÁN BRUTE FORCE & BỘ ĐO RUNNER

#### 1. Đinh Vũ Phương Khánh 
* **Đầu việc 1.1: Cài đặt thuật toán Brute Force (`BruteForceMatcher.java`)**
  * *Mô tả:* So khớp trượt từng byte từ trái qua phải, dừng ngay ở byte sai khớp đầu tiên.
  * *Yêu cầu kỹ thuật:* Đọc theo khối block kích thước $B$ (mặc định $1\text{ MiB}$). Phải triển khai kỹ thuật **đệm giữ lại $m - 1$ byte cuối của block trước** nối vào đầu block sau để không bị sót mẫu vắt qua 2 block.
  * *Đầu ra:* File `src/algorithms/BruteForceMatcher.java` kế thừa Interface `PatternMatcher`.
* **Đầu việc 1.2: Xây dựng bộ đo trung tâm (`BenchmarkRunner.java` & `MeasurementTask.java`)**
  * *Mô tả:* Viết "trọng tài bấm giờ" tự động chạy qua `ProcessBuilder` (JVM con độc lập với cờ `-Xmx2g`).
  * *Yêu cầu kỹ thuật:* 
    * Ngắt tiến trình sau 60s (`timeout`).
    * Cơ chế làm nóng (JIT warm-up) 3 lần trước khi đo.
    * Đo thời gian đầu-cuối bằng `System.nanoTime()`.
    * Đo đỉnh RAM bằng JMX `MemoryPoolMXBean.getPeakUsage()`.
    * Xuất kết quả chuẩn 22 cột dữ liệu vào file CSV.
  * *Đầu ra:* Các file `src/runner/BenchmarkRunner.java` và `src/runner/MeasurementTask.java`.
* **Đầu việc 1.3: Cài đặt điểm khởi động chương trình (`Main.java`)**
  * *Mô tả:* Tiếp nhận các tham số dòng lệnh CLI (ví dụ: `--demo rq1`, `--demo rq2`, `--demo rq3`, `--test`, `--benchmark`).
  * *Đầu ra:* File `src/Main.java`.
* **Đầu việc 1.4: Soạn thảo Paper**
  * Viết phần Brute Force và cơ chế đo đạc trong mục **III. Methods** và mục **IV. Experimental Setup**.

#### 2. Hà Văn Sang
* **Đầu việc 2.1: Cài đặt các bộ sinh dữ liệu (`src/generator/`)**
  * `LogGenerator.java`: Sinh log mô phỏng $T_1$ định dạng server `yyyy-MM-dd HH:mm:ss.SSS LEVEL [component] message`, 50 component, 1.000 mã lỗi phân phối Zipf $s = 1$.
  * `RandomByteGenerator.java`: Sinh file ngẫu nhiên $T_3$ với bảng chữ cái $\sigma \in \{2, 4, 26, 256\}$.
  * `WorstCaseGenerator.java`: Sinh dữ liệu bất lợi $T_4$ (toàn bộ là byte `'a'`, mẫu gồm $m-1$ byte `'a'` và kết thúc bằng `'b'`).
  * `HashUtils.java`: Tính chuỗi băm SHA-256 của file/mảng byte để ghi vào CSV.
  * *Đầu ra:* Gói mã nguồn trong thư mục `src/generator/`.
* **Đầu việc 2.2: Cài đặt mốc tham chiếu đọc file nền (`LibReadMatcher.java`)**
  * *Mô tả:* Đọc luồng file theo block $B$ từ đĩa/cache mà không tìm kiếm, để đo chi phí đọc file nền (`lib-read`).
  * *Đầu ra:* File `src/algorithms/LibReadMatcher.java`.
* **Đầu việc 2.3: Viết bộ kiểm thử ranh giới block (`BlockBoundaryTest.java`)**
  * *Mô tả:* Chạy kiểm thử Brute Force với các kích thước block cực nhỏ: $B = 1, 2, 3, 7\text{ bytes}$ đối chiếu với $B = 1\text{ MiB}$. Kết quả `count:check` phải trùng khớp 100%.
  * *Đầu ra:* File `test/BlockBoundaryTest.java`.
* **Đầu việc 2.4: Soạn thảo tài liệu dữ liệu & Review chéo**
  * Hoàn thành tài liệu `data/README.md` (hướng dẫn seed, SHA-256, nguồn Loghub).
  * Kiểm tra chéo (Review) code thuật toán KMP của Cặp 2.

---

### 🟢 CẶP 2: THUẬT TOÁN KMP, ĐA MẪU K-KMP & BỘ KIỂM THỬ TÍNH ĐÚNG

#### 3. Nguyễn Đặng Trường Hải
* **Đầu việc 3.1: Cài đặt thuật toán KMP đơn mẫu (`KMPMatcher.java`)**
  * *Mô tả:* Tiền tính toán bảng tiền tố (Failure Function) $\pi$ theo giáo trình Knuth-Morris-Pratt.
  * *Yêu cầu kỹ thuật:* Đọc theo block kích thước $B$. Duy trì biến chỉ số $j$ (số byte đã khớp thành công) liên tục qua các block mà **tuyệt đối không bị reset về 0**.
  * *Đầu ra:* File `src/algorithms/KMPMatcher.java`.
* **Đầu việc 3.2: Cài đặt thuật toán đa mẫu k-KMP (`MultiKMPMatcher.java`)**
  * *Mô tả:* Tìm đồng thời $k$ mẫu. Đọc văn bản **đúng 1 lượt duy nhất**; tại mỗi byte đọc được, cập nhật lần lượt trạng thái của cả $k$ bảng KMP độc lập.
  * *Yêu cầu kỹ thuật:* Duy trì một mảng trạng thái `int[] state = new int[k]` liên tục qua các ranh giới block.
  * *Đầu ra:* File `src/algorithms/MultiKMPMatcher.java`.
* **Đầu việc 3.3: Soạn thảo Paper**
  * Viết chi tiết nguyên lý KMP, công thức tính bảng $\pi$, cơ chế giữ trạng thái qua block và thuật toán $k$-KMP trong mục **III. Methods**.

#### 4. Nguyễn Hoài Nhi
* **Đầu việc 4.1: Cài đặt bộ kiểm thử chuyên sâu cho KMP & k-KMP (`KMPTest.java`)**
  * *Mô tả:* Xây dựng bộ test đơn vị (Unit Test) chuyên biệt cho KMP của Cặp 2:
    1. Kiểm tra tính đúng đắn của bảng tiền tố (Failure Function) $\pi$ trên các chuỗi mẫu kinh điển trong giáo trình (Knuth 1977, Sedgewick §5.3, ví dụ: `"ababac"`, `"ababcabab"`).
    2. Kiểm tra cơ chế duy trì biến chỉ số trạng thái $j$ liên tục qua ranh giới block mà không bị reset về 0.
    3. Kiểm tra mảng trạng thái $k$ phần tử `int[] state` của thuật toán đa mẫu `MultiKMPMatcher` ($k$-KMP).
  * *Đầu ra:* File `test/KMPTest.java`.
* **Đầu việc 4.2: Cài đặt bộ kiểm thử 5 ca cố định chuẩn đề tài (`FixedCasesTest.java`)**
  * *Mô tả:* Kiểm thử tính đúng đắn bắt buộc tại Mục 5 của đề tài:
    1. `aaaaa` tìm `aaa` $\to$ offset 0, 1, 2.
    2. `abababab` tìm `abab` $\to$ offset 0, 2, 4.
    3. `abc` tìm `abcd` $\to$ không có (count = 0).
    4. Mẫu rỗng $\to$ ném ngoại lệ `IllegalArgumentException`.
    5. `ushers` tìm `{he, she, his, hers}` $\to$ `she` tại 1, `he` tại 2, `hers` tại 2.
  * *Đầu ra:* File `test/FixedCasesTest.java`.
* **Đầu việc 4.3: Cài đặt bộ kiểm thử ngẫu nhiên đối chiếu Oracle (`RandomOracleTest.java`)**
  * *Mô tả:* Sinh $10^4$ ca ngẫu nhiên ($\sigma = 2$, text 0–200 byte, pattern 1–8 byte). Đối chiếu từng ca với Oracle `String.indexOf` (chuẩn mã hóa `ISO-8859-1`).
  * *Yêu cầu kỹ thuật:* Cả Brute Force, KMP, k-KMP và Aho-Corasick đều phải chạy qua test này và cho ra `output_check` trùng khớp 100% với Oracle.
  * *Đầu ra:* File `test/RandomOracleTest.java`.


---

### 🟣 CẶP 3: THUẬT TOÁN AHO-CORASICK (FAIL & DFA), BỘ MẪU & BÁO CÁO REPORT

#### 5. Lương Triều Vỹ
* **Đầu việc 5.1: Xây dựng cấu trúc Cây Trie trên byte (`ACTrieNode.java`)**
  * *Mô tả:* Đại diện cho từng nút/trạng thái trong Otômat Aho-Corasick.
  * *Yêu cầu kỹ thuật:* Lưu các cạnh con dạng thưa (`Map<Byte, ACTrieNode>`), con trỏ `fail` (Failure Link), con trỏ `outputLink` (Dictionary Suffix Link), danh sách ID mẫu kết thúc tại nút.
  * *Đầu ra:* File `src/algorithms/ahocorasick/ACTrieNode.java`.
* **Đầu việc 5.2: Cài đặt Aho-Corasick biến thể `fail` (`ACFailMatcher.java`)**
  * *Mô tả:* Dựng cây Trie; xây dựng Failure Link và Output Link bằng thuật toán duyệt theo chiều rộng (BFS).
  * *Yêu cầu kỹ thuật:* Khi quét văn bản, nếu không có cạnh chuyển thì nhảy theo Failure Link. Giữ con trỏ nút trạng thái liên tục giữa các block.
  * *Đầu ra:* File `src/algorithms/ahocorasick/ACFailMatcher.java`.
* **Đầu việc 5.3: Cài đặt bộ trích xuất và sinh mẫu (`PatternExtractor.java`)**
  * *Mô tả:* 
    * Trích xuất mẫu ngẫu nhiên từ văn bản cho RQ1 và RQ2 (đảm bảo xuất hiện ít nhất 1 lần).
    * Sinh $k \in \{1, 10, 100, 1000\}$ mẫu cho RQ3 (50% lấy từ log, 50% chuỗi ngẫu nhiên không có trong log, độ dài 8–32 bytes).
  * *Đầu ra:* File `src/generator/PatternExtractor.java`.
* **Đầu việc 5.4: Viết script vẽ đồ thị Python**
  * Chuẩn bị sẵn file `bench/plot_scripts/plot_results.py` đọc CSV để sẵn sàng vẽ 5 biểu đồ ở ngày D11.

#### 6. Nguyễn Bảo Trâm
* **Đầu việc 6.1: Cài đặt Aho-Corasick biến thể `dfa` (`ACDFAMatcher.java`)**
  * *Mô tả:* Cài đặt **Algorithm 4** trong bài báo gốc của Aho và Corasick (1975) để tiền tính toán bảng chuyển trạng thái đầy đủ: `int[numStates][256]`.
  * *Yêu cầu kỹ thuật:* Khi quét văn bản: mỗi byte chỉ tốn đúng **1 phép tra mảng $O(1)$**, không duyệt Failure Link lúc runtime. Tiền gộp output match vào từng trạng thái. Giữ trạng thái `int currState` qua các block.
  * *Đầu ra:* File `src/algorithms/ahocorasick/ACDFAMatcher.java`.
* **Đầu việc 6.2: Viết bộ kiểm thử tính đúng Aho-Corasick (`AhoCorasickTest.java`)**
  * *Mô tả:* Xây dựng bộ test chuyên biệt cho thuật toán Aho-Corasick:
    1. Kiểm tra ca `ushers` với tập mẫu `{"he", "she", "his", "hers"}` để thẩm định cơ chế Dictionary Suffix Link (Output Link), đảm bảo không bỏ sót mẫu con lồng nhau.
    2. Kiểm tra tính tương đương (Equivalence Test) giữa 2 biến thể: Chạy đồng thời `ACFailMatcher` và `ACDFAMatcher` trên tập $k \in \{1, 10, 20\}$ mẫu, đối chiếu `count:check` phải trùng khớp 100%.
    3. Kiểm tra duy trì trạng thái Automaton (con trỏ node / chỉ số state) không bị reset khi chuyển tiếp qua ranh giới block $B = 1, 2, 3, 7\text{ bytes}$.
  * *Đầu ra:* File `test/AhoCorasickTest.java`.
* **Đầu việc 6.3: Xây dựng Bảng ánh xạ cơ chế vào mã nguồn (Bắt buộc theo Mục 7.4 Bảng 5)**
  * *Mô tả:* Lập bảng ánh xạ chi tiết chỉ rõ từng cơ chế cốt lõi của Aho-Corasick (Trie, BFS, Failure Link, Dictionary Suffix Link, DFA Table) tương ứng với tên file, tên hàm nào trong code của nhóm.
  * *Đầu ra:* Bảng ánh xạ chèn vào mục **III. Methods** của Paper.
* **Đầu việc 6.4: Quản lý và hoàn thiện bản thảo Paper IEEE Word**
  * Cập nhật file `paper/Group4_Paper.docx`: Hoàn thiện mục **II. Background and Related Work** và mục **III. Methods** (kèm mã giả và Bảng ánh xạ).
* **Đầu việc 6.5: Review chéo**
  * Kiểm tra chéo (Review) code Brute Force và bộ đo Runner của Cặp 1.

---

## IV. TIẾN ĐỘ THỰC HIỆN THEO NGÀY (D0 $\to$ D6)

| Thời gian | Cặp 1 (Hạ tầng & Brute Force) | Cặp 2 (KMP & Test tính đúng) | Cặp 3 (Aho-Corasick & Paper) |
| :---: | :--- | :--- | :--- |
| **D0 - D2**<br>*(Đã xong)* | - Tạo repo GitHub, mời Giảng viên<br>- Thống nhất Interface `PatternMatcher` & `ChecksumConsumer`<br>- Soạn thảo Giả thuyết RQ1 | - Nghiên cứu thuật toán KMP gốc [1]<br>- Lên danh sách test tính đúng<br>- Soạn thảo Giả thuyết RQ2 | - Tải mẫu IEEE Conference Template<br>- Tạo khung bài báo 7 mục<br>- Soạn thảo Giả thuyết RQ3 |
| **Ngày D3** | - Cài `BruteForceMatcher.java`<br>- Cài `LogGenerator.java` & `RandomByteGenerator.java` | - Cài `KMPMatcher.java` (tính bảng $\pi$ và quét block)<br>- Lên khung `FixedCasesTest.java` | - Cài `ACTrieNode.java`<br>- Dựng thuật toán nạp mẫu vào Trie |
| **Ngày D4** | - Cài `MeasurementTask.java` & `BenchmarkRunner.java`<br>- Cài `LibReadMatcher.java` | - Cài `MultiKMPMatcher.java` ($k$-KMP)<br>- Viết `KMPTest.java` (test bảng $\pi$ và trạng thái $j$)<br>- Hoàn thiện `FixedCasesTest` (đảm bảo PASS 5 ca) | - Cài `ACFailMatcher.java` (BFS failure & output link)<br>- Cài `PatternExtractor.java` |
| **Ngày D5** | - Cài `BlockBoundaryTest.java`<br>- Cài `Main.java` nhận lệnh CLI | - Cài `RandomOracleTest.java` ($10^4$ ca đối chiếu `String.indexOf`) | - Cài `ACDFAMatcher.java` (Algorithm 4 ma trận 256 cột)<br>- Viết `AhoCorasickTest.java` (test `ushers` & đối chiếu fail vs dfa)<br>- Lập Bảng ánh xạ cơ chế |
| **Ngày D6** | - Ghép toàn bộ thuật toán vào `Main`<br>- Chạy demo cấu hình 16 MiB ra file CSV mẫu | - Chạy toàn bộ Unit Tests kiểm tra chéo cả 3 thuật toán (đảm bảo 100% PASS) | - Hoàn thiện mục **III. Methods** trong Paper Word<br>- Cùng Leader kiểm tra gói nộp D7 |

---

