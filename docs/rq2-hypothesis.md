# PHÂN TÍCH KHOA HỌC VÀ XÂY DỰNG GIẢ THUYẾT NGHIÊN CỨU CHO RQ2

- **Đề tài:** Tìm mẫu trong log lớn: Brute Force, KMP và Aho–Corasick (CSD201 - Fall 2026)
- **Thành viên chủ trì:** Hải (Kỹ sư Thuật toán KMP / KMP Lead Dev - Cặp 2)
- **Sản phẩm bàn giao D2:** Đoạn phân tích và phát biểu giả thuyết khoa học cho RQ2

---

## 1. CÂU HỎI NGHIÊN CỨU RQ2 (RESEARCH QUESTION 2)

> **RQ2:** *Hiệu năng tìm kiếm (thời gian thực thi, thông lượng MiB/s và số phép so sánh ký tự) của giải thuật KMP so với Brute Force thay đổi như thế nào dưới tác động của kích thước bảng chữ cái $\sigma$, độ dài mẫu $m$, và tập dữ liệu bất lợi $T_4$ (dạng văn bản 'aaaa...a' và mẫu 'aaaa...b')?*

---

## 2. CƠ SỞ LÝ THUYẾT VÀ PHÂN TÍCH TOÁN HỌC

### 2.1. Phân tích ảnh hưởng của kích thước bảng chữ cái $\sigma$ (Alphabet Size)

Giả sử văn bản $T$ gồm $n$ ký tự và mẫu $P$ gồm $m$ ký tự được sinh ngẫu nhiên độc lập theo phân phối đều trên bảng chữ cái kích thước $\sigma$:
- Xác suất để một ký tự ngẫu nhiên của văn bản khớp với ký tự đầu tiên của mẫu là:
  $$P(\text{match}) = \frac{1}{\sigma}$$
- Xác suất mismatch xảy ra ngay tại ký tự đầu tiên ($j = 0$) là:
  $$P(\text{mismatch tại vị trí 0}) = 1 - \frac{1}{\sigma}$$

#### Trường hợp bảng chữ cái lớn ($\sigma \approx 70 - 256$, log thực tế):
- Trong các tệp log hệ thống thực tế (Apache, HDFS, Linux Syslog), dữ liệu sử dụng bảng mã ASCII / UTF-8 mở rộng, tập ký tự xuất hiện thực tế thường có $\sigma \ge 70$.
- Xác suất trượt ngay từ ký tự đầu tiên lên tới:
  $$P(\text{mismatch}) \ge 1 - \frac{1}{70} \approx 98.57\% \quad (\text{với } \sigma = 256 \text{ là } 99.61\%)$$
- **Kỳ vọng số phép so sánh của Brute Force:**
  $$E[C_{\text{BF}}] = (n - m + 1) \sum_{k=1}^{m} k \cdot \left(\frac{1}{\sigma}\right)^{k-1} \left(1 - \frac{1}{\sigma}\right) \approx n \cdot \frac{\sigma}{\sigma - 1}$$
  Khi $\sigma = 70$, $E[C_{\text{BF}}] \approx 1.014 \times n$. Nghĩa là trên thực tế, Brute Force hầu như chỉ cần đúng $1$ phép so sánh cho mỗi byte văn bản!
- **Hệ quả phần cứng:** Vòng lặp Brute Force cực kỳ đơn giản (con trỏ tăng tuần tự, không có rẽ nhánh phụ thuộc mảng tra cứu). Bộ dự đoán rẽ nhánh của CPU (Branch Predictor) đạt độ chính xác gần như $99\%$, tận dụng tối đa kiến trúc đường ống lệnh (Instruction Pipeline) và bộ nhớ đệm L1/L2.
- **Ngược lại với KMP:** KMP phải tốn thêm chi phí nạp giá trị từ bảng $\pi[j-1]$, rẽ nhánh kiểm tra `while (j > 0 && pattern[j] != c)`, gây ra chi phí phụ trội (overhead) cao hơn trên mỗi byte.

#### Trường hợp bảng chữ cái cực nhỏ ($\sigma = 2$, nhị phân hoặc chuỗi $\{a, b\}$):
- Xác suất trùng tiền tố là $1/2$. Brute Force sẽ thường xuyên so sánh trùng nhiều ký tự liên tiếp trước khi gặp mismatch, buộc phải quay lui con trỏ văn bản nhiều lần. Số phép so sánh trung bình tăng lên $\approx 2n$ hoặc lớn hơn rất nhiều nếu dữ liệu có tính tuần hoàn lặp.

---

### 2.2. Phân tích ảnh hưởng của độ dài mẫu $m$ (Pattern Length)

