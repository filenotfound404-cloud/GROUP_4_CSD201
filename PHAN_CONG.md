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
* **Đầu việc 6.3: Viết giải thích bằng bảng ánh xạ cơ chế (Bắt buộc theo Mục 7.4 Bảng 5)**
  * *Mô tả:* Lập bảng ánh xạ chi tiết chỉ rõ từng cơ chế cốt lõi của Aho-Corasick (Trie, BFS, Failure Link, Dictionary Suffix Link, DFA Table) tương ứng với tên file, tên hàm nào trong code của nhóm.
  * *Đầu ra:* Bảng ánh xạ chèn vào mục **III. Methods** của Paper.
* **Đầu việc 6.4: Quản lý và hoàn thiện bản thảo Paper IEEE Word**
  * Cập nhật file `paper/Group4_Paper.docx`: Hoàn thiện mục **II. Background and Related Work** và mục **III. Methods** (kèm mã giả và Bảng ánh xạ).


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



## V. BẢNG PHÂN CÔNG CHI TIẾT GIAI ĐOẠN D7–D14 (THỰC NGHIỆM, PAPER IEEE & BẢO VỆ)
**Mục tiêu giai đoạn D7–D14:** Hoàn thành kiểm tra tiến độ D7, chạy toàn bộ ma trận đo đạc 3 RQ (D8–D10), xuất biểu đồ trực quan hóa, hoàn thiện Paper IEEE 7 phần và chuẩn bị sẵn sàng cho buổi vấn đáp bảo vệ cá nhân 1-1 (D14).

---

### 🔵 CẶP 1: THỰC NGHIỆM RQ1 (BRUTE FORCE VS KMP), GIÁM SÁT BỘ NHỚ & PHÂN TÍCH HIỆU NĂNG

#### 1. Đinh Vũ Phương Khánh (Nhóm trưởng & Phụ trách RQ1)
* **Đầu việc 1.5: Chuẩn bị và chủ trì buổi kiểm tra tiến độ D7**
  * *Mô tả:* Chuẩn bị slide/tài liệu 1 trang tóm tắt 3 RQ, giả thuyết và kiến trúc hệ thống; chạy demo kiểm tra tính đúng và chạy mẫu cấu hình nhỏ 16 MiB để xuất CSV trước Giảng viên trong 10 phút.
  * *Đầu ra:* Biên bản/Xác nhận đạt kiểm tra tiến độ ngày D7.
* **Đầu việc 1.6: Chủ trì chạy thực nghiệm đo đạc RQ1 (D8 – D10)**
  * *Mô tả:* Đo đạc so sánh Brute Force và KMP trên 5 mức kích thước văn bản: $S \in \{16, 64, 256, 512, 1024\text{ MiB}\}$ trên cả tập log mô phỏng $T_1$ và tập ngẫu nhiên $T_3$ ($\sigma = 4$), lặp 3 seed ngẫu nhiên.
  * *Yêu cầu kỹ thuật:* Giữ cố định độ dài mẫu $m = 16\text{ bytes}$. Đo ở trạng thái cache nóng (warm cache) theo đúng giao thức. Đảm bảo `status == OK` và giá trị `output_check` giữa Brute Force và KMP trùng khớp 100%.
  * *Đầu ra:* Tập dữ liệu CSV chuẩn cho RQ1 trong thư mục `results/`.
* **Đầu việc 1.7: Soạn thảo Paper IEEE - Mục RQ1 & Tổng kết**
  * *Mô tả:* Viết phần phân tích dữ liệu RQ1 trong mục **V. Results**: giải thích cơ chế vì sao Brute Force có throughput xấp xỉ hoặc cao hơn KMP trên $T_1$ do tỉ lệ mismatch xảy ra ngay byte đầu tiên rất cao. Phối hợp viết mục **VII. Conclusion**.
  * *Đầu ra:* Các mục văn bản tương ứng chèn vào file `paper/Group4_Paper.docx`.
* **Đầu việc 1.8: Chuẩn bị bảo vệ cá nhân & Điều phối bảo vệ nhóm (D14)**
  * *Mô tả:* Soạn kịch bản trình bày nhóm 5 phút (câu hỏi chung, kết quả chính 3 RQ, 1 kết quả trái dự đoán); ôn luyện truy vết Brute Force và KMP trên chuỗi ngắn cho phần thi cá nhân 5 phút.

