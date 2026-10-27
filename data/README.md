# DATASET SPECIFICATION & SHA-256 CHECKSUM LOG
**Học phần:** CSD201 — Data Structures and Algorithms (Java)  
**Nhóm:** Group 4 | **Lớp:** SE20C | **Học kỳ:** Fall 2026  
**Đề tài 3:** Tìm mẫu trong log lớn: Brute Force, KMP và Aho-Corasick  

---

## 1. QUY ƯỚC QUẢN LÝ DỮ LIỆU (MỤC 6.8 & BẢNG 6)
- **Không commit file dữ liệu lớn > 10 MB** lên Git repository.
- Dữ liệu được tái lập hoàn toàn từ **Generator** bằng mã nguồn Java trong `ProjectNetBeans/src/generator/`.
- Mọi file hoặc khối dữ liệu đầu vào trong mỗi lượt đo đều được tính mã băm **SHA-256** trên dãy byte nguyên bản để ghi vào cột `input_sha256` trong CSV, phục vụ kiểm tra tính toàn vẹn và tái lập kết quả tại buổi bảo vệ (Mục 10.1).

---

## 2. BẢNG MÃ BĂM SHA-256 THEO CẤU HÌNH ĐO CHUẨN

### 2.1. Họ T1 — Log mô phỏng (Synthetic Log Stream)
*Định dạng:* `yyyy-MM-dd HH:mm:ss.SSS LEVEL [component] message`  
*Phân phối:* LEVEL (80% INFO, 15% WARN, 5% ERROR), 50 components, mã lỗi Zipf $s=1$.  
*Phiên bản Generator:* `LogGenerator v1.0`

| Quy mô (Bytes / MiB) | Hạt giống (Seed) | Mã băm SHA-256 (`input_sha256`) | Trạng thái |
| :--- | :---: | :--- | :---: |
| **1 MiB** (Demo) | 42 | `0b79ec261ae165319e7a2b9be20d7f1e7ff8a3df13da6cf8db6522c0cf7cf68c` | Đã xác thực |
| **4 MiB** (Demo) | 42 | `83df1e2a0cd7f9c2105e6b4fa17a4192b0e6df29c7810aa72ef5f2998a67de19` | Đã xác thực |
| **16 MiB** (RQ1) | 42 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **16 MiB** (RQ1) | 101 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **16 MiB** (RQ1) | 2024 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **64 MiB** (RQ1) | 42 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **64 MiB** (RQ1) | 101 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **64 MiB** (RQ1) | 2024 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **256 MiB** (RQ1, RQ3) | 42 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **256 MiB** (RQ1, RQ3) | 101 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **256 MiB** (RQ1, RQ3) | 2024 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **512 MiB** (RQ1) | 42 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |
| **1024 MiB** (RQ1, RQ3) | 42 | *(Được tự động log khi chạy `java -jar ... --rq1`)* | Sẵn sàng chạy |

---

### 2.2. Họ T3 — Dữ liệu ngẫu nhiên theo bảng chữ cái $\Sigma$ (RQ2 Thí nghiệm A)
*Phiên bản Generator:* `RandomByteGenerator v1.0`  
*Quy mô cố định:* $S = 256\text{ MiB}$ ($268,435,456\text{ bytes}$)

| Bảng chữ cái ($\Sigma$) | Seed | Ký hiệu byte sử dụng | Mã băm SHA-256 |
| :---: | :---: | :--- | :--- |
| $\Sigma = 2$ | 42 | `a`, `b` | *(Ghi nhận tự động qua CLI)* |
| $\Sigma = 4$ | 42 | `a`, `b`, `c`, `d` | *(Ghi nhận tự động qua CLI)* |
| $\Sigma = 26$ | 42 | `a` – `z` | *(Ghi nhận tự động qua CLI)* |
| $\Sigma = 256$ | 42 | Toàn bộ dãy byte 0..255 | *(Ghi nhận tự động qua CLI)* |

---

### 2.3. Họ T4 — Dữ liệu trường hợp bất lợi (Worst-case Dataset - RQ2 Thí nghiệm B)
*Đặc điểm:* Văn bản gồm toàn byte `'a'`. Mẫu tìm kiếm gồm $m - 1$ byte `'a'` và kết thúc bằng $1$ byte `'b'`.  
*Phiên bản Generator:* `WorstCaseGenerator v1.0`  
*Quy mô:* $S = 256\text{ MiB}$

- Mẫu $m = 8\text{ byte}$: `aaaaaaab`
- Mẫu $m = 64\text{ byte}$: $63 \times \text{'a'} + \text{'b'}$
- Mẫu $m = 1024\text{ byte}$: $1023 \times \text{'a'} + \text{'b'}$

---

### 2.4. Dữ liệu công khai Loghub (Dataset T2 - Khuyến khích)
Trường hợp nhóm sử dụng thêm log thực tế từ bộ dữ liệu mã nguồn mở [Loghub](https://github.com/logpai/loghub):
- **Bộ dữ liệu lựa chọn:** `HDFS v1` / `BGL`
- **File gốc:** `HDFS_2k.log` / `BGL_2k.log`
- **Quy tắc ghép lặp (nếu áp dụng):** Nhân bản liên tiếp file gốc để đạt kích thước 256 MiB hoặc 1 GiB và ghi chú rõ trong Mục IV của Paper IEEE.

---

## 3. HƯỚNG DẪN KIỂM TRA MÃ HASH TRỰC TIẾP
Bạn có thể tự sinh và đối chiếu mã hash SHA-256 của bất kỳ khối dữ liệu nào bằng công cụ `HashUtils.java` trong mã nguồn dự án:

```java
byte[] data = ...;
String sha256 = generator.HashUtils.sha256Hex(data);
System.out.println("SHA-256: " + sha256);
```
Hoặc kiểm tra file trên Windows PowerShell:
```powershell
Get-FileHash -Algorithm SHA256 <đường_dẫn_file>
```
