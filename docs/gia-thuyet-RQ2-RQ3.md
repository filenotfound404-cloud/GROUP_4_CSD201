# Giả thuyết RQ2 và RQ3 (bản nháp cho kế hoạch D2)

> **Lưu ý:** Giả thuyết nộp ở D2 được giữ nguyên trong paper, không sửa sau khi có kết quả. Kết quả trái giả thuyết vẫn được chấp nhận nếu phương pháp đúng và nhóm giải thích được. Các con số dưới đây là **dự đoán từ lý thuyết**, không phải số đo. Cần thống nhất với Hải (RQ2) và Cặp 3 (RQ3) trước khi nộp.

## RQ2. Bảng chữ cái, độ dài mẫu và mức lặp tiền tố ảnh hưởng thế nào đến Brute Force và KMP?

**Thiết kế (theo đề):** giữ S = 256 MiB cố định.

| Thí nghiệm | Dữ liệu | Yếu tố thay đổi | Giữ cố định |
|---|---|---|---|
| A | T3 (ngẫu nhiên σ ký hiệu) | σ = 2; 4; 26; 256 | Mẫu 64 byte cắt từ văn bản, 3 seed |
| B | T4 (toàn `a`; mẫu m − 1 byte `a` + 1 byte `b`) | m = 8; 64; 1.024 | Dữ liệu T4 |

**Chỉ số:** `elapsed_ms`, throughput, số phép so sánh byte chia cho số byte văn bản (lượt `--count`).

### Giả thuyết cho thí nghiệm A (σ thay đổi)

| | Giả thuyết | Cơ sở | Điều kiện làm giả thuyết sai |
|---|---|---|---|
| H2.1 | Số phép so sánh/byte của **Brute Force giảm dần về gần 1** khi σ tăng: khoảng 2,0 (σ = 2), 1,33 (σ = 4), 1,04 (σ = 26), 1,00 (σ = 256) | Trên văn bản ngẫu nhiên, sai khớp thường xảy ra ngay ở byte đầu. Kỳ vọng ≈ σ/(σ − 1) phép so sánh mỗi vị trí | Số đo `--count` lệch rõ khỏi công thức (ví dụ do mẫu được cắt từ văn bản có cấu trúc lặp) |
| H2.2 | **KMP không vượt quá 2 phép so sánh/byte** ở mọi σ; chênh lệch với Brute Force nhỏ | Cận khấu hao 2n của KMP; Brute Force đã gần 1 | KMP > 2 phép so sánh/byte là dấu hiệu lỗi cài đặt |
| H2.3 | Thời gian `bf` và `kmp` chênh nhau **không quá 2 lần** ở mọi σ (tỷ lệ trong khoảng 0,5–2), vì chi phí đọc file chiếm phần lớn | Đề yêu cầu đo đầu–cuối gồm đọc file; mốc `lib-read` cho biết chi phí nền | Một thuật toán chậm hơn rõ rệt so với `lib-read` + phần tìm dự kiến |

**Điều kiện đổi chiều so với RQ1:** KMP chỉ vượt Brute Force rõ khi σ nhỏ (σ = 2). Khi σ ≥ 26, Brute Force có thể **nhanh bằng hoặc hơn** KMP vì vòng lặp đơn giản hơn và không phải tra bảng `pi`. Đây là kết quả cần giải thích trong paper, không được chỉnh dữ liệu để KMP thắng.

### Giả thuyết cho thí nghiệm B (m thay đổi trên T4)

| | Giả thuyết | Cơ sở | Điều kiện làm giả thuyết sai |
|---|---|---|---|
| H2.4 | Số phép so sánh/byte của **Brute Force ≈ m** (xấp xỉ 8; 64; 1.024) | Mỗi vị trí khớp m − 1 byte `a` rồi sai ở byte `b` cuối: m phép so sánh, tức O(n × m) | Số đo lệch xa m |
| H2.5 | **KMP giữ ≈ 2 phép so sánh/byte, không phụ thuộc m**; thời gian gần như phẳng khi m tăng | KMP không quay lui trên văn bản: O(n) | Thời gian KMP tăng theo m |
| H2.6 | Thời gian Brute Force **tăng gần tỷ lệ với m**: ước lượng cỡ vài giây ở m = 8, hàng chục giây ở m = 64, và **timeout (> 60 s) ở m = 1.024** (cỡ 2,7 × 10¹¹ phép so sánh với S = 256 MiB) | Suy ra từ H2.4 và tốc độ so sánh cỡ 10⁹/giây | Brute Force hoàn thành ở m = 1.024 trong 60 s |

**Quy tắc báo cáo:** lượt timeout để trống `elapsed_ms`, ghi chú "> 60 s" trên hình, không dùng để tính tỷ lệ tăng tốc. Đây là kết quả hợp lệ.

**Kết luận có điều kiện dự kiến cho RQ2:** KMP chỉ có lợi rõ khi (a) bảng chữ cái nhỏ hoặc (b) mẫu có tiền tố lặp dài như T4. Trên log thực (σ lớn, mẫu ít lặp), Brute Force thường đủ tốt.