#### 2. Hà Văn Sang (Phụ trách Sinh dữ liệu quy mô lớn & Hệ số đọc nền)
* **Đầu việc 2.5: Sinh và kiểm soát dữ liệu log quy mô lớn (D7 – D8)**
  * *Mô tả:* Vận hành các bộ sinh dữ liệu (`LogGenerator`, `RandomByteGenerator`, `WorstCaseGenerator`) để tạo các file thực nghiệm $S$ lên đến $1\text{ GiB}$; tính toán và lưu mã băm SHA-256 cho từng file.
  * *Đầu ra:* Toàn bộ dữ liệu thực nghiệm sẵn sàng cùng tài liệu cập nhật `data/README.md`.
* **Đầu việc 2.6: Đo đạc mốc tham chiếu đọc file nền `lib-read` & Giám sát tài nguyên (D8 – D10)**
  * *Mô tả:* Thực hiện đo `lib-read` trên tất cả các kích thước file để xác định trần I/O của phần cứng; theo dõi `heap_peak_bytes` và kiểm soát hiện tượng nghẽn I/O hay Garbage Collection.
  * *Đầu ra:* Các dòng log `lib-read` trong file CSV kết quả.
* **Đầu việc 2.7: Soạn thảo Paper IEEE - Mục Thiết lập thực nghiệm (IV. Experimental Setup)**
  * *Mô tả:* Mô tả chi tiết thông số môi trường thử nghiệm: thông số CPU, RAM, OS (Windows), phiên bản JDK 1.8, NetBeans 17, JVM parameter (`-Xmx2g`), block size $B = 1\text{ MiB}$, chiến lược JIT warm-up 3 lần.
  * *Đầu ra:* Toàn bộ nội dung mục **IV. Experimental Setup** trong Paper Word.
* **Đầu việc 2.8: Thực hiện phản biện chéo (Cross-Review) Cặp 2 & Chuẩn bị vấn đáp cá nhân**
  * *Mô tả:* Kiểm tra chéo toàn bộ dữ liệu thực nghiệm RQ2 của Cặp 2; luyện tập thao tác sửa code trực tiếp trong 2 phút trên IDE theo yêu cầu vấn đáp D14.

---

### 🟢 CẶP 2: THỰC NGHIỆM RQ2 (ĐIỀU KIỆN ĐỔI CHIỀU & DỮ LIỆU BẤT LỢI), KIỂM SOÁT TÍNH ĐÚNG

#### 3. Nguyễn Đặng Trường Hải (Phụ trách Thực nghiệm RQ2 & Phân tích số phép tính)
* **Đầu việc 3.4: Chủ trì chạy thực nghiệm đo đạc RQ2 (D8 – D10)**
  * *Mô tả:* 
    * **Thí nghiệm A:** Giữ cố định $S = 256\text{ MiB}$, mẫu $m = 64\text{ bytes}$, thay đổi kích thước bảng chữ cái $\sigma \in \{2, 4, 26, 256\}$ trên tập $T_3$ để tìm điểm giao thoa hiệu năng giữa Brute Force và KMP.
    * **Thí nghiệm B:** Giữ $S = 256\text{ MiB}$, chạy trên tập dữ liệu bất lợi $T_4$ ('aaaa...b') với độ dài mẫu thay đổi $m \in \{8, 64, 1024\}$.
  * *Yêu cầu kỹ thuật:* Đảm bảo KMP chứng minh được độ phức tạp $O(n)$ trong khi Brute Force thoái hóa về $O(n \times m)$ trên $T_4$.
  * *Đầu ra:* Tập dữ liệu CSV chuẩn cho RQ2 trong thư mục `results/`.
* **Đầu việc 3.5: Đo đạc chỉ số số phép so sánh byte (`--count`)**
  * *Mô tả:* Thu thập và phân tích chỉ số số phép so sánh byte trên mỗi byte văn bản cho cả hai thuật toán trên từng mức $\sigma$ và $m$.
  * *Đầu ra:* Bảng số liệu thống kê tỷ lệ so sánh byte đưa vào báo cáo.
* **Đầu việc 3.6: Soạn thảo Paper IEEE - Mục RQ2**
  * *Mô tả:* Viết phần phân tích thực nghiệm RQ2 trong mục **V. Results**: luận giải tác động của việc thu hẹp bảng chữ cái $\sigma$ làm tăng số tiền tố trùng lặp, khiến Brute Force quay lui nhiều và KMP bắt đầu vượt trội.
  * *Đầu ra:* Bản thảo mục **V. Results (Subsection RQ2)** trong Paper Word.
