<<<<<<< HEAD
# Danh sách kiểm thử k-KMP
=======

# Danh sách kiểm thử k-KMP và Bộ Test Chung cho cả Nhóm
>>>>>>> 735a892067cf49ce3e5279d62203003da4bef543

Quy ước: vị trí là **vị trí bắt đầu** tính từ 0. Mã mẫu `id` là thứ tự mẫu trong danh sách (0, 1, 2, …); khi chỉ có một mẫu thì `id = 0`.
Mỗi lần khớp: `count += 1` và `check += (vị trí + 1) × 1000003 + id` (cộng `long`, cho phép tràn).

## 1. Các trường hợp kiểm thử cố định

<<<<<<< HEAD
### 1.1. Ca bắt buộc của đề

| ID | Văn bản | Mẫu | Kết quả mong đợi | count | check |
|---|---|---|---|---|---|
| TC01 | `aaaaa` | `aaa` | Vị trí 0, 1, 2 | 3 | 6.000.018 |
| TC02 | `abababab` | `abab` | Vị trí 0, 2, 4 | 3 | 9.000.027 |
| TC03 | `abc` | `abcd` | Không tìm thấy | 0 | 0 |
| TC04 | bất kỳ | mẫu rỗng | Báo lỗi (`IllegalArgumentException`) | — | — |
| TC05 | `ushers` | `he`(0), `she`(1), `his`(2), `hers`(3) | she tại 1, he tại 2, hers tại 2 | 3 | 8.000.028 |

### 1.2. Ca biên và ca k mẫu bổ sung

| ID | Văn bản | Mẫu | Kết quả mong đợi | count | check |
|---|---|---|---|---|---|
| TC06 | `abababab` | `abab`(0), `bab`(1), `xyz`(2) | abab tại 0, 2, 4; bab tại 1, 3, 5; xyz không có | 6 | 21.000.066 |
| TC07 | `aaaa` | `aa`(0), `aa`(1) | Hai mẫu trùng nhau: mỗi mẫu tại 0, 1, 2, đếm riêng từng mã mẫu | 6 | 12.000.039 |
| TC08 | (rỗng) | `a` | Văn bản rỗng, không tìm thấy | 0 | 0 |
| TC09 | `abcd` | `abcd` | Mẫu dài đúng bằng văn bản, tại 0 | 1 | 1.000.003 |
| TC10 | `banana` | `a`(0), `n`(1) | a tại 1, 3, 5; n tại 2, 4 (mẫu 1 ký tự) | 5 | 20.000.062 |
| TC11 | `aaaaaaaab` | `aaab` | Tại 5 (T₄ nhỏ: lật bất lợi, sai khớp ở byte cuối) | 1 | 6.000.018 |
| TC12 | bytes `E9 FF E9 FF E9 00` | `E9 FF`, `FF E9 00`, `E9` | Byte ≥ 128 (Java `byte` có dấu), kết quả phải khớp oracle | theo oracle | theo oracle |
| TC13 | bất kỳ | bất kỳ, block size ≤ 0 | Báo lỗi (`IllegalArgumentException`) | — | — |

Giá trị `count` và `check` ở trên được tính theo đúng công thức của đề. Chương trình kiểm thử so kết quả của `kmp-k` với danh sách vị trí mong đợi của từng ca, và đồng thời so với oracle.
=======
| ID | Văn bản | Mẫu | Kết quả mong đợi |
|---|---|---|---|
| TC01 | aaaaa | aaa | Vị trí 0, 1, 2 |
| TC02 | abababab | abab | Vị trí 0, 2, 4 |
| TC03 | abc | abcd | Không tìm thấy |
| TC04 | abc | Mẫu rỗng | Báo lỗi `IllegalArgumentException` |
| TC05 | ushers | he, she, his, hers | she tại 1, he tại 2, hers tại 2 |
>>>>>>> 735a892067cf49ce3e5279d62203003da4bef543

## 2. Kiểm thử ranh giới block

**Mọi ca cố định (TC01–TC13) và mọi ca ngẫu nhiên (mục 3)** đều chạy với các kích thước block:

- B = 1 byte
- B = 2 byte
- B = 3 byte
- B = 7 byte
- B = 1 MiB

`count` và `check` phải **giống nhau ở mọi kích thước block**. Với test ngẫu nhiên, các giá trị B được luân phiên theo thứ tự cặp để mọi B đều được thử.

## 3. Kiểm thử ngẫu nhiên (Randomized Stress Test)

