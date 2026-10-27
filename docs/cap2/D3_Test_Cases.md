# Kịch bản kiểm thử tính đúng cho Cặp 2

## Quy ước

Các thuật toán Brute Force, KMP, k-KMP và Aho–Corasick phải cho cùng kết quả trên cùng đầu vào. Chuẩn hóa kết quả thành các cặp `(patternId, vị trí)`, sắp xếp theo `patternId` rồi vị trí tăng dần. Vị trí bắt đầu được đánh số từ `0`.

`B` là số byte trong mỗi block. Với các test ranh giới, văn bản phải được cấp thành đúng các block thể hiện ở cột Text. Trạng thái thuật toán không được reset khi chuyển block.

## Test case

| Mã test | Mô tả | Text / cách chia block | Pattern | B | Kết quả mong đợi | Mục đích |
|---|---|---|---|---:|---|---|
| TC01 | Không khớp | `abc` | `xy` | 3 | `[]` | Trường hợp không có kết quả |
| TC02 | Khớp ở đầu | `ABCxx` | `ABC` | 3 | `[0]` | Mẫu bắt đầu tại byte đầu |
| TC03 | Khớp ở cuối | `xxABC` | `ABC` | 3 | `[2]` | Mẫu kết thúc tại byte cuối |
| TC04 | Khớp nhiều lần | `abxxab` | `ab` | 3 | `[0, 4]` | Ghi nhận nhiều lần xuất hiện |
| TC05 | Khớp chồng lấn | `aaaa` | `aa` | 3 | `[0, 1, 2]` | Tiếp tục tìm sau khi khớp |
| TC06 | Pattern dài hơn text | `abc` | `abcd` | 2 | `[]` | Không thể có kết quả |
| TC07 | Text rỗng | `""` | `a` | 3 | `[]` | Văn bản rỗng |
| TC08 | Pattern rỗng | `a` | `""` | 3 | Ném `IllegalArgumentException` | Mẫu rỗng không hợp lệ theo `PHAN_CONG.md` |
| TC09 | Pattern một ký tự | `banana` | `a` | 2 | `[1, 3, 5]` | Mẫu dài 1, nhiều kết quả |
| TC10 | Vắt block, B=1 | Blocks: `[A, B, C]` | `ABC` | 1 | `[0]` | Giữ trạng thái qua hai ranh giới |
| TC11 | Vắt block, B=2 | Blocks: `[xA, BC]` | `ABC` | 2 | `[1]` | Mẫu bắt đầu trước, kết thúc sau ranh giới |
| TC12 | Vắt block, B=3 | Blocks: `[xAB, C]` | `ABC` | 3 | `[1]` | Giữ `j=2` tại ranh giới |
| TC13 | Vắt block, B=4 | Blocks: `[xxxA, BC]` | `ABC` | 4 | `[3]` | Ranh giới nằm giữa mẫu |
| TC14 | Vắt block, B=7 | Blocks: `[xxxxxxA, BC]` | `ABC` | 7 | `[6]` | Block lớn hơn pattern |
| TC15 | T4 bất lợi | `aaaaaaaaaaaa` | `aaaab` | 3 | `[]` | Nhiều tiền tố lặp, kiểm tra tính đúng và phép đếm |
| TC16 | Aho–Corasick nhiều mẫu | `ushers` | `{he, she, his, hers}` | 3 | `he:[2]`; `she:[1]`; `his:[]`; `hers:[2]` | Tìm nhiều mẫu đồng thời |

## Điều kiện đạt

- So sánh vị trí và số lượng kết quả, không chỉ so sánh checksum.
- Chạy TC10–TC14 với cách chia block ghi trong bảng; kết quả phải giống lần quét toàn văn bản trong một block.
- TC08 phải bị từ chối bằng `IllegalArgumentException` theo yêu cầu trong `PHAN_CONG.md`.
- Với TC16, `patternId` theo thứ tự đầu vào `{he, she, his, hers}`.
- TC15 dùng văn bản toàn `a` và mẫu `a^(m-1)b`, đúng mô tả T4 trong `PHAN_CONG.md`. Không ghi nhận kết quả benchmark giả; chỉ kết luận hiệu năng sau khi chạy đo.