* **Đầu việc 3.7: Chuẩn bị vấn đáp cá nhân (D14)**
  * *Mô tả:* Nắm vững quy trình tính bảng tiền tố $\pi$, vẽ đồ thị bước nhảy KMP trên giấy nháp để trả lời giảng viên; sẵn sàng thực hiện sửa đổi logic bước nhảy KMP trong 2 phút.

#### 4. Nguyễn Hoài Nhi (Phụ trách Kiểm soát tính đúng & Thẩm định độ tin cậy)
* **Đầu việc 4.4: Đảm bảo bộ test tính đúng cho kiểm tra tiến độ D7**
  * *Mô tả:* Đóng gói và chạy toàn bộ các test suite (`FixedCasesTest`, `KMPTest`, `RandomOracleTest`, `BlockBoundaryTest`) trong buổi kiểm tra D7, đảm bảo 100% test pass màu xanh.
  * *Đầu ra:* Báo cáo kết quả kiểm thử tính đúng nộp tại D7.
* **Đầu việc 4.5: Rà soát và lọc ngoại lai dữ liệu CSV (D8 – D10)**
  * *Mô tả:* Kiểm tra tính hợp lệ của toàn bộ các dòng kết quả CSV RQ2: đối chiếu `output_check`, phát hiện các lần đo bị biến thiên lớn do GC spike để tiến hành đo lại.
  * *Đầu ra:* Bộ số liệu CSV sạch, có độ tin cậy cao và tính tái lập tốt.
* **Đầu việc 4.6: Soạn thảo Paper IEEE - Mục Các đe dọa đến độ tin cậy (VI. Threats to Validity)**
  * *Mô tả:* Viết mục **VI. Threats to Validity**: phân tích các yếu tố nội tại và ngoại cảnh ảnh hưởng đến kết quả (cơ chế cache ổ đĩa của OS, Garbage Collection của JVM, độ chính xác của bộ đo `System.nanoTime()`, ranh giới đọc block).
  * *Đầu ra:* Toàn bộ nội dung mục **VI. Threats to Validity** trong Paper Word.
* **Đầu việc 4.7: Thực hiện phản biện chéo (Cross-Review) Cặp 3 & Chuẩn bị vấn đáp cá nhân**
  * *Mô tả:* Kiểm tra chéo cấu trúc cây Trie, mảng chuyển trạng thái DFA của Cặp 3; luyện tập trả lời câu hỏi dự đoán kết quả khi tham số $m, \sigma$ thay đổi.

---

### 🟣 CẶP 3: THỰC NGHIỆM RQ3 (AHO-CORASICK VS K-KMP), ĐỒ THỊ PYTHON & CHỦ BIÊN PAPER IEEE

#### 5. Lương Triều Vỹ (Phụ trách Thực nghiệm RQ3 & Trực quan hóa dữ liệu)
* **Đầu việc 5.5: Chủ trì chạy thực nghiệm đo đạc RQ3 (D8 – D10)**
  * *Mô tả:* Đo đạc so sánh $k$-KMP vs Aho-Corasick biến thể `fail` vs Aho-Corasick biến thể `dfa` khi số lượng mẫu $k \in \{1, 10, 100, 1000\}$ trên file log $T_1$ quy mô $256\text{ MiB}$ (và cấu hình tối đa $1024\text{ MiB}$).
  * *Yêu cầu kỹ thuật:* Ghi nhận đầy đủ 3 thông số: thời gian dựng (`build_ms`), throughput quét (`throughput_mibs`), và đỉnh RAM chiếm dụng (`heap_peak_bytes`).
  * *Đầu ra:* Tập dữ liệu CSV chuẩn cho RQ3 trong thư mục `results/`.
* **Đầu việc 5.6: Trực quan hóa dữ liệu bằng script Python (D11 – D12)**
  * *Mô tả:* Hoàn thiện và chạy script `bench/plot_scripts/plot_results.py` trên file CSV để tự động sinh ra 5 biểu đồ khoa học đạt chuẩn IEEE:
    * *Biểu đồ 1 (RQ1):* Throughput (MiB/s) theo kích thước văn bản $S$.
    * *Biểu đồ 2 (RQ2A):* Throughput theo kích thước bảng chữ cái $\sigma$.
    * *Biểu đồ 3 (RQ2B):* Thời gian thực thi trên tập dữ liệu bất lợi $T_4$ theo độ dài mẫu $m$.
    * *Biểu đồ 4 (RQ3):* Thời gian quét (Scan Time) khi số lượng mẫu $k$ tăng từ 1 đến 1.000.
    * *Biểu đồ 5 (RQ3 - Cơ chế tắt/bật):* So sánh đánh đổi thời gian dựng (`build_ms`) và bộ nhớ phụ trội giữa `AC-DFA` và `AC-Fail`.
  * *Đầu ra:* 5 file hình chất lượng cao đặt trong thư mục `paper/figures/`.
