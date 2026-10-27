
# Thiết kế thuật toán k-KMP

## 1. Mục tiêu

Xây dựng thuật toán k-KMP để tìm nhiều mẫu trong một file lớn bằng cách đọc file một lượt.

## 2. Ý tưởng chính

Với mỗi mẫu cần tìm, chương trình lưu:
- Mảng byte của mẫu.
- Bảng tiền tố `pi`.
- Trạng thái khớp hiện tại `j`.
- ID của mẫu.

Chương trình đọc file theo từng block. Với mỗi byte đọc được, cập nhật trạng thái KMP của từng mẫu. Các trạng thái được giữ lại khi chuyển sang block tiếp theo.

## 3. Quy trình xử lý block

1. Đọc block byte tiếp theo.
2. Duyệt từng byte trong block.
3. Cập nhật trạng thái của từng mẫu.
4. Khi tìm thấy mẫu, ghi nhận kết quả.
5. Giữ trạng thái để tiếp tục xử lý block tiếp theo.
6. Kết thúc khi đã đọc hết file.

## 4. Yêu cầu về tính đúng đắn

- Không bỏ sót mẫu nằm qua ranh giới block.
- Đếm các lần xuất hiện chồng lấn.
- Báo lỗi nếu mẫu rỗng.
- Trả về 0 lần xuất hiện nếu mẫu dài hơn văn bản.
- Vị trí kết quả tính từ 0 theo byte.

## 5. Độ phức tạp

Gọi:
- `n`: số byte của văn bản.
- `k`: số mẫu cần tìm.
- `m_i`: độ dài của mẫu thứ i.

Tiền xử lý: O(tổng độ dài các mẫu).

Thời gian quét trong trường hợp xấu nhất:
O(n × k) đối với phương pháp chạy KMP độc lập cho từng mẫu.

Bộ nhớ phụ:
O(tổng độ dài các mẫu + k + B), chưa tính bộ nhớ lưu kết quả.
