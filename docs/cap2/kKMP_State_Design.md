# Thiết kế mảng trạng thái của k-KMP

## Các dữ liệu cần lưu

| Tên | Kiểu gợi ý | Ý nghĩa |
|---|---|---|
| `patterns[i]` | `byte[][]` | Pattern thứ `i` |
| `pi[i]` | `int[][]` | Bảng tiền tố của pattern thứ `i` |
| `state[i]` | `int[]` | Số byte đầu của pattern thứ `i` hiện đang khớp |
| `globalOffset` | `long` | Offset toàn cục của byte đang xử lý |
| `blockSize` | `int` | Kích thước block `B` |

Pattern rỗng bị từ chối bằng `IllegalArgumentException` theo `PHAN_CONG.md`. Mảng `pi` được tính một lần trước khi quét. Mảng `state` khởi tạo bằng 0 khi bắt đầu lượt quét mới; không khởi tạo lại khi bắt đầu block mới.

## Bảng tiền tố `π`

Với pattern `P` có độ dài `m`, `pi[i]` là độ dài tiền tố dài nhất của `P[0..i]` đồng thời là hậu tố của đoạn đó, không tính toàn bộ đoạn.

Ví dụ `P = ABABAC`:

| `i` | 0 | 1 | 2 | 3 | 4 | 5 |
|---:|---:|---:|---:|---:|---:|---:|
| `P[i]` | A | B | A | B | A | C |
| `pi[i]` | 0 | 0 | 1 | 2 | 3 | 0 |

```text
computePrefixTable(P):
    m = length(P)
    pi[0] = 0
    j = 0

    for i = 1 .. m - 1:
        while j > 0 and P[i] != P[j]:
            j = pi[j - 1]

        if P[i] == P[j]:
            j = j + 1

        pi[i] = j

    return pi
```

## Cập nhật trạng thái

Với mỗi byte `c`, mỗi pattern được cập nhật độc lập. Khi không khớp, thuật toán lùi `state[i]` theo `pi[i]` nhưng thử lại cùng byte `c`. Khi khớp toàn bộ pattern, thuật toán báo offset rồi lùi theo giá trị tiền tố cuối để giữ khả năng tìm kết quả chồng lấn.

```text
compile(patterns):
    for i = 0 .. k - 1:
        if length(patterns[i]) == 0:
            throw IllegalArgumentException
        pi[i] = computePrefixTable(patterns[i])

    state = int[k] filled with 0

search(blocks):
    globalOffset = 0

    for each block:
        for each byte c in block:
            for i = 0 .. k - 1:
                while state[i] > 0 and c != patterns[i][state[i]]:
                    state[i] = pi[i][state[i] - 1]

                if c == patterns[i][state[i]]:
                    state[i] = state[i] + 1

                if state[i] == length(patterns[i]):
                    startOffset = globalOffset - length(patterns[i]) + 1
                    onMatch(startOffset, i)
                    state[i] = pi[i][state[i] - 1]

            globalOffset = globalOffset + 1
```

`globalOffset` là offset toàn cục của byte hiện tại. Khi match, vị trí bắt đầu được tính bằng `globalOffset - m + 1`. Không cộng offset cục bộ của block vào lần nữa.

## Ví dụ với B = 3

Pattern `ABCD`, text `xABCD`, chia thành `xAB | CD`. Bảng tiền tố của `ABCD` là `[0, 0, 0, 0]`.

| Offset | Byte | `j` trước | Xử lý | `j` sau |
|---:|---|---:|---|---:|
| 0 | `x` | 0 | Không khớp `A` | 0 |
| 1 | `A` | 0 | Khớp ký tự đầu | 1 |
| 2 | `B` | 1 | Khớp tiếp | 2 |
|  | Ranh giới block | 2 | Giữ nguyên `j` | 2 |
| 3 | `C` | 2 | Khớp tiếp | 3 |
| 4 | `D` | 3 | Match, offset bắt đầu `4 - 4 + 1` | 0 |

Kết quả là vị trí `[1]`. k-KMP áp dụng cùng quy tắc cho từng phần tử `state[i]`. Không cần nối đuôi block trước vào block mới vì trạng thái KMP được giữ liên tục.