* **Đầu việc 5.7: Soạn thảo Paper IEEE - Mục RQ3 & Thảo luận**
  * *Mô tả:* Viết phần phân tích RQ3 trong mục **V. Results**: luận giải sự đánh đổi giữa tốc độ $O(1)$ của DFA với chi phí bộ nhớ ma trận $O(\text{states} \times 256)$, và lợi thế của Aho-Corasick so với $k$-KMP khi $k \ge 10$.
  * *Đầu ra:* Nội dung mục RQ3 gửi cho Bảo Trâm tích hợp vào Paper.

#### 6. Nguyễn Bảo Trâm (Chủ biên Paper IEEE & Trưởng ban Biên tập)
* **Đầu việc 6.5: Tổng hợp và hoàn thiện toàn diện Paper IEEE 7 phần (D11 – D12)**
  * *Mô tả:* Tiếp nhận số liệu và đồ thị từ các cặp để tích hợp vào `paper/Group4_Paper.docx`. Trực tiếp viết và biên tập:
    * **Title, Abstract & Keywords (Tiếng Anh chuyên ngành):** Tóm tắt gọn gàng bài toán, phương pháp, kết quả số liệu cốt lõi và đóng góp chính.
    * **I. Introduction:** Nêu rõ bài toán quét log lớn trong thực tế, các giới hạn của thuật toán đơn mẫu và sự cần thiết của automata đa mẫu.
    * **VII. Discussion:** Thảo luận sâu các phát hiện bất ngờ (Brute Force thắng KMP trên log tự nhiên), quy tắc chọn thuật toán dựa trên thực tế.
    * **Tài liệu tham khảo (References):** Chuẩn hóa đúng định dạng trích dẫn IEEE cho 4 tài liệu bắt buộc ([1] Knuth 1977, [2] Aho 1975, [3] Sedgewick 2011, [4] Loghub 2023).
    * **Khai báo sử dụng AI (AI Declaration):** Viết tuyên bố minh bạch về các tác vụ có sử dụng AI (debug, viết test, hỗ trợ vẽ script) theo quy định của môn học.
  * *Đầu ra:* Bản thảo Paper hoàn chỉnh đúng format IEEE 2 cột, không vượt quá giới hạn trang.
* **Đầu việc 6.6: Rà soát chất lượng & Kiểm tra trích dẫn chéo (D13)**
  * *Mô tả:* Chủ trì rà soát toàn bộ bài báo: kiểm tra độ khớp giữa số liệu bảng biểu, đồ thị với file CSV gốc; kiểm tra định dạng hình ảnh và bảng biểu theo quy chuẩn IEEE.
* **Đầu việc 6.7: Đóng gói sản phẩm nộp & Chuẩn bị kịch bản bảo vệ (D13 – D14)**
  * *Mô tả:* Cùng Leader kiểm tra gói nộp cuối cùng (mã nguồn, file CSV gốc, script Python, Paper file Word và PDF); phân công kịch bản phản biện chéo và hỗ trợ đồng đội trong phần bảo vệ cá nhân.

---

## VI. TIẾN ĐỘ THỰC HIỆN GIAI ĐOẠN CUỐI (D7 $\to$ D14)

