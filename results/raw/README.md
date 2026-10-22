# Dữ liệu benchmark CSV

`benchmark_results.csv` là nơi lưu kết quả đo thô chung cho Brute Force, KMP, k-KMP và Aho-Corasick. Mỗi dòng tương ứng với **một lần chạy Java (JVM)**. File hiện chỉ có hàng tiêu đề; chỉ ghi kết quả của lần chạy thật, kể cả các lần `timeout`, `crash`, `oom` hoặc `wrong`.

Schema gồm 20 cột bắt buộc theo quy ước chung và 2 cột riêng của bài Pattern Matching là `build_ms`, `throughput_mibs`. Không đổi tên, xoá hoặc thêm cột khi chưa thống nhất với cả nhóm.

## Cách hiểu nhanh

- `dataset` (`T1`, `T2`, `T3`, `T4`) là **kiểu/kịch bản sinh dữ liệu**. Mỗi loại có quy tắc tạo dữ liệu riêng.
- `seed` là giá trị khởi tạo ngẫu nhiên cho generator. Cùng `dataset`, `seed`, `n` và tham số sinh dữ liệu sẽ tạo lại đúng cùng một file. Đổi `seed` sẽ tạo file khác nhưng vẫn thuộc cùng loại dataset.
- `n` là **tổng kích thước toàn bộ file đầu vào**, tính theo byte.
- `block_size_bytes` trong `params` là kích thước một phần file được đọc mỗi lần. Chương trình vẫn phải đọc và tìm trên **toàn bộ file** có kích thước `n`, không phải chỉ một block. Ví dụ file 10 MiB (`n=10485760`) với `block_size_bytes=65536` sẽ được đọc thành 160 block 64 KiB.
- Nếu pattern nằm vắt qua ranh giới giữa hai block, chương trình phải giữ trạng thái hoặc xử lý phần chồng lấn để không bỏ sót kết quả.
- `pattern_length` là độ dài pattern cần tìm; `alphabet_size` là số ký tự tối đa có thể xuất hiện khi sinh dữ liệu, **không phải** số ký tự của cả file.

## Quy trình đo

- Mỗi cấu hình đo trong một tiến trình JVM riêng. Warm-up thuật toán ba lần với input không lớn hơn 1 MiB; không ghi các lần warm-up vào CSV.
- Nếu một cấu hình chạy ba lần để lấy số liệu ổn định, ghi ba dòng giống nhau về cấu hình và đặt `repeat` lần lượt là `1`, `2`, `3`. Khi phân tích sau đó mới tính trung bình hoặc median.
- `elapsed_ms` là thời gian từ lúc bắt đầu đọc file cho đến khi tìm kiếm xong. Bao gồm đọc file và tìm kiếm; không bao gồm tiền xử lý, kiểm tra kết quả, ghi CSV và vẽ biểu đồ.
- `build_ms` ghi riêng thời gian tiền xử lý, ví dụ tạo bảng prefix của KMP hoặc trie, failure links, DFA table của Aho-Corasick. Không có tiền xử lý thì ghi `0`.
- Trước lần đo Pattern Matching, đọc file một lần để làm nóng cache của hệ điều hành; nêu rõ quy ước hot-cache này trong báo cáo. Đồng thời đo `algorithm=lib-read` với cùng block size để có mốc chỉ-đọc-file.
- `throughput_mibs = (n / 1,048,576) / (elapsed_ms / 1,000)`. Chỉ dùng cùng khoảng thời gian đọc-và-tìm với `elapsed_ms`, không gồm `build_ms`.
- Gọi `System.gc()`, reset số liệu heap peak, rồi lấy `heap_before_bytes` ngay trước khi chạy và `heap_peak_bytes` sau khi chạy. Bộ nhớ phụ trợ có thể tính là `heap_peak_bytes - heap_before_bytes`.
- Chỉ bật đếm thao tác cho một lần chạy riêng có cờ `--count`. Dòng đo thời gian để trống `ops_count`; dòng đo đếm có thể để trống `elapsed_ms`.
- `status` chỉ nhận một trong các giá trị: `ok`, `timeout`, `crash`, `oom`, `wrong`.