1. **Chi phí tiền xử lý (Preprocessing Overhead):**
   - KMP cần xây dựng bảng tiền tố $\pi$ với chi phí thời gian $O(m)$ và bộ nhớ $O(m)$ bytes.
   - Brute Force không cần tiền xử lý ($O(1)$).
   - Với các mẫu ngắn phổ biến trong log ($m \le 8$, như `ERROR`, `FATAL`, `404`), chi phí dựng $\pi$ của KMP là không đáng kể, nhưng cũng không đem lại lợi thế rõ rệt do số bước quay lui của Brute Force quá ít.
2. **Khi độ dài mẫu $m$ tăng lớn ($m = 64, 256, 1024$):**
   - Nếu văn bản có chứa các tiền tố khớp một phần với mẫu, KMP phát huy ưu thế tuyệt đối nhờ bảng $\pi$: khi trượt ở ký tự thứ $j$, KMP lập tức chuyển trạng thái về $j \leftarrow \pi[j-1]$ mà không cần đọc lại các byte văn bản đã qua.

---

### 2.3. Phân tích tập dữ liệu bất lợi $T_4$ (Adversarial Dataset - Trường hợp suy biến tồi nhất)

Tập dữ liệu $T_4$ được thiết kế đặc biệt để kích hoạt trường hợp xấu nhất (Worst-Case Complexity):
- **Văn bản $T_4$:** Chuỗi đồng nhất gồm toàn ký tự `'a'` với độ dài $n$:
  $$T_4 = \underbrace{\text{'a' 'a' 'a' \dots 'a'}}_{n \text{ ký tự}}$$
- **Mẫu $P$:** Gồm $m-1$ ký tự `'a'` và kết thúc bằng ký tự `'b'`:
  $$P = \underbrace{\text{'a' 'a' 'a' \dots 'a'}}_{m-1 \text{ ký tự}} \text{'b'}$$

#### Phân tích hành vi của Brute Force trên $T_4$:
1. Tại mỗi vị trí $i$ ($0 \le i \le n - m$):
   - So sánh $P[0 \dots m-2]$ với $T_4[i \dots i+m-2]$: Trùng khớp hoàn toàn $m-1$ ký tự `'a'`.
   - So sánh $P[m-1]$ (`'b'`) với $T_4[i+m-1]$ (`'a'`): Xảy ra **mismatch** ở ký tự cuối cùng!
   - Đã tiêu tốn đúng $m$ phép so sánh.
2. Brute Force dịch mẫu sang vị trí $i+1$, quay lui con trỏ văn bản từ $i+m-1$ về $i+1$, và lặp lại toàn bộ quá trình so sánh từ đầu.
3. Tổng số phép so sánh của Brute Force:
   $$C_{\text{BF}}(T_4) = (n - m + 1) \times m = \Theta(n \times m)$$
- **Ví dụ định lượng:** Với file log $n = 1\text{ GB} \approx 10^9$ bytes và mẫu $m = 1.000$:
  $$C_{\text{BF}} \approx 10^9 \times 1.000 = 10^{12} \text{ phép so sánh!}$$
  Thời gian thực thi của Brute Force sẽ bị suy biến hoàn toàn (từ vài giây tăng vọt lên hàng giờ).

#### Phân tích hành vi của KMP trên $T_4$:
1. Bảng tiền tố của mẫu $P = \text{'a'}^{m-1}\text{'b'}$:
   - Các tiền tố $\text{'a'}^k$ có $\pi[k] = k$ với mọi $0 \le k \le m-2$.
   - $\pi = [0, 1, 2, 3, \dots, m-2, 0]$.
2. Khi duyệt qua văn bản $T_4$:
   - Trạng thái $j$ tăng liên tục từ $0$ lên $m-1$.
   - Khi đọc ký tự `'a'` tiếp theo trong khi đang ở trạng thái $j = m-1$, thuật toán so sánh với $P[m-1]$ (`'b'`) $\rightarrow$ mismatch.
   - KMP lui trạng thái: $j \leftarrow \pi[m-2] = m-2$.
   - Tiếp tục kiểm tra byte `'a'` với $P[m-2]$ (`'a'`) $\rightarrow$ khớp! Biến $j$ lập tức tăng lại thành $m-1$.
3. **Quan trọng nhất:** Con trỏ văn bản **tiếp tục tăng tuần tự mà không hề quay lui**. Mỗi byte văn bản chỉ được xử lý đúng một lần.
4. Tổng số phép so sánh của KMP trên $T_4$:
   $$C_{\text{KMP}}(T_4) \le 2n = \Theta(n)$$
- KMP hoàn toàn miễn nhiễm với sự suy biến, giữ vững độ phức tạp thời gian tuyến tính $O(n)$ bất kể giá trị $m$ lớn đến đâu!