| Mốc thời gian | Cặp 1 (Hạ tầng & RQ1) | Cặp 2 (KMP & RQ2) | Cặp 3 (Aho-Corasick & Paper) | Mục tiêu kiểm soát |
| :---: | :--- | :--- | :--- | :--- |
| **Ngày D7** | - Trình bày tiến độ trên lớp (10p)<br>- Demo chạy Runner cấu hình 16 MiB ra CSV | - Chạy toàn bộ test suite tính đúng trước GV (100% PASS)<br>- Trình bày kế hoạch test | - Chuẩn bị tài liệu 1 trang (3 RQ, giả thuyết, interface)<br>- Trình bày thiết kế Aho-Corasick | **Đạt xác nhận tiến độ D7 từ Giảng viên** |
| **Ngày D8** | - Sinh toàn bộ dữ liệu lớn $T_1, T_3, T_4$ ($S \le 1\text{ GiB}$)<br>- Ghi nhận SHA-256 vào `data/README.md` | - Thiết lập kịch bản đo Thí nghiệm A RQ2 ($\sigma \in \{2, 4, 26, 256\}$)<br>- Kiểm tra tính đúng với $m = 64$ | - Sinh tập $k \in \{1, 10, 100, 1000\}$ mẫu cho RQ3 (50% log, 50% random)<br>- Kiểm tra bộ nhớ Trie | Sẵn sàng toàn bộ dữ liệu và mẫu cho benchmark |
| **Ngày D9** | - Chạy toàn bộ cấu hình thực nghiệm RQ1 (5 mức kích thước, 3 seed)<br>- Chạy đối chiếu `lib-read` | - Chạy toàn bộ Thí nghiệm A và Thí nghiệm B ($T_4$) của RQ2<br>- Đo số phép so sánh byte | - Chạy benchmark RQ3: so sánh $k$-KMP, `AC-Fail`, `AC-DFA` trên $T_1$ 256 MiB và 1 GiB | Thu thập đủ 100% dữ liệu thô vào các file CSV |
| **Ngày D10** | - Kiểm tra tính nhất quán CSV RQ1 (`output_check`, warm cache)<br>- Lọc dữ liệu ngoại lai | - Rà soát dữ liệu CSV RQ2<br>- Lập bảng số liệu tỷ lệ so sánh byte | - Rà soát CSV RQ3, kiểm tra đánh đổi bộ nhớ và build time<br>- Chuẩn bị dữ liệu cho script vẽ hình | Khóa dữ liệu CSV, đảm bảo tính tái lập 100% |
| **Ngày D11** | - Viết mục **V. Results (RQ1)**<br>- Viết mục **IV. Experimental Setup** | - Viết mục **V. Results (RQ2)**<br>- Viết mục **VI. Threats to Validity** | - Chạy script Python xuất 5 biểu đồ IEEE<br>- Viết mục **V. Results (RQ3)** | Hoàn thành toàn bộ số liệu và đồ thị trực quan |
| **Ngày D12** | - Cùng Cặp 3 hoàn thiện phần phân tích và kết luận Paper<br>- Rà soát mã nguồn | - Hỗ trợ Cặp 3 rà soát bảng số liệu trong Paper<br>- Ôn luyện truy vết thuật toán KMP | - Tổng hợp toàn bộ bài báo IEEE Word (`paper/Group4_Paper.docx`)<br>- Viết Abstract, Intro, Discussion, Conclusion, AI Declaration | Bản thảo Paper IEEE đầy đủ 7 mục |
| **Ngày D13** | - Thực hiện Cross-Review code Cặp 2<br>- Chạy lại toàn bộ test trên bản build cuối | - Thực hiện Cross-Review code Cặp 3<br>- Luyện tập thao tác sửa code 2 phút | - Thực hiện Cross-Review code Cặp 1<br>- Rà soát chuẩn trích dẫn IEEE và format 2 cột<br>- Đóng gói sản phẩm nộp | Sản phẩm nộp hoàn chỉnh, sẵn sàng bảo vệ |
| **Ngày D14** | **BẢO VỆ ĐỒ ÁN:**<br>- Thuyết trình nhóm 5 phút<br>- Vấn đáp cá nhân 1-1 (5 phút) | **BẢO VỆ ĐỒ ÁN:**<br>- Trực tiếp thực hiện chạy lại cấu hình GV chỉ định (3 phút)<br>- Vấn đáp cá nhân 1-1 (5 phút) | **BẢO VỆ ĐỒ ÁN:**<br>- Phản biện kết quả đồ thị và bảng số liệu<br>- Vấn đáp cá nhân 1-1 (5 phút) | **Hoàn thành bảo vệ đồ án CSD201** |

---

## VII. GIẢI THÍCH CHI TIẾT Ý NGHĨA KỸ THUẬT, BẢN CHẤT DỮ LIỆU & BÍ QUYẾT BẢO VỆ CHO TỪNG CẶP

Dưới đây là cẩm nang phân tích chuyên sâu cho từng cặp nhằm phục vụ trực tiếp cho việc viết Paper IEEE (Discussion, Threats to Validity) và trả lời vấn đáp cá nhân 1-1 (4.0 điểm):

---