---

## RQ3. Aho–Corasick so với k-KMP khi tìm k mẫu cùng lúc

**Thiết kế (theo đề):** k = 1; 10; 100; 1.000. T1 256 MiB; cấu hình nặng nhất dùng T1 1.024 MiB. Mẫu dài 8–32 byte: một nửa là mã lỗi hoặc từ có trong log, nửa còn lại là chuỗi ngẫu nhiên không có trong log.

| Cài đặt | Tên trong CSV | Vai trò |
|---|---|---|
| k-KMP | `kmp-k` | Phương án giáo trình |
| Aho–Corasick, failure link | `ac`, variant `fail` | Cạnh thưa; đi theo failure link khi quét |
| Aho–Corasick, bảng chuyển đầy đủ | `ac`, variant `dfa` | Bảng `int[số trạng thái][256]`; không đi theo failure link khi quét |
| Mốc đọc file | `lib-read` | Chi phí đọc file nền |

**Chỉ số:** `build_ms`, `elapsed_ms`, throughput, số trạng thái, bộ nhớ phụ (`heap_peak_bytes − heap_before_bytes`), số lần chuyển trạng thái và số lần đi theo failure link (lượt `--count`).

### Giả thuyết

| | Giả thuyết | Cơ sở | Điều kiện làm giả thuyết sai |
|---|---|---|---|
| H3.1 | **Thời gian quét của `kmp-k` tăng gần tuyến tính theo k**; `ac` (cả hai biến thể) **gần như không đổi khi k tăng** | k-KMP: O(n · k). Aho–Corasick: O(n + Σm + z) | Thời gian `ac` tăng theo k tương đương `kmp-k` |
| H3.2 | Ở **k = 1 và k = 10**, `kmp-k` **nhanh bằng hoặc hơn** `ac` (chênh không rõ rệt); từ **k ≥ 100**, `ac` nhanh hơn `kmp-k`; ở **k = 1.000**, chênh lệch **trên một bậc độ lớn** | Automaton có chi phí cố định (dựng, tra cạnh); lợi thế chỉ lộ khi k lớn | `ac` thua `kmp-k` ở k = 1.000, hoặc đã nhanh hơn rõ ngay từ k = 1 |
| H3.3 | **`kmp-k` có thể timeout (> 60 s) ở k = 1.000**, nhất là ở T1 1.024 MiB (cỡ 10¹² thao tác trạng thái) | Suy ra từ H3.1 | `kmp-k` hoàn thành ở k = 1.000 |
| H3.4 | **Thời gian dựng** của `dfa` lớn hơn `fail` và `kmp-k`, và tăng theo số trạng thái; ở k = 1.000 cỡ 2 × 10⁴ trạng thái (≤ Σm + 1) | `dfa` điền bảng (số trạng thái × 256) | `dfa` dựng nhanh hơn `fail` |
| H3.5 | **Bộ nhớ phụ của `dfa` lớn hơn `fail` cỡ vài chục lần** ở k = 1.000: bảng `int` cỡ 2 × 10⁴ × 256 × 4 byte ≈ 20 MB, trong khi `fail` chỉ lưu cạnh thưa | Kích thước bảng chuyển đầy đủ | Chênh lệch bộ nhớ nhỏ |
| H3.6 | **Thí nghiệm tắt cơ chế (`dfa` so với `fail`):** `dfa` quét nhanh hơn `fail` ở k nhỏ đến vừa vì không đi theo failure link; ở k = 1.000, bảng 20 MB không nằm gọn trong bộ nhớ đệm nên **ưu thế của `dfa` thu hẹp** | Đánh đổi tốc độ quét lấy bộ nhớ | `dfa` vẫn nhanh hơn `fail` rõ rệt ở k = 1.000 |
| H3.7 | Số lần đi theo failure link của `fail` **trung bình dưới 1 lần mỗi byte** trên T1 | Phần lớn byte của log không mở rộng được khớp dài; cận khấu hao của Aho–Corasick | Số đo `--count` ≥ 1 lần/byte |

**Điều kiện đổi chiều:** nếu chi phí đọc file (`lib-read`) chiếm ưu thế, chênh lệch giữa các thuật toán thu hẹp. Cần báo mốc `lib-read` cạnh kết quả mọi thuật toán.

**Kiểm tra tính đúng bắt buộc:** `count` và `check` của `kmp-k` và hai biến thể `ac` phải bằng nhau trên cùng tập mẫu ở mọi lượt RQ3.

**Kết luận có điều kiện dự kiến cho RQ3:** Aho–Corasick đáng dùng khi k lớn (hàng trăm mẫu trở lên); `dfa` phù hợp khi bộ nhớ dư và tập mẫu vừa, `fail` phù hợp khi tập mẫu rất lớn hoặc bộ nhớ hạn chế; k nhỏ thì `kmp-k` đủ dùng.