## Ý nghĩa các cột

| Cột | Kiểu | Ý nghĩa |
| --- | --- | --- |
| `run_id` | string | Mã duy nhất của lần chạy, ví dụ `RUN_0001`. |
| `rq` | string | Câu hỏi nghiên cứu: `RQ1`, `RQ2`, `RQ3` hoặc `TEST`. |
| `algorithm` | string | Thuật toán: `bf`, `kmp`, `kmp-k`, `ac`, hoặc `lib-read`. Baseline thư viện dùng tiền tố `lib-`. |
| `variant` | string | Biến thể của thuật toán đang chạy; dùng `base` nếu không có biến thể. Aho-Corasick dùng `fail` hoặc `dfa`. |
| `dataset` | string | Kiểu/kịch bản dữ liệu: `T1`, `T2`, `T3` hoặc `T4`. |
| `n` | integer | Tổng kích thước toàn bộ input, tính theo byte. |
| `params` | string | Các tham số cấu hình, ghi dạng `key=value;key=value`. Ví dụ block size, độ dài pattern, alphabet size. |
| `seed` | integer/string | Seed của generator; giữ nguyên chính xác giá trị đã dùng để tái tạo input. |
| `input_sha256` | string | Mã băm SHA-256 của file input đã chạy, giống “dấu vân tay” giúp xác nhận các thuật toán dùng cùng file. Có thể lấy bằng `Get-FileHash .\input.txt -Algorithm SHA256`. |
| `generator_version` | string | Phiên bản hoặc Git commit của chương trình generator đã tạo input. |
| `repeat` | integer | Thứ tự lần lặp của cùng một cấu hình, ví dụ `1`, `2`, `3`. |
| `elapsed_ms` | decimal | Thời gian đọc và tìm kiếm end-to-end, đơn vị mili-giây; để trống nếu không hoàn thành. |
| `heap_before_bytes` | integer | Heap JVM đang sử dụng ngay trước khi chạy, tính theo byte. |
| `heap_peak_bytes` | integer | Tổng peak used-heap của các heap memory pool trong lúc chạy, tính theo byte. |
| `ops_count` | decimal/integer | Kết quả từ một lần chạy `--count` riêng; để trống trên dòng chỉ đo thời gian. |
| `output_check` | string | Kết quả xác thực, dạng `count=<so_match>;check=<checksum>`. Các thuật toán chạy cùng input/pattern phải có giá trị này giống nhau. |
| `status` | string | `ok`, `timeout`, `crash`, `oom` (hết bộ nhớ) hoặc `wrong` (kết quả sai). |
| `xmx` | string | Giới hạn heap của JVM, ví dụ `2g` khi chạy Java với `-Xmx2g`. Nên cố định cùng một giá trị khi so sánh. |
| `machine_id` | string | Mã nhận diện ổn định của máy dùng để đo. |
| `git_commit` | string | Git commit của mã nguồn thuật toán tại lúc đo; giúp biết chính xác phiên bản code nào tạo ra số liệu. Lấy bằng `git rev-parse --short HEAD`. |
| `build_ms` | decimal | Thời gian tiền xử lý của Pattern Matching; cột riêng của bài. |
| `throughput_mibs` | decimal | Tốc độ xử lý input, đơn vị MiB/giây, tính từ `elapsed_ms`; cột riêng của bài. |

## Quy ước `params` và `ops_count`

Đưa các cấu hình riêng của bài vào `params`, ví dụ:

```text
block_size_bytes=65536;pattern_set_id=PSET_001;pattern_count=10;pattern_length_min=8;pattern_length_max=16;alphabet_size=80;states_or_nodes=96
```

Với Brute Force và KMP, ở dòng `--count`, thêm `counter=byte_comparisons_per_byte` vào `params`. `ops_count` là tổng số lần so sánh byte chia cho `n`. Ví dụ input 10.000 byte có 18.700 lần so sánh thì `ops_count=1.87`.

