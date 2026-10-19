# BẢNG PHÂN CÔNG ĐỒ ÁN NHÓM CSD201 (FALL 2026)
**Đề tài 3:** Tìm mẫu trong log lớn: Brute Force, KMP và Aho–Corasick  
**Lớp:** SE20C | **Nhóm:** 4

---

## 1. NGUYÊN TẮC TỔ CHỨC VÀ PHỐI HỢP (Mục 3 Quy định đồ án)
* **Quy mô:** Nhóm gồm **6 sinh viên**, chia thành **3 cặp làm việc chéo**.
* **Luân phiên vai trò:** Hai thành viên trong cùng một cặp luân phiên vai trò cài đặt (Code) và kiểm thử (Test), không phân chia cố định một người chỉ viết code và một người chỉ làm slide/tài liệu.
* **Cơ chế phản biện chéo (Cross-Review):**
  * **Cặp 1** review module và code của **Cặp 2**.
  * **Cặp 2** review module và code của **Cặp 3**.
  * **Cặp 3** review module và code của **Cặp 1**.
* **Đánh giá cá nhân:** Mỗi thành viên đều chủ trì một phần việc kỹ thuật và một câu hỏi nghiên cứu (RQ) để phục vụ cho **buổi vấn đáp cá nhân 1-1** (chiếm 4.0/10 điểm đồ án). Tuyệt đối không khai tỷ lệ % đóng góp.

---

## 2. BẢNG PHÂN CÔNG CHI TIẾT GIAI ĐOẠN D0–D2 (3 NGÀY ĐẦU)

| Cặp | Thành viên & Vai trò | Nhiệm vụ cụ thể trong 3 ngày (D0 - D2) | Sản phẩm phải nộp cuối D2 |
| :---: | :--- | :--- | :--- |
| **CẶP 1** | **Phương Khánh**<br>*(Trưởng nhóm / Project Lead & Kiến trúc sư hệ thống)*<br>MSSV: `[Điền MSSV 1]` | - Khởi tạo GitHub repository `GROUP_4_CSD201` và mời Giảng viên làm Collaborator.<br>- Thiết kế file Java chuẩn: Interface `PatternMatcher.java` và lớp `ChecksumConsumer.java`.<br>- **Chủ trì viết Giả thuyết cho RQ1** (So sánh Brute Force vs KMP khi kích thước log tăng từ 16 MB đến 1 GB).<br>- Điều phối nhóm, tổng hợp và xuất bản Kế hoạch nghiên cứu 1 trang A4. | - File code `PatternMatcher.java` & `ChecksumConsumer.java`.<br>- Link GitHub đã add GV.<br>- File PDF Kế hoạch 1 trang nộp GV. |
| **CẶP 1** | **[Văn Sang]**<br>*(Kỹ sư Dữ liệu & Đo lường / Benchmark Lead)*<br>MSSV: `[Điền MSSV 2]` | - Đọc tài liệu **[4] Loghub (Zhu et al. 2023)**, nghiên cứu cấu trúc log server và phân phối Zipf ($s=1$) cho mã lỗi.<br>- Thống nhất danh sách **22 cột dữ liệu của file CSV** (20 cột chuẩn Bảng 4 + 2 cột riêng `build_ms`, `throughput_mibs`).<br>- Cùng Phương Khánh phân tích lý do khoa học tại sao Brute Force có thể chạy nhanh hơn KMP trên log thực tế để hoàn thiện giả thuyết RQ1.<br>- Viết file `PHAN_CONG.md`. | - Bản mô tả định dạng 22 cột CSV.<br>- File `PHAN_CONG.md`.<br>- Đoạn phân tích giả thuyết RQ1. |
| **CẶP 2** | **[Hải]**<br>*(Kỹ sư Thuật toán KMP / KMP Lead Dev)*<br>MSSV: `[Điền MSSV 3]` | - Đọc tài liệu **[1] KMP gốc (Knuth 1977)** và **[3] Sedgewick §5.3**, nắm vững cách tính bảng tiền tố $\pi$.<br>- Thiết kế giải pháp kỹ thuật: Cách giữ biến trạng thái $j$ (số ký tự đã khớp) liên tục khi đọc qua ranh giới các block kích thước $B$.<br>- **Chủ trì viết Giả thuyết cho RQ2** (Ảnh hưởng của kích thước bảng chữ cái $\sigma$, độ dài mẫu $m$, và tập dữ liệu bất lợi $T_4$ 'aaaa...b'). | - Bản thiết kế thuật toán giữ trạng thái KMP qua block.<br>- Đoạn phân tích giả thuyết RQ2. |
| **CẶP 2** | **[Nhi]**<br>*(Kỹ sư Đa mẫu k-KMP & QA Lead)*<br>MSSV: `[Điền MSSV 4]` | - Thiết kế kiến trúc cho thuật toán mở rộng **k-KMP**: Cơ chế đọc file 1 lượt, cập nhật đồng thời mảng $k$ trạng thái KMP.<br>- Liệt kê danh sách các ca kiểm thử bắt buộc (Test Cases) cho ngày D3: các ca biên (`ushers`, `aaaaa`), ranh giới block ($B=1,2,3,7$), và kiểm thử với Oracle `String.indexOf`.<br>- Cùng TV3 làm rõ điều kiện nào khiến Brute Force bị thoái hóa về $O(n \times m)$ còn KMP giữ vững $O(n)$ trên dữ liệu $T_4$. | - Danh sách kịch bản test tính đúng cho ngày D3.<br>- Bản thiết kế mảng trạng thái của k-KMP. |
| **CẶP 3** | **[Vỹ]**<br>*(Kỹ sư Aho-Corasick Lead & Trực quan hóa)*<br>MSSV: `[Điền MSSV 5]` | - Đọc tài liệu **[2] Aho–Corasick gốc (1975)**, phân tích cấu trúc cây Trie trên byte, thuật toán BFS dựng Failure Link và Dictionary Suffix Link.<br>- Phân biệt cơ chế giữa biến thể `fail` (cạnh thưa/Map) và biến thể `dfa` (Algorithm 4 - ma trận $S \times 256$).<br>- **Chủ trì viết Giả thuyết cho RQ3** (So sánh Aho-Corasick vs k-KMP khi $k$ tăng từ 1 đến 1.000 mẫu; sự đánh đổi giữa tốc độ $O(1)$ và bộ nhớ RAM của DFA). | - Bản thiết kế cấu trúc nút Trie và thuật toán BFS.<br>- Đoạn phân tích giả thuyết RQ3. |
| **CẶP 3** | **[Trâm]**<br>*(Chủ biên Paper IEEE & Tối ưu DFA)*<br>MSSV: `[Điền MSSV 6]` | - Tải mẫu bài báo hội nghị IEEE Conference Template (`.docx` 2 cột chuẩn từ IEEE Author Center).<br>- Tạo khung bài báo (Paper Skeleton): Điền Title tiếng Anh, danh sách 6 tác giả (FPT University), Abstract nháp, Keywords, và 7 đề mục lớn (I -> VII).<br>- Cùng TV5 tính toán lý thuyết mức độ phình to bộ nhớ của bảng DFA (`int[states][256]`) khi $k=1.000$ để đưa vào giả thuyết RQ3.<br>- Soạn thảo mục Khai báo sử dụng AI (AI Declaration) theo chuẩn IEEE. | - File Word khung bài báo IEEE (`paper/Group4_Paper.docx`).<br>- Đoạn phân tích bộ nhớ DFA cho giả thuyết RQ3. |

---