---

## 3. CÁC PHÁT BIỂU GIẢ THUYẾT KHOA HỌC CHÍNH THỨC (FORMAL HYPOTHESES)

Dựa trên phân tích lý thuyết giải thuật và kiến trúc phần cứng, Cặp 2 đề xuất 3 giả thuyết nghiên cứu chính thức cho RQ2:

### Giả thuyết 2.1 (Worst-Case Immunity & Speedup trên tập $T_4$):
> **Hypothesis 2.1:** *Trên tập dữ liệu bất lợi $T_4$ ($T_4 = \text{'a'}^n$ và $P = \text{'a'}^{m-1}\text{'b'}$), thời gian thực thi của Brute Force tăng trưởng theo hàm bậc hai $\Theta(n \times m)$, trong khi KMP duy trì thời gian tăng trưởng tuyến tính $\Theta(n)$ độc lập với $m$. Hệ số tăng tốc (Speedup factor $S = T_{\text{BF}} / T_{\text{KMP}}$) sẽ tăng tỷ lệ thuận tuyến tính với độ dài mẫu $m$.*

### Giả thuyết 2.2 (Ngưỡng kích thước bảng chữ cái $\sigma$):
> **Hypothesis 2.2:** *Khi kích thước bảng chữ cái giảm dần từ $\sigma = 256$ xuống $\sigma = 2$ (dữ liệu nhị phân hoặc chuỗi 2 ký tự), số lần quay lui của Brute Force tăng phi tuyến, dẫn đến thông lượng (Throughput) của Brute Force suy giảm mạnh. Tại ngưỡng $\sigma \le 4$, KMP sẽ bắt đầu vượt trội hơn Brute Force về cả thời gian chạy lẫn thông lượng xử lý.*

### Giả thuyết 2.3 (Nghịch lý hiệu năng trên Log thực tế - Overhead Asymmetry):
> **Hypothesis 2.3:** *Trên dữ liệu log server thực tế với bảng chữ cái ASCII tự nhiên ($\sigma \approx 70-90$) và các mẫu tìm kiếm ngắn đặc trưng ($m \le 16$), thuật toán Brute Force sẽ đạt thông lượng (MiB/s) cao hơn hoặc tương đương KMP (chênh lệch từ $5\% - 20\%$). Nguyên nhân là do chi phí kiểm tra điều kiện lặp và truy xuất mảng $\pi$ của KMP cao hơn chi phí của các bước mismatch sớm ($P \approx 99\%$) của Brute Force.*

---

## 4. MA TRẬN DỰ ĐOÁN KẾT QUẢ THỰC NGHIỆM CHO RQ2

| Kịch bản kiểm thử | $\sigma$ | Độ dài $m$ | Dự đoán số phép so sánh BF | Dự đoán số phép so sánh KMP | Thuật toán chiến thắng dự kiến |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Log thực tế (HTTP/HDFS)** | $\approx 80$ | Ngắn ($m=4..8$) | $\approx 1.01 \times n$ | $\approx 1.00 \times n$ + tra bảng | **Brute Force** (do ít overhead) |
| **Log thực tế (URL/Trace)** | $\approx 80$ | Dài ($m=64..256$) | $\approx 1.01 \times n$ | $\approx 1.00 \times n$ | **Tương đương** |
| **Chuỗi nhị phân ngẫu nhiên** | $2$ | Trung bình ($m=16$) | $\approx 2.00 \times n$ | $\approx 1.20 \times n$ | **KMP** |
| **Tập dữ liệu bất lợi $T_4$** | $2$ | Bất kỳ ($m=100$) | $\mathbf{\approx 100 \times n}$ | $\mathbf{\approx 2 \times n}$ | **KMP áp đảo hoàn toàn** ($S \approx 50x - 100x$) |

---

## 5. ĐÓNG GÓP CHO BUỔI VẤN ĐÁP 1-1 CÁ NHÂN (VIVA DEFENSE PREPARATION)

Phần phân tích và giả thuyết trên trang bị cho thành viên Hải các luận điểm bảo vệ cốt lõi:
1. **Bản chất toán học của bảng $\pi$:** Giải thích tường tận tại sao KMP đạt $O(n)$ thông qua phương pháp phân tích khấu hao (Amortized Analysis bằng thế năng $\Phi = j$).
2. **Lý do KMP không luôn nhanh hơn Brute Force:** Phân tích sâu về kiến trúc máy tính hiện đại (CPU branch prediction, cache miss, register allocation) so với độ phức tạp lý thuyết.
3. **Ý nghĩa của việc giữ trạng thái $j$ qua block:** Khẳng định tính đúng đắn khi áp dụng trên Big Data mà không cần tốn chi phí RAM.
