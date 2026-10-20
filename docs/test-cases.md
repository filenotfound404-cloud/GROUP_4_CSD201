
# Danh sách kiểm thử k-KMP

## 1. Các trường hợp kiểm thử cố định

| ID | Văn bản | Mẫu | Kết quả mong đợi |
|---|---|---|---|
| TC01 | aaaaa | aaa | Vị trí 0, 1, 2 |
| TC02 | abababab | abab | Vị trí 0, 2, 4 |
| TC03 | abc | abcd | Không tìm thấy |
| TC04 | abc | Mẫu rỗng | Báo lỗi |
| TC05 | ushers | he, she, his, hers | she tại 1, he tại 2, hers tại 2 |

## 2. Kiểm thử ranh giới block

Chạy các trường hợp cố định và ngẫu nhiên với kích thước block:
- B = 1 byte
- B = 2 byte
- B = 3 byte
- B = 7 byte
- B = 1 MiB

Kết quả phải giống nhau ở mọi kích thước block.

## 3. Kiểm thử ngẫu nhiên

Sinh 10.000 cặp văn bản và mẫu:
- Độ dài văn bản: 0–200 byte.
- Kích thước bảng chữ cái: sigma = 2.
- Độ dài mẫu: 1–8 byte.

Đối chiếu kết quả với bộ tham chiếu sử dụng `String.indexOf`. Chuyển byte sang chuỗi bằng `StandardCharsets.ISO_8859_1` để bảo toàn ánh xạ một byte thành một ký tự.

## 4. Tiêu chí đạt

- Số lần xuất hiện phải chính xác.
- Không bỏ sót các lần xuất hiện chồng lấn.
- Vị trí bắt đầu được tính từ 0.
- Kết quả không thay đổi theo kích thước block.
- Kết quả khớp với bộ tham chiếu.