### 1. CẶP 1: BẢN CHẤT KỸ THUẬT RQ1, MỐC THAM CHIẾU LIB-READ & HIỆN TƯỢNG ĐẢO CHIỀU

* **Câu hỏi cốt lõi của RQ1:** Khi dung lượng log tăng từ $16\text{ MiB}$ đến $1.024\text{ MiB}$ ($1\text{ GiB}$), Brute Force hay KMP nhanh hơn về mặt thời gian thực tế (`elapsed_ms`) và Throughput (`throughput_mibs`)?
* **Bản chất khoa học của hiện tượng Brute Force $\approx$ KMP trên log tự nhiên ($T_1$):**
  * *Tại sao lý thuyết nói KMP $O(n)$ tốt hơn Brute Force $O(n \times m)$ nhưng thực tế Brute Force lại chạy nhanh tương đương hoặc hơn?*
  * Trong các file log thực tế ($T_1$), bảng chữ cái ASCII phân bố rộng và đa dạng. Hơn 95% các vị trí so khớp bị sai (mismatch) ngay từ byte đầu tiên hoặc byte thứ hai ($j = 0$ hoặc $j = 1$). Do đó, Brute Force chỉ tốn 1 đến 2 phép so sánh là trượt tiếp sang vị trí kế tiếp, độ phức tạp thực nghiệm thực tế gần như là $O(n)$.
  * Ngược lại, KMP tuy không quay lui con trỏ văn bản nhưng lại tốn thêm chi phí overhead để quản lý biến trạng thái `j = pi[j - 1]`, tra mảng $\pi$ trong bộ nhớ cache. Trên dữ liệu log thông thường, chi phí quản trị trạng thái này lớn hơn lợi ích bước nhảy mà KMP mang lại.
* **Ý nghĩa của việc đo mốc tham chiếu `lib-read`:**
  * Thuật toán `lib-read` chỉ đọc file tuần tự từng block $1\text{ MiB}$ vào mảng byte rồi bỏ qua mà không xử lý tìm kiếm.
  * Tốc độ của `lib-read` đại diện cho **trần băng thông I/O vật lý của phần cứng / OS cache**.
  * Khi nhóm chứng minh Throughput của Brute Force hay KMP đạt xấp xỉ Throughput của `lib-read`, nhóm có luận điểm khoa học vững chắc trong Paper: *Thuật toán đã tối ưu hóa tiệm cận giới hạn phần cứng, nút thắt cổ chai (bottleneck) nằm ở I/O đĩa chứ không nằm ở thuật toán*.
* **Trọng tâm vấn đáp cá nhân D14:**
  * Vẽ từng bước dịch chuyển con trỏ của Brute Force trên chuỗi ngắn (10–15 ký tự).
  * Giải thích cơ chế đệm $m - 1$ byte cuối của block trước nối vào đầu block sau để không bỏ sót mẫu vắt qua 2 block $1\text{ MiB}$.
  * Thao tác sửa code trong 2 phút: Thay đổi công thức tính biến kiểm tra tích lũy `check = (start_pos + 1) * 1000003L + pattern_id` và chạy test chứng minh.

---

### 2. CẶP 2: BẢN CHẤT KỸ THUẬT RQ2, BƯỚC NHẢY BẢNG TIỀN TỐ $\pi$ & DỮ LIỆU BẤT LỢI $T_4$

* **Câu hỏi cốt lõi của RQ2:** Kích thước bảng chữ cái $\sigma$, độ dài mẫu $m$ và dữ liệu bất lợi ảnh hưởng thế nào đến hai thuật toán? Điều kiện nào khiến KMP vượt trội hoàn toàn so với Brute Force?
* **Thí nghiệm A - Ảnh hưởng của kích thước bảng chữ cái $\sigma$ ($\sigma \in \{2, 4, 26, 256\}$):**
  * Khi $\sigma$ lớn ($\sigma = 26$ chữ cái tiếng Anh, $\sigma = 256$ toàn bộ byte): Xác suất trùng tiền tố rất thấp $\to$ Brute Force rất nhanh.
  * Khi $\sigma$ thu hẹp cực nhỏ ($\sigma = 2$ như hệ nhị phân, chuỗi bit hoặc DNA): Xác suất các ký tự trùng lặp nhau tăng vọt. Mẫu và văn bản liên tục khớp được một đoạn dài rồi mới sai $\to$ Brute Force phải quay lui liên tục, số phép so sánh byte chia cho số byte văn bản (`--count`) tăng cao. Lúc này bảng $\pi$ của KMP phát huy sức mạnh tối đa, giúp KMP nhảy cóc mà không quay lui $\to$ **KMP chiến thắng rõ rệt tại $\sigma = 2$**.
