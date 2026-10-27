# ĐỊNH DẠNG 22 CỘT DỮ LIỆU FILE CSV BENCHMARK

- **Dự án:** Tìm mẫu trong log lớn: Brute Force, KMP và Aho–Corasick (CSD201 - Fall 2026)
- **Quy chuẩn:** Thống nhất dùng chung cho toàn bộ các thuật toán của Nhóm 4
- **Cấu trúc:** 20 cột chuẩn (Bảng 4 Đề tài) + 2 cột bổ sung đo lường (`build_ms`, `throughput_mibs`)

---

## 1. DANH SÁCH 22 CỘT DỮ LIỆU (CSV HEADER)

```csv
run_id,timestamp,dataset_name,file_size_bytes,file_size_mib,alphabet_size_sigma,algorithm,variant,pattern_count_k,pattern_length_m,pattern_sample,block_size_bytes,warmup_iterations,measured_iterations,build_ms,search_ms,total_ms,throughput_mibs,matches_count,checksum,memory_used_kib,status
```

---

## 2. Ý NGHĨA CHI TIẾT TỪNG CỘT

| STT | Tên cột (Header) | Kiểu dữ liệu | Mô tả chi tiết | Ví dụ |
| :---: | :--- | :---: | :--- | :--- |
| 1 | `run_id` | `String` / `UUID` | Định danh duy nhất cho từng lần chạy thí nghiệm | `RUN_20261020_001` |
| 2 | `timestamp` | `ISO 8601` | Thời điểm thực thi đo lường | `2026-10-20T16:30:00Z` |
| 3 | `dataset_name` | `String` | Tên tập log / dữ liệu đo (`HDFS`, `Apache`, `T4_Adversarial`, `Synthetic`) | `Loghub_HDFS` |
| 4 | `file_size_bytes` | `long` | Kích thước file dữ liệu tính bằng byte | `104857600` |
| 5 | `file_size_mib` | `double` | Kích thước file tính bằng MiB | `100.0` |
| 6 | `alphabet_size_sigma` | `int` | Kích thước bảng chữ cái $\sigma$ của tập dữ liệu | `74` |
| 7 | `algorithm` | `String` | Tên thuật toán (`BruteForce`, `KMP`, `k-KMP`, `AhoCorasick`) | `KMP` |
| 8 | `variant` | `String` | Biến thể triển khai (`streaming_block`, `dfa_matrix`, `fail_map`) | `streaming_block` |
| 9 | `pattern_count_k` | `int` | Số lượng mẫu tìm kiếm ($k$) | `1` |
| 10 | `pattern_length_m` | `int` | Độ dài mẫu tìm kiếm (hoặc trung bình nếu đa mẫu) | `8` |
| 11 | `pattern_sample` | `String` | Mẫu chuỗi đại diện (tránh dấu phẩy hoặc escape dấu ngoặc kép) | `"ERROR"` |
| 12 | `block_size_bytes` | `int` | Kích thước buffer đọc block $B$ (bytes) | `4096` |
| 13 | `warmup_iterations` | `int` | Số lượt chạy khởi động (JVM JIT Warm-up) | `3` |
| 14 | `measured_iterations` | `int` | Số lượt đo thực tế để lấy giá trị trung bình | `5` |
| 15 | **`build_ms`** *(Cột riêng 1)* | `double` | Thời gian tiền xử lý mẫu (dựng bảng $\pi$, Trie/DFA) tính bằng ms | `0.045` |
| 16 | `search_ms` | `double` | Thời gian quét stream tìm kiếm tính bằng ms | `142.310` |
| 17 | `total_ms` | `double` | Tổng thời gian (`build_ms + search_ms`) tính bằng ms | `142.355` |
| 18 | **`throughput_mibs`** *(Cột riêng 2)* | `double` | Thông lượng xử lý: `(file_size_mib / (search_ms / 1000.0))` | `702.71` |
| 19 | `matches_count` | `long` | Tổng số lần tìm thấy mẫu (lấy từ `ChecksumConsumer.getMatchCount()`) | `12540` |
| 20 | `checksum` | `long` | Mã checksum tích lũy (lấy từ `ChecksumConsumer.getChecksum()`) | `894723019842` |
| 21 | `memory_used_kib` | `long` | Lượng RAM heap tiêu thụ ước tính (KiB) | `512` |
| 22 | `status` | `String` | Trạng thái thực thi (`SUCCESS`, `TIMEOUT`, `OOM`) | `SUCCESS` |

---

## 3. VÍ DỤ DÒNG DỮ LIỆU CSV MẪU

```csv
RUN_01,2026-10-20T16:30:00Z,Loghub_HDFS,104857600,100.0,74,KMP,streaming_block,1,5,"ERROR",4096,3,5,0.012,120.450,120.462,830.22,4120,49182390123,256,SUCCESS
RUN_02,2026-10-20T16:30:05Z,Loghub_HDFS,104857600,100.0,74,BruteForce,streaming_block,1,5,"ERROR",4096,3,5,0.000,110.120,110.120,908.10,4120,49182390123,128,SUCCESS
RUN_03,2026-10-20T16:30:10Z,T4_Adversarial,52428800,50.0,2,KMP,streaming_block,1,100,"aaaa...b",4096,3,5,0.025,65.300,65.325,765.70,0,0,256,SUCCESS
RUN_04,2026-10-20T16:30:20Z,T4_Adversarial,52428800,50.0,2,BruteForce,streaming_block,1,100,"aaaa...b",4096,3,5,0.000,1240.500,1240.500,40.31,0,0,128,SUCCESS
```

---

## 4. QUY TẮC ĐỐI CHIẾU TÍNH ĐÚNG (CORRECTNESS ORACLE)
- Hai thuật toán khác nhau (ví dụ KMP vs Brute Force) khi chạy trên cùng một `dataset_name`, `pattern_sample`, và kích thước file **BẮT BUỘC** phải cho ra:
  1. `matches_count` bằng nhau tuyệt đối.
  2. `checksum` bằng nhau tuyệt đối.
- Nếu `checksum` khác nhau $\rightarrow$ có thuật toán bị sai vị trí hoặc bỏ sót kết quả ở ranh giới block.