Với Aho-Corasick, tạo các dòng `--count` riêng cho từng chỉ số. Trong `params` thêm `counter=state_transitions` hoặc `counter=failure_link_traversals`, và ghi số đếm tương ứng vào `ops_count`. Biến thể `dfa` phải có `failure_link_traversals=0` trong giai đoạn quét. Chỉ so sánh `ops_count` giữa các dòng có cùng `params.counter`, vì so sánh byte, chuyển trạng thái và đi failure link có đơn vị/ý nghĩa khác nhau.

## Ba dòng dữ liệu mẫu

> Các hash và commit dưới đây chỉ là dữ liệu minh hoạ. Khi chạy thật phải thay bằng giá trị thực tế.

| Trường | `RUN_0001` — đo thời gian KMP, lần 1 | `RUN_0002` — đo thời gian KMP, lần 2 | `RUN_0003` — đếm phép so sánh Brute Force |
| --- | --- | --- | --- |
| `run_id` | `RUN_0001` | `RUN_0002` | `RUN_0003` |
| `rq` | `RQ1` | `RQ1` | `RQ1` |
| `algorithm` | `kmp` | `kmp` | `bf` |
| `variant` | `base` | `base` | `base` |
| `dataset` | `T1` | `T1` | `T1` |
| `n` | `10485760` | `10485760` | `10485760` |
| `params` | `block_size_bytes=65536; pattern_length=12; alphabet_size=80` | `block_size_bytes=65536; pattern_length=12; alphabet_size=80` | `block_size_bytes=65536; pattern_length=12; alphabet_size=80; counter=byte_comparisons_per_byte` |
| `seed` | `42` | `42` | `42` |
| `input_sha256` | `a3f5c8d91b2e...` | `a3f5c8d91b2e...` | `a3f5c8d91b2e...` |
| `generator_version` | `v1.0` | `v1.0` | `v1.0` |
| `repeat` | `1` | `2` | `1` |
| `elapsed_ms` | `125.50` | `123.10` | *(để trống)* |
| `heap_before_bytes` | `5242880` | `5242880` | `5242880` |
| `heap_peak_bytes` | `8388608` | `8388608` | `8388608` |
| `ops_count` | *(để trống)* | *(để trống)* | `1.87` |
| `output_check` | `count=15; check=15000098` | `count=15; check=15000098` | `count=15; check=15000098` |
| `status` | `ok` | `ok` | `ok` |
| `xmx` | `2g` | `2g` | `2g` |
| `machine_id` | `LAB_PC_01` | `LAB_PC_01` | `LAB_PC_01` |
| `git_commit` | `abc1234` | `abc1234` | `abc1234` |
| `build_ms` | `0.03` | `0.03` | `0` |
| `throughput_mibs` | `79.68` | `81.23` | *(để trống)* |

- `RUN_0001` và `RUN_0002` là hai lần đo thời gian khác nhau của cùng một cấu hình KMP (`repeat=1`, `repeat=2`).
- `RUN_0003` là một lần đếm phép so sánh riêng cho Brute Force, nên `elapsed_ms` và `throughput_mibs` để trống, còn `ops_count=1.87`.

## Kiểm tra kết quả

Không lưu toàn bộ vị trí match. Mỗi khi match, tăng `count` và cộng `(start_position + 1) * 1000003 + pattern_id` vào `check` bằng phép cộng Java `long` có overflow; dùng `pattern_id=0` khi chỉ tìm một pattern. Lưu hai giá trị vào `output_check` theo dạng `count=<count>;check=<check>`.

Các thuật toán thành công trên cùng input và pattern set phải có `output_check` giống nhau. Trong RQ1/RQ2, Brute Force và KMP phải khớp nhau; trong RQ3, k-KMP và hai biến thể Aho-Corasick phải khớp nhau. Một lần chạy lỗi vẫn được giữ trong CSV với `status` tương ứng và để trống các chỉ số hiệu năng không có.