* **Thí nghiệm B - Dữ liệu bất lợi đối kháng ($T_4$ Worst-Case):**
  * Văn bản $T_4$: Toàn bộ là ký tự `'a'` (ví dụ $256\text{ MiB}$ ký tự `'a'`).
  * Mẫu tìm kiếm: Gồm $m - 1$ ký tự `'a'` và kết thúc bằng một ký tự `'b'` (ví dụ: `"aaaa...aab"`).
  * *Hành vi của Brute Force:* Tại mỗi vị trí $i$, nó phải so khớp đúng $m - 1$ ký tự `'a'`, đến ký tự cuối cùng mới gặp `'b'` (sai khớp), sau đó nó chỉ dịch sang phải đúng 1 vị trí và lặp lại việc so khớp $m - 1$ ký tự. Độ phức tạp bị thoái hóa đúng về trường hợp xấu nhất: $O(n \times m)$. Khi $m = 1024$, thời gian chạy tăng vọt hàng trăm lần.
  * *Hành vi của KMP:* Nhờ bảng $\pi$ chỉ ra rằng tiền tố `'a'...'a'` trùng nhau, KMP không quay lui con trỏ mà chỉ cập nhật lại trạng thái, giữ vững thời gian chạy tuyến tính $O(n)$.
* **Trọng tâm vấn đáp cá nhân D14:**
  * Tự tay tính và vẽ mảng tiền tố (bảng $\pi$) trên giấy nháp cho một chuỗi cụ thể (ví dụ: `"ababac"` hoặc `"ababcabab"`) trong 1 phút.
  * Giải thích bản chất biến trạng thái $j$ (số ký tự khớp) được duy trì liên tục qua ranh giới các block $B$ như thế nào để không bị reset về 0.
  * Thao tác sửa code trong 2 phút: Sửa điều kiện cập nhật $j$ hoặc mảng trạng thái `int[] state` của $k$-KMP.

---

### 3. CẶP 3: BẢN CHẤT KỸ THUẬT RQ3, SỰ ĐÁNH ĐỔI BỘ NHỚ CỦA DFA VS TRIE VÀ VAI TRÒ CHỦ BIÊN PAPER

* **Câu hỏi cốt lõi của RQ3:** Khi tìm đồng thời $k$ mẫu ($k \in \{1, 10, 100, 1000\}$), Aho-Corasick vượt trội $k$-KMP ra sao? Hai biến thể của Aho-Corasick (`fail` và `dfa`) đánh đổi giữa tốc độ và bộ nhớ như thế nào?
* **So sánh $k$-KMP với Aho-Corasick:**
  * $k$-KMP: Quét file đúng 1 lượt, nhưng tại mỗi byte đọc được, chương trình phải dùng vòng lặp `for` duyệt qua cả $k$ trạng thái KMP độc lập. Do đó, thời gian quét tỷ lệ thuận tuyến tính với $k$ ($O(n \times k)$). Khi $k = 1.000$, tốc độ quét chậm đi gần 1.000 lần.
  * Aho-Corasick: Gom toàn bộ $k$ mẫu vào một cây Trie / Otômat duy nhất. Thời gian quét văn bản độc lập với $k$, đạt $O(n)$ cho toàn bộ tập mẫu $\to$ Khi $k \ge 10$, Aho-Corasick nhanh hơn $k$-KMP rõ rệt; khi $k = 1.000$, Aho-Corasick bỏ xa $k$-KMP hoàn toàn.
