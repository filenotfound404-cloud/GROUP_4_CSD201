# Phân tích độ phức tạp trên dữ liệu T4

Theo mô tả T4 trong `PHAN_CONG.md`, văn bản gồm toàn ký tự `a`, còn pattern gồm `m - 1` ký tự `a` rồi kết thúc bằng `b`:

```text
Text:    aaaaaaaaaaaaaaaaa...
Pattern: aaaaa...aaab
```

## Brute Force

Tại gần mỗi vị trí bắt đầu, Brute Force so khớp được `m - 1` ký tự `a`, rồi thất bại khi gặp ký tự `b` trong pattern và `a` trong text. Sau đó, nó dịch pattern một byte và kiểm tra lại gần như cùng đoạn `a`.

Có khoảng `n - m + 1` vị trí bắt đầu, mỗi vị trí có thể tốn đến `m` phép so sánh. Vì vậy số phép so sánh trong trường hợp bất lợi có bậc `O(n × m)`.

## KMP

KMP tính bảng tiền tố `π` của pattern trước khi quét, với chi phí `O(m)`. Khi gặp mismatch ở cuối đoạn `a` đã khớp, bảng `π` cho biết trạng thái tiền tố phù hợp để tiếp tục mà không so sánh lại các byte văn bản đã đọc.

Sau bước tiền xử lý, KMP quét văn bản trong `O(n)`. Tổng thời gian gồm tiền xử lý và tìm kiếm là `O(n + m)`; khi `n` lớn hơn nhiều so với `m`, phần quét là tuyến tính theo `n`.

## Cách kiểm chứng thực nghiệm

Chạy cùng văn bản T4 cho Brute Force và KMP với các độ dài mẫu được quy định trong cấu hình thực nghiệm. Ghi nhận `ops_count` và thời gian chạy. Trước khi so sánh hiệu năng, xác nhận hai thuật toán trả cùng số lượng kết quả và cùng checksum. Không suy ra thời gian chạy từ độ phức tạp lý thuyết; dùng số liệu benchmark thực tế.
