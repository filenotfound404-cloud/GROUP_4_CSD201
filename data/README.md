# HƯỚNG DẪN DỮ LIỆU THỰC NGHIỆM - ĐỒ ÁN CSD201 (FALL 2026)

Tài liệu này đáp ứng **Đầu việc 2.4** theo phân công của Cặp 1 (`PHAN_CONG.md`) và **Mục 4** trong Quy định Đề tài 3.

---

## 1. CÁC HỆ VĂN BẢN THỰC NGHIỆM

| Mã | Tên hệ văn bản | Đặc điểm & Cơ chế sinh | Mục đích sử dụng |
| :---: | :--- | :--- | :--- |
| **$T_1$** | **Log mô phỏng** | Định dạng `yyyy-MM-dd HH:mm:ss.SSS LEVEL [component] message`. 80% INFO, 15% WARN, 5% ERROR; 50 components; 200 câu mẫu, mã lỗi $E0000 - E0999$ phân phối Zipf $s = 1$. Lớp sinh: `generator.LogGenerator`. | Sử dụng cho **RQ1** và **RQ3**. |
| **$T_2$** | **Log thực tế** (Khuyến khích) | Dữ liệu log thực lấy từ kho **Loghub** (ví dụ HDFS hoặc BGL). Ghi nhận phiên bản và mã băm SHA-256. | Đối chiếu thực tế mở rộng. |
| **$T_3$** | **Ngẫu nhiên $\sigma$ ký hiệu** | Chuỗi byte ngẫu nhiên phân bố đều trên $\sigma$ ký tự đầu tiên của bảng mã: $\sigma \in \{2, 4, 26, 256\}$. Lớp sinh: `generator.RandomByteGenerator`. | Sử dụng cho **RQ1** ($\sigma = 4$) và **RQ2 Thí nghiệm A**. |
| **$T_4$** | **Bất lợi (Worst-case)** | Toàn bộ văn bản là byte `'a'`. Mẫu tìm kiếm gồm $m - 1$ byte `'a'` và kết thúc bằng một byte `'b'` (`"aaaa...aab"`). Lớp sinh: `generator.WorstCaseGenerator`. | Sử dụng cho **RQ2 Thí nghiệm B** (độ dài mẫu $m \in \{8, 64, 1024\}$). |

---

## 2. QUY ƯỚC SEED VÀ TÍNH TÁI LẬP (REPRODUCIBILITY)
- **Quy tắc Seed:**
  - Mỗi lượt đo dùng các seed ngẫu nhiên cố định: mặc định là `42L`, `2026L`, `9999L` (3 lượt lặp để lấy trung vị median).
  - Khởi tạo bộ sinh số giả ngẫu nhiên `java.util.Random(seed)` để đảm bảo mỗi lần chạy lại đều sinh ra đúng từng byte giống nhau 100%.
- **Mã băm SHA-256:**
  - Mỗi tập dữ liệu sau khi sinh đều được tính mã băm SHA-256 bằng lớp `generator.HashUtils.sha256Hex(data)`.
  - Giá trị SHA-256 được ghi trực tiếp vào cột `input_sha256` của file CSV kết quả (`results/raw/benchmark_results.csv`).

---

## 3. NGUỒN LOG THỰC TẾ (LOGHUB DATASET)
- Khi sử dụng log thực tế $T_2$, nhóm tham khảo nguồn chuẩn:
  * **Trích dẫn:** J. Zhu, S. He, P. He, J. Liu, and M. R. Lyu, *"Loghub: A large collection of system log datasets for AI-driven log analytics,"* in Proc. IEEE 34th Int. Symp. Software Reliability Engineering (ISSRE), 2023.
  * **Kho lưu trữ:** `https://github.com/logpai/loghub`
  * **Tập log:** `HDFS_v1` hoặc `BGL`. File tải về được lưu tại thư mục `data/raw/` và ghi nhận SHA-256 trước khi đo.