* **Thí nghiệm tắt/bật cơ chế: Đánh đổi giữa Biến thể `fail` và Biến thể `dfa`:**
  * **Biến thể `fail` (Trie + BFS Failure Link + Dictionary Suffix Link):**
    * *Bộ nhớ:* Rất tiết kiệm. Mỗi nút chỉ lưu các cạnh con thực tế tồn tại (dạng bảng băm hoặc mảng thưa).
    * *Tốc độ:* Lúc quét văn bản, nếu gặp byte không có nhánh đi tiếp, thuật toán phải lặp qua các con trỏ `failure link` để tìm trạng thái lùi hợp lệ $\to$ số bước chuyển trạng thái trên mỗi byte có thể $> 1$.
  * **Biến thể `dfa` (Ma trận chuyển trạng thái đầy đủ theo Algorithm 4 Aho-Corasick 1975):**
    * *Tốc độ:* Cực nhanh và ổn định tuyệt đối. Tại mỗi byte đọc vào, thuật toán chỉ thực hiện đúng **1 phép tra mảng $O(1)$**: `currState = transitionTable[currState][b & 0xFF]`. Tuyệt đối không có vòng lặp nhảy failure link lúc quét.
    * *Bộ nhớ phình to (Trade-off):* Bảng ma trận có kích thước `int[numStates][256]`. Khi số mẫu $k = 1.000$, số trạng thái lên đến hàng chục nghìn, bảng ma trận này chiếm dụng hàng chục/hàng trăm MB RAM, thời gian dựng tiền xử lý (`build_ms`) cũng lâu hơn.
* **Trọng tâm biên tập Paper IEEE & Vấn đáp cá nhân D14:**
  * Vẽ cây Trie cho tập từ ngắn `{"he", "she", "his", "hers"}` trên giấy nháp, chỉ rõ mũi tên nét đứt là `failure link` và mũi tên `output link` (Dictionary Suffix Link).
  * Giải thích Bảng ánh xạ cơ chế: Chỉ ra chính xác thuật toán BFS, nút Trie, mảng DFA nằm ở file `.java` nào và hàm nào trong project của nhóm.
  * Giải thích quy tắc lựa chọn thực tế trong phần Discussion: *Hệ thống nhúng ít RAM nên dùng biến thể `fail`; hệ thống máy chủ mạng băng thông cao như Snort/ripgrep cần tốc độ quét cực hạn $O(1)$ nên dùng biến thể `dfa`*.

---

### 🏆 BẢNG PHÂN BỔ ĐIỂM SỐ VÀ TIÊU CHÍ ĐÁNH GIÁ (THANG ĐIỂM 10)

| Thành phần điểm | Trọng số | Tiêu chí chi tiết theo Quy định chung | Người chịu trách nhiệm chính |
| :--- | :---: | :--- | :--- |
| **1. Cài đặt & Tính đúng** | **1.5** | Thuật toán qua 100% test bắt buộc, test biên, test ngẫu nhiên Oracle | Toàn bộ 3 cặp (Cặp 2 dẫn đầu test) |
| **2. Bộ đo & Tái lập** | **1.5** | CSV chuẩn 22 cột, có SHA-256/seed, chạy lại tại buổi bảo vệ khớp trong khoảng 0.5 – 2 lần median | Cặp 1 (Bộ đo Runner) & Cặp 3 (Plot script) |
| **3. Kết quả & Lập luận** | **2.0** | Trả lời đầy đủ RQ1 (0.5), RQ2 (0.5), RQ3 kèm tắt cơ chế (0.5), Discussion sâu sắc (0.5) | Cặp 1 (RQ1), Cặp 2 (RQ2), Cặp 3 (RQ3 & Discussion) |
| **4. Paper IEEE** | **1.0** | Đúng format 2 cột IEEE, đủ 7 mục, Abstract/Keywords chuẩn tiếng Anh, trích dẫn đúng 4 nguồn, khai báo AI | Cặp 3 (Bảo Trâm & Triều Vỹ) |
| **5. Giải thích phần phụ trách** | **1.0** | Từng cá nhân giải thích đúng code, test và dữ liệu mình đứng tên trong file `PHAN_CONG.md` | Điểm cá nhân (Mỗi người 5 phút) |
| **6. Truy vết thuật toán** | **1.0** | Truy vết đúng trên input nhỏ giảng viên đưa ra (Brute Force, bảng $\pi$, Trie/Fail link) | Điểm cá nhân (Mỗi người 5 phút) |
| **7. Thay đổi nhỏ & Kiểm tra** | **1.0** | Sửa code đúng theo yêu cầu GV trong 2 phút trên NetBeans và chạy test chứng minh | Điểm cá nhân (Mỗi người 5 phút) |
| **8. Diễn giải kết quả** | **1.0** | Nhìn biểu đồ/bảng số liệu giải thích hoặc dự đoán kết quả một cấu hình mới | Điểm cá nhân (Mỗi người 5 phút) |
| **TỔNG CỘNG** | **10.0** | **Điểm sinh viên = Điểm nhóm (0 – 6) + Điểm cá nhân (0 – 4)** | Cả nhóm đồng lòng đạt điểm tuyệt đối |

---
