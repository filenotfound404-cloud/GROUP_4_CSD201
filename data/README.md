# Dữ liệu phục vụ thí nghiệm (Data Specification)

> Thư mục này chứa đặc tả dữ liệu và quy định theo Mục 6.8 & Bảng 6.
> Không lưu file dữ liệu có kích thước lớn hơn 10 MB trong Git repository.

## 1. Thông tin Dataset mô phỏng (Synthetic Datasets)
- **Họ T1 (Log mô phỏng):** Sinh theo phân phối Zipf (s = 1), mẫu log timestamp `yyyy-MM-dd HH:mm:ss.SSS LEVEL [component] message`.
- **Họ T3 (Dữ liệu ngẫu nhiên):** Bảng chữ cái $\Sigma = \{2, 4, 26, 256\}$.
- **Họ T4 (Trường hợp bất lợi):** Chuỗi toàn ký tự `'a'` và kết thúc bằng `'b'`.

## 2. Danh sách Seed và Bảng mã băm SHA-256 (Cần bổ sung sau khi chạy đo)
| Họ dữ liệu | Kích thước | Seed | SHA-256 Checksum | Generator Version |
| :--- | :--- | :--- | :--- | :--- |
| T1 | 16 MiB | 42 | *[Cần cập nhật]* | v1.0 |
| T1 | 64 MiB | 42 | *[Cần cập nhật]* | v1.0 |
| T1 | 256 MiB | 42 | *[Cần cập nhật]* | v1.0 |
| T1 | 512 MiB | 42 | *[Cần cập nhật]* | v1.0 |
| T1 | 1024 MiB | 42 | *[Cần cập nhật]* | v1.0 |

## 3. Dataset công khai (Public Log Dataset - T2)
- Nguồn: [Loghub](https://github.com/logpai/loghub) (bộ HDFS / BGL).
- Phiên bản và SHA-256 của file tải về: *[Cần cập nhật nếu nhóm sử dụng T2]*