### 3.1. Một mẫu

Sinh 10.000 cặp văn bản và mẫu:
- Độ dài văn bản: 0–200 byte.
- Kích thước bảng chữ cái: σ = 2.
- Độ dài mẫu: 1–8 byte.

### 3.2. Nhiều mẫu (đặc trưng của k-KMP)

Sinh 10.000 cặp, với cùng quy tắc trên, nhưng mỗi văn bản có **1–20 mẫu**. Kết quả của `kmp-k` phải bằng **tổng kết quả oracle của từng mẫu** (cộng `count` và `check`, với mã mẫu theo thứ tự).

### 3.3. Bộ tham chiếu (oracle)

Dùng vòng lặp gọi `String.indexOf`. Chuyển byte sang chuỗi bằng `new String(bytes, StandardCharsets.ISO_8859_1)` để mỗi byte tương ứng đúng một ký tự. Sau mỗi lần tìm thấy tại `idx`, tiếp tục tìm từ `idx + 1` để lấy cả kết quả chồng lấn.

`String.indexOf` chỉ dùng làm oracle kiểm tra đúng, không dùng trong thuật toán.

## 4. Kiểm thử đọc file và lượt `--count`

| ID | Nội dung | Yêu cầu |
|---|---|---|
| TC14 | Ghi file tạm, đọc bằng `FileInputStream.read(byte[])` theo block với B = 1, 7, 4096, 1 MiB | `count` và `check` khớp oracle |
| TC15 | Lượt `--count` (đếm số phép so sánh byte) chạy trên mọi ca cố định | `count` và `check` **bằng** lượt đo thời gian (bộ đếm không được làm đổi kết quả) |

## 5. Đối chiếu giữa các thuật toán

Các kiểm tra này chạy ở bước tích hợp, khi code của các cặp đã sẵn sàng:

| Kiểm tra | Yêu cầu |
|---|---|
| `kmp` và `bf` ở RQ1, RQ2 | `count` và `check` bằng nhau ở mọi lượt đo |
| `kmp-k`, `ac` (variant `fail`) và `ac` (variant `dfa`) ở RQ3 | `count` và `check` bằng nhau trên cùng tập mẫu, trong đó có ca `ushers` (TC05) |

Lượt nào `count` hoặc `check` khác oracle thì ghi `status = wrong`.

## 6. Tiêu chí đạt

- `count` và `check` **đều** khớp bộ tham chiếu (oracle) và danh sách vị trí mong đợi của ca cố định.
- Không bỏ sót các lần xuất hiện chồng lấn.
- Vị trí bắt đầu được tính từ 0.
<<<<<<< HEAD
- Kết quả không thay đổi theo kích thước block (B = 1, 2, 3, 7, 1 MiB).
- Mẫu rỗng và block size ≤ 0 báo lỗi.
- Lượt `--count` cho cùng kết quả với lượt đo thời gian.
- Toàn bộ test chạy xong với mã thoát 0 (chương trình in `TAT CA TEST DEU QUA`).
=======
- Kết quả không thay đổi theo kích thước block.
- Kết quả khớp với bộ tham chiếu.

## 5. Hướng dẫn chạy kiểm thử tự động (Automated Test Execution)

Toàn bộ các tiêu chí trên đã được mã hóa thành các lớp kiểm thử tự động:

1. **Bộ TestCase chung cho cả nhóm (`CommonTestSuite`):**
   ```powershell
   java -cp "build/classes" kkmp.experiment.CommonTestSuite
   # Hoặc chạy từ src:
   java -cp "src" kkmp.experiment.CommonTestSuite
   ```
   *Kết quả:* Thực thi toàn bộ TC01 - TC05, kiểm thử ranh giới block $B \in \{1, 2, 3, 7, 1\text{ MiB}\}$, và 10.000 ca ngẫu nhiên đối chiếu Oracle `String.indexOf` (100% PASS).

2. **Bộ TestCase riêng cho thuật toán KMP (`KmpDedicatedTestSuite`):**
   ```powershell
   java -cp "src" kkmp.experiment.KmpDedicatedTestSuite
   ```
   *Kết quả:* Thực thi 19 bài kiểm thử chuyên sâu về bảng $\pi$, ranh giới cắt mẫu, chuỗi Fibonacci, chuỗi tuần hoàn, tập dữ liệu bất lợi $T_4$ (100% PASS).
>>>>>>> 735a892067cf49ce3e5279d62203003da4bef543
