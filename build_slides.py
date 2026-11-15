import os
import sys
from reportlab.lib.pagesizes import landscape
from reportlab.pdfgen import canvas
from reportlab.lib.colors import HexColor
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

# Register Windows Arial fonts for full Vietnamese Unicode support
pdfmetrics.registerFont(TTFont('Arial', r'C:\Windows\Fonts\arial.ttf'))
pdfmetrics.registerFont(TTFont('Arial-Bold', r'C:\Windows\Fonts\arialbd.ttf'))
pdfmetrics.registerFont(TTFont('Arial-Italic', r'C:\Windows\Fonts\ariali.ttf'))

ROOT_DIR = r'D:\COLLEGE\MYSEMETER\FALL26\CSD201\ASSIGNMENT\GROUP_4_CSD201'
FIG_DIR = os.path.join(ROOT_DIR, 'results', 'figures')
PDF_OUT = os.path.join(ROOT_DIR, 'slides.pdf')
PPTX_OUT = os.path.join(ROOT_DIR, 'slides.pptx')

# Colors
C_NAVY = HexColor('#0F2942')
C_BLUE = HexColor('#2563EB')
C_LIGHT_BG = HexColor('#F8FAFC')
C_CARD_BG = HexColor('#FFFFFF')
C_BORDER = HexColor('#E2E8F0')
C_TEXT_DARK = HexColor('#0F172A')
C_TEXT_MUTED = HexColor('#475569')
C_GREEN = HexColor('#059669')
C_RED = HexColor('#DC2626')

def draw_header(c, title, category="CSD201 — ĐỒ ÁN NHÓM: PATTERN MATCHING"):
    # Header background banner
    c.setFillColor(HexColor('#FFFFFF'))
    c.rect(0, 480, 960, 60, fill=1, stroke=0)
    
    # Top blue accent line
    c.setFillColor(C_BLUE)
    c.rect(0, 536, 960, 4, fill=1, stroke=0)
    
    # Category tag
    c.setFillColor(C_BLUE)
    c.setFont('Arial-Bold', 9)
    c.drawString(45, 518, category.upper())
    
    # Title
    c.setFillColor(C_TEXT_DARK)
    c.setFont('Arial-Bold', 18)
    c.drawString(45, 494, title)
    
    # Divider line
    c.setStrokeColor(C_BORDER)
    c.setLineWidth(1)
    c.line(45, 480, 915, 480)

def draw_footer(c, current_slide, total_slides=8):
    c.setStrokeColor(C_BORDER)
    c.setLineWidth(1)
    c.line(45, 30, 915, 30)
    
    c.setFillColor(C_TEXT_MUTED)
    c.setFont('Arial', 8)
    c.drawString(45, 18, "FPT University Quy Nhon — SE20C — Nhóm 4: Đinh Vũ Phương Khánh, Nguyễn Hoài Nhi, Phan Ngọc Bảo Trâm, Nguyễn Đặng Trường Hải, Lương Triệu Vỹ, Hà Văn Sang")
    
    page_str = f"Slide {current_slide} / {total_slides}"
    c.setFont('Arial-Bold', 9)
    c.drawRightString(915, 18, page_str)

def draw_card(c, x, y, w, h, title=None, border_color=C_BORDER, fill_color=C_CARD_BG, accent_color=None):
    c.setFillColor(fill_color)
    c.setStrokeColor(border_color)
    c.roundRect(x, y, w, h, 6, fill=1, stroke=1)
    
    if accent_color:
        c.setFillColor(accent_color)
        c.roundRect(x, y + h - 4, w, 4, 2, fill=1, stroke=0)
        
    if title:
        c.setFillColor(C_NAVY)
        c.setFont('Arial-Bold', 12)
        c.drawString(x + 14, y + h - 22, title)

def build_pdf_slides():
    print("-> Đang khởi tạo file PDF slides với ReportLab (Phông Arial Unicode tiếng Việt)...")
    c = canvas.Canvas(PDF_OUT, pagesize=(960, 540))
    
    # ==========================================
    # SLIDE 1: COVER
    # ==========================================
    c.setFillColor(HexColor('#0F172A'))
    c.rect(0, 0, 960, 540, fill=1, stroke=0)
    
    c.setFillColor(HexColor('#1E3A8A'))
    c.rect(0, 525, 960, 15, fill=1, stroke=0)
    c.setFillColor(C_BLUE)
    c.rect(0, 520, 960, 5, fill=1, stroke=0)
    
    # Badge
    c.setFillColor(HexColor('#1E293B'))
    c.roundRect(60, 440, 310, 26, 4, fill=1, stroke=0)
    c.setFillColor(HexColor('#60A5FA'))
    c.setFont('Arial-Bold', 9)
    c.drawString(72, 448, "CSD201 — DATA STRUCTURES & ALGORITHMS (JAVA)")
    
    # Title
    c.setFillColor(HexColor('#FFFFFF'))
    c.setFont('Arial-Bold', 25)
    c.drawString(60, 395, "ĐÁNH GIÁ THỰC NGHIỆM HIỆU NĂNG THUẬT TOÁN")
    c.drawString(60, 360, "TÌM MẪU CHUỖI TRONG LOG HỆ THỐNG")
    
    # Subtitle English
    c.setFillColor(HexColor('#94A3B8'))
    c.setFont('Arial-Italic', 12.5)
    c.drawString(60, 330, "Empirical Performance Evaluation of Brute Force, KMP, and Aho-Corasick on Large-Scale Logs")
    
    # Info Grid (3 Cards for 3 Pairs)
    pairs = [
        ("CẶP 1: RQ1 & INFRASTRUCTURE", "Đinh Vũ Phương Khánh (Nhóm trưởng)\nHà Văn Sang", "#3B82F6"),
        ("CẶP 2: RQ2 & KMP STREAMING", "Nguyễn Hoài Nhi (KMP Lead)\nNguyễn Đặng Trường Hải", "#10B981"),
        ("CẶP 3: RQ3 & AHO-CORASICK", "Lương Triệu Vỹ (AC Lead)\nPhan Ngọc Bảo Trâm (Paper Lead)", "#F59E0B")
    ]
    
    for idx, (p_title, members, col) in enumerate(pairs):
        bx = 60 + idx * 285
        by = 135
        bw = 270
        bh = 130
        
        c.setFillColor(HexColor('#1E293B'))
        c.setStrokeColor(HexColor('#334155'))
        c.roundRect(bx, by, bw, bh, 6, fill=1, stroke=1)
        
        c.setFillColor(HexColor(col))
        c.roundRect(bx, by + bh - 4, bw, 4, 2, fill=1, stroke=0)
        
        c.setFillColor(HexColor(col))
        c.setFont('Arial-Bold', 9.5)
        c.drawString(bx + 14, by + bh - 22, p_title)
        
        c.setFillColor(HexColor('#E2E8F0'))
        c.setFont('Arial', 10)
        lines = members.split('\n')
        c.drawString(bx + 14, by + bh - 50, lines[0])
        c.drawString(bx + 14, by + bh - 72, lines[1])
        
        c.setFillColor(HexColor('#64748B'))
        c.setFont('Arial', 8)
        c.drawString(bx + 14, by + 18, "Phản biện chéo & Kiểm tra tính đúng")

    # Bottom Meta
    c.setFillColor(HexColor('#64748B'))
    c.setFont('Arial', 9)
    c.drawString(60, 70, "Lớp: SE20C | Học kỳ: Fall 2026 | FPT University Quy Nhon | Giảng viên hướng dẫn: CSD201 Instructor")
    c.drawRightString(900, 70, "Báo cáo Bảo vệ Đồ án (5 Phút) • Slide 1 / 8")
    
    c.showPage()
    
    # ==========================================
    # SLIDE 2: BỐI CẢNH & 3 CÂU HỎI NGHIÊN CỨU
    # ==========================================
    draw_header(c, "Bối cảnh Thực tế & 3 Câu hỏi Nghiên cứu (Research Questions)")
    
    draw_card(c, 45, 60, 390, 400, "BỐI CẢNH VÀ ĐỘNG LỰC THỰC TIỄN", accent_color=C_BLUE)
    c.setFillColor(C_TEXT_DARK)
    c.setFont('Arial-Bold', 10.5)
    c.drawString(60, 420, "1. Thách thức phân tích Log quy mô lớn:")
    c.setFont('Arial', 9)
    c.drawString(60, 400, "• Hệ thống cloud/microservices phát sinh hàng chục GiB log mỗi giờ.")
    c.drawString(60, 382, "• Tìm kiếm mã lỗi (Error Signatures) đòi hỏi thông lượng cực cao.")
    c.drawString(60, 364, "• Bộ nhớ RAM bị giới hạn: Không thể nạp toàn bộ file log vào RAM.")
    
    c.setFont('Arial-Bold', 10.5)
    c.drawString(60, 330, "2. Vấn đề khoa học cần giải quyết:")
    c.setFont('Arial', 9)
    c.drawString(60, 310, "• Đơn mẫu (k = 1): Liệu KMP O(n) có luôn nhanh hơn Brute Force?")
    c.drawString(60, 292, "• Điều kiện đối kháng: Khi nào Brute Force bị thoái hóa nặng O(n*m)?")
    c.drawString(60, 274, "• Đa mẫu (k = 10 -> 100): Chạy lặp k-KMP hay dựng Aho-Corasick?")
    
    c.setFont('Arial-Bold', 10.5)
    c.drawString(60, 240, "3. Mục tiêu chuẩn đầu ra CLO:")
    c.setFont('Arial', 9)
    c.drawString(60, 220, "• Tự cài đặt 100% từ số 0 (from scratch) trên JDK 8.")
    c.drawString(60, 202, "• Kiểm chứng thực nghiệm có đối chứng Oracle Checksum 64-bit.")
    c.drawString(60, 184, "• Đo đạc tái lập 100% trên JVM cô lập (-Xmx2g).")
    
    draw_card(c, 455, 60, 460, 400, "3 CÂU HỎI NGHIÊN CỨU TRỌNG TÂM (RQS)", accent_color=C_GREEN)
    
    rqs = [
        ("RQ1: Quy mô dữ liệu (Scaling trên Log thật T1)", 
         "So sánh Brute Force vs. KMP khi kích thước log tăng từ 16 MiB đến 256 MiB.\n"
         "-> Giả thuyết: Trong log thực tế có cấu trúc, KMP tránh quay lui con trỏ nên sẽ đạt thông lượng vượt trội hơn Brute Force.", C_BLUE),
        ("RQ2: Ảnh hưởng Bảng chữ cái & Tập đối kháng T4", 
         "Giữ kích thước 64 MiB, khảo sát alphabet ngẫu nhiên (σ=256) và tập xấu nhất a^(N-1)b với độ dài mẫu m tăng từ 8 -> 64 -> 1024.\n"
         "-> Giả thuyết: Brute Force sụp đổ O(n*m), trong khi KMP giữ vững O(n).", C_RED),
        ("RQ3: Đa mẫu Aho-Corasick vs. Multi-KMP", 
         "Tìm đồng thời k mẫu (k = 1, 10, 100) trên 64 MiB log.\n"
         "-> Giả thuyết: k-KMP suy giảm tuyến tính O(k*n); Aho-Corasick quét 1 lượt duy nhất; AC-DFA đạt O(1) nhưng tốn thêm bộ nhớ RAM.", HexColor('#F59E0B'))
    ]
    
    for i, (rq_title, desc, col) in enumerate(rqs):
        ry = 345 - i * 115
        c.setFillColor(HexColor('#F8FAFC'))
        c.setStrokeColor(C_BORDER)
        c.roundRect(470, ry, 430, 95, 4, fill=1, stroke=1)
        
        c.setFillColor(col)
        c.setFont('Arial-Bold', 10)
        c.drawString(482, ry + 75, rq_title)
        
        c.setFillColor(C_TEXT_DARK)
        c.setFont('Arial', 8.5)
        lines = desc.split('\n')
        c.drawString(482, ry + 52, lines[0])
        c.setFillColor(HexColor('#1E3A8A'))
        c.setFont('Arial-Italic', 8.5)
        c.drawString(482, ry + 32, lines[1])

    draw_footer(c, 2)
    c.showPage()
    
    # ==========================================
    # SLIDE 3: KIẾN TRÚC KỸ THUẬT & KIỂM SOÁT ĐO ĐẠC
    # ==========================================
    draw_header(c, "Kiến trúc Streaming Block Buffer & Giao thức Đo lường Thực nghiệm")
    
    draw_card(c, 45, 60, 420, 400, "1. KIẾN TRÚC STREAMING BLOCK-BUFFERED", accent_color=C_BLUE)
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "Xử lý dòng dữ liệu không phụ thuộc dung lượng RAM:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 402, "• File log được đọc theo từng khối cố định: Block B = 1 MiB.")
    c.drawString(60, 384, "• Không nạp cả file vào heap; bộ nhớ đệm chỉ cấp phát đúng B bytes.")
    
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_NAVY)
    c.drawString(60, 350, "Cơ chế duy trì tính liên tục (Continuity Guarantee):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 332, "• Brute Force: Lưu đệm carry-over (m - 1) byte ở cuối block trước")
    c.drawString(70, 316, "ghép vào đầu block sau để không bỏ sót mẫu vắt ngang ranh giới.")
    c.drawString(60, 296, "• KMP & Aho-Corasick: Bảo toàn con trỏ trạng thái (currState và")
    c.drawString(70, 280, "globalOffset) xuyên suốt qua ranh giới các block không cần reset.")
    
    # Invariant formula box
    c.setFillColor(HexColor('#F1F5F9'))
    c.setStrokeColor(C_BORDER)
    c.roundRect(60, 110, 390, 140, 4, fill=1, stroke=1)
    c.setFillColor(HexColor('#0F172A'))
    c.setFont('Arial-Bold', 9.5)
    c.drawString(72, 230, "BỘ KIỂM ĐỊNH INVARIANT ORACLE (ChecksumConsumer):")
    c.setFont('Arial', 8.5)
    c.drawString(72, 208, "1. Tổng số lần khớp (Match Count):")
    c.setFont('Arial-Bold', 9)
    c.drawString(90, 192, "Count = ∑ 1   (i = 1 .. z)")
    c.setFont('Arial', 8.5)
    c.drawString(72, 170, "2. Mã kiểm tra tích lũy 64-bit (Checksum):")
    c.setFont('Arial-Bold', 9)
    c.drawString(90, 154, "Checksum = ∑ ((offset_i + 1) × 1000003 + patternId_i) (mod 2^64)")
    c.setFont('Arial-Italic', 8)
    c.setFillColor(C_GREEN)
    c.drawString(72, 126, "✓ Mọi lượt benchmark đều đối chiếu khớp 100% với Java Oracle.")
    
    draw_card(c, 485, 60, 430, 400, "2. GIAO THỨC ĐO LƯỜNG CÔ LẬP & TÁI LẬP", accent_color=C_GREEN)
    
    items = [
        ("Tiến trình JVM độc lập", "Chạy qua ProcessBuilder bên ngoài IDE, áp dụng cờ cố định -Xmx2g để loại trừ ô nhiễm heap và rác classloader giữa các lượt."),
        ("JIT Warm-up bắt buộc", "Mỗi cấu hình chạy 3 lượt làm nóng không bấm giờ để trình biên dịch JIT C2 Tier 4 biên dịch mã máy tối ưu trước khi đo."),
        ("Đo thời gian độ phân giải cao", "Sử dụng System.nanoTime() đơn điệu (monotonic time), đo 5 lần lặp và tính giá trị trung bình (Mean / Median)."),
        ("Đo đỉnh bộ nhớ Heap thực tế", "Lấy mẫu liên tục qua JMX MemoryPoolMXBean.getPeakUsage() để ghi lại đúng đỉnh RAM tiêu thụ thực sự."),
        ("Phân lập độ trễ I/O đĩa", "Tích hợp đường cơ sở lib-read (chỉ đọc buffer không so khớp) để chứng minh I/O đĩa chỉ chiếm <5% tổng thời gian."),
        ("Ghi nhận kết quả chuẩn hóa", "Xuất tự động vào results/raw/benchmark_results.csv với đầy đủ 22 cột dữ liệu, seed, sha256 và git commit hash.")
    ]
    
    for idx, (title, desc) in enumerate(items):
        iy = 395 - idx * 62
        c.setFillColor(C_BLUE)
        c.setFont('Arial-Bold', 9.5)
        c.drawString(500, iy, f"• {title}:")
        c.setFillColor(C_TEXT_DARK)
        c.setFont('Arial', 8.5)
        if len(desc) > 85:
            c.drawString(512, iy - 14, desc[:85])
            c.drawString(512, iy - 26, desc[85:])
        else:
            c.drawString(512, iy - 14, desc)

    draw_footer(c, 3)
    c.showPage()
    
    # ==========================================
    # SLIDE 4: RQ1 - KẾT QUẢ QUY MÔ TRÊN LOG T1
    # ==========================================
    draw_header(c, "RQ1: Hiệu năng Thực nghiệm trên Log thật T1 (Brute Force vs. KMP)")
    
    draw_card(c, 45, 60, 420, 400, "KẾT QUẢ THỰC NGHIỆM TRÊN TẬP T1", accent_color=C_BLUE)
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "1. Số liệu thực nghiệm đo đạc (Mean Execution Time & Tput):")
    
    table_data = [
        ("Kích thước", "Brute Force", "KMP Algorithm", "So sánh Tput"),
        ("16 MiB", "82.86 ms (200.9 M/s)", "56.72 ms (292.4 M/s)", "KMP nhanh hơn 1.46×"),
        ("64 MiB", "301.01 ms (234.5 M/s)", "183.43 ms (354.2 M/s)", "KMP nhanh hơn 1.51×"),
        ("256 MiB", "2624.1 ms (122.5 M/s)", "2052.4 ms (153.0 M/s)", "KMP nhanh hơn 1.25×")
    ]
    
    ty = 390
    for r_idx, row in enumerate(table_data):
        bg = HexColor('#F1F5F9') if r_idx == 0 else (HexColor('#FFFFFF') if r_idx % 2 == 1 else HexColor('#F8FAFC'))
        c.setFillColor(bg)
        c.rect(60, ty - 18, 390, 20, fill=1, stroke=1)
        c.setFillColor(C_NAVY if r_idx == 0 else C_TEXT_DARK)
        c.setFont('Arial-Bold' if r_idx == 0 else 'Arial', 7.5 if r_idx > 0 else 8)
        c.drawString(65, ty - 13, row[0])
        c.drawString(135, ty - 13, row[1])
        c.drawString(245, ty - 13, row[2])
        c.setFillColor(C_GREEN if r_idx > 0 else C_NAVY)
        c.drawString(350, ty - 13, row[3])
        ty -= 20
        
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_NAVY)
    c.drawString(60, 290, "2. Phân tích Bản chất Khoa học:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 272, "• Trong log máy chủ có cấu trúc, các bản ghi liên tục lặp lại các chuỗi")
    c.drawString(70, 258, "tiền tố (timestamp '2026-11-15', tên thread, log level [INFO], [WARN]).")
    c.drawString(60, 240, "• Khi so khớp gần hết mẫu mới gặp sai khác (mismatch ở đuôi mẫu),")
    c.drawString(70, 226, "Brute Force bị buộc phải quay lui con trỏ (backtracking) về i+1, gây lãng")
    c.drawString(70, 212, "phí hàng triệu phép so sánh trùng lặp.")
    c.drawString(60, 194, "• KMP tra cứu bảng tiền tố π, trượt mẫu về vị trí tiền tố dài nhất mà")
    c.drawString(70, 180, "không bao giờ lùi con trỏ văn bản, đảm bảo thời gian tuyến tính O(n).")
    
    c.setFont('Arial-Bold', 9.5)
    c.setFillColor(C_BLUE)
    c.drawString(60, 150, "3. Mốc tham chiếu bộ đệm I/O (lib-read Baseline):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 134, "• Baseline lib-read chỉ mất 2.45 ms (16M) và 8.67 ms (64M) -> >7,500 MiB/s.")
    c.drawString(60, 120, "• Khẳng định I/O đĩa chỉ chiếm <5% tổng thời gian, năng lực xử lý")
    c.drawString(70, 106, "của thuật toán mới là yếu tố quyết định hiệu năng hệ thống.")
    
    draw_card(c, 485, 60, 430, 400, "BIỂU ĐỒ TĂNG TRƯỞNG THỜI GIAN THEO KÍCH THƯỚC FILE (FIG. 1)", accent_color=C_BLUE)
    fig1_path = os.path.join(FIG_DIR, 'fig3_line_rq1_scaling.png')
    if os.path.exists(fig1_path):
        c.drawImage(fig1_path, 495, 110, width=410, height=270, preserveAspectRatio=True)
        c.setFillColor(C_TEXT_MUTED)
        c.setFont('Arial-Italic', 8)
        c.drawString(500, 85, "Fig. 1: Đường màu đỏ (Brute Force) mất nhiều thời gian hơn đường màu xanh (KMP)")
        c.drawString(500, 72, "xuyên suốt từ 16 MiB đến 256 MiB trên dataset T1 (Số liệu thực tế 100% từ CSV).")
        
    draw_footer(c, 4)
    c.showPage()
    
    # ==========================================
    # SLIDE 5: RQ2 - ẢNH HƯỞNG ALPHABET & TẬP ĐỐI KHÁNG T4
    # ==========================================
    draw_header(c, "RQ2: Ảnh hưởng của Bảng chữ cái & Sự sụp đổ của Brute Force trên T4")
    
    draw_card(c, 45, 60, 420, 400, "PHÂN TÍCH HIỆU ỨNG BẢNG CHỮ CÁI & ADVERSARIAL", accent_color=C_RED)
    
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "1. Tập ngẫu nhiên đồng đều T3 (σ = 256, m = 64):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 402, "• Xác suất trùng ký tự đầu cực thấp: P(match) = 1/256 ≈ 0.39%.")
    c.drawString(60, 386, "• Bình quân sau 1.004 phép so sánh là phát hiện sai khớp ngay.")
    c.drawString(60, 370, "• Cả 2 thuật toán đều đạt thông lượng đỉnh: KMP đạt 829.88 MiB/s,")
    c.drawString(70, 356, "Brute Force đạt 486.40 MiB/s (79.6 ms vs. 134.7 ms).")
    
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_RED)
    c.drawString(60, 326, "2. Tập đối kháng bệnh lý T4 (Worst-case a^(N-1)b, m = 8..1024):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 308, "• KMP bảo vệ hệ thống tuyệt đối: Giữ vững thông lượng ổn định")
    c.drawString(70, 294, "quanh mức 448.6 – 467.6 MiB/s (137–143 ms), chứng minh O(n) tuyến tính.")
    c.drawString(60, 276, "• Brute Force sụp đổ thảm họa theo O(n × m):")
    
    c.setFillColor(HexColor('#FEF2F2'))
    c.setStrokeColor(HexColor('#F87171'))
    c.roundRect(60, 150, 390, 115, 4, fill=1, stroke=1)
    c.setFillColor(C_RED)
    c.setFont('Arial-Bold', 9)
    c.drawString(72, 246, "SỰ THOÁI HÓA BẬC HAI CỦA BRUTE FORCE TRÊN T4 (64 MiB):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(72, 228, "• m = 8:      315.65 ms  (Thông lượng: 208.36 MiB/s)")
    c.drawString(72, 210, "• m = 64:    1,727.65 ms (Thông lượng: 42.26 MiB/s  — tụt 5 lần)")
    c.drawString(72, 192, "• m = 1024: 25,297.78 ms (Thông lượng: 2.72 MiB/s   — tụt 76 lần!)")
    c.setFont('Arial-Bold', 8.5)
    c.setFillColor(C_RED)
    c.drawString(72, 168, "=> Chậm hơn KMP tới 165 lần! Nguy cơ tấn công Algorithmic DoS.")
    
    c.setFont('Arial-Bold', 9)
    c.setFillColor(C_NAVY)
    c.drawString(60, 126, "3. Kết luận RQ2:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 110, "Bắt buộc phải dùng KMP trong hệ thống mission-critical khi không thể")
    c.drawString(60, 96, "kiểm soát được nội dung đầu vào, nhằm ngăn chặn tấn công treo CPU.")
    
    draw_card(c, 485, 60, 430, 400, "SO SÁNH THÔNG LƯỢNG QUÉT THỰC TẾ GIỮA CÁC THUẬT TOÁN (FIG. 2)", accent_color=C_RED)
    fig2_path = os.path.join(FIG_DIR, 'fig2_bar_throughput_comparison.png')
    if os.path.exists(fig2_path):
        c.drawImage(fig2_path, 495, 110, width=410, height=270, preserveAspectRatio=True)
        c.setFillColor(C_TEXT_MUTED)
        c.setFont('Arial-Italic', 8)
        c.drawString(500, 85, "Fig. 2: Thông lượng quét (Throughput MiB/s) đo thực tế trên máy chạy.")
        c.drawString(500, 72, "KMP vượt trội ở đơn mẫu; Multi-KMP sụp đổ khi k tăng; Aho-Corasick duy trì tốc độ cao.")
        
    draw_footer(c, 5)
    c.showPage()
    
    # ==========================================
    # SLIDE 6: RQ3 - TÌM KIẾM ĐA MẪU AHO-CORASICK VS MULTI-KMP
    # ==========================================
    draw_header(c, "RQ3: Đa mẫu Aho–Corasick vs. Multi-KMP & Đánh đổi Bộ nhớ (Space-Time)")
    
    draw_card(c, 45, 60, 430, 400, "HIỆU NĂNG ĐA MẪU K = 1 ĐẾN 100", accent_color=HexColor('#F59E0B'))
    
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "1. Sự suy thoái nghiêm trọng của k-KMP khi k tăng:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 402, "• k = 1:   336.66 ms  (Thông lượng: 192.40 MiB/s)")
    c.drawString(60, 386, "• k = 10:  4,411.08 ms (Thông lượng: 14.76 MiB/s — chậm 13.1×)")
    c.drawString(60, 370, "• k = 100: 28,031.81 ms (Thông lượng: 2.41 MiB/s — mất 28.03 giây!)")
    c.drawString(60, 354, "=> Phù hợp tuyệt đối với lý thuyết: k-KMP tốn O(k × n) thời gian.")
    
    c.setFont('Arial-Bold', 10)
    c.setFillColor(C_GREEN)
    c.drawString(60, 326, "2. Aho-Corasick quét 1 lượt duy nhất qua Automaton:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 308, "• Ở k = 100 mẫu trên cùng file log 64 MiB:")
    c.drawString(70, 292, "-> AC-DFA hoàn thành trong 487.38 ms (138.95 MiB/s) — Nhanh hơn 57.5×!")
    c.drawString(70, 276, "-> AC-Fail hoàn thành trong 545.31 ms (124.15 MiB/s) — Nhanh hơn 51.4×!")
    
    fig3_path = os.path.join(FIG_DIR, 'fig4_line_rq3_multi_pattern.png')
    if os.path.exists(fig3_path):
        c.drawImage(fig3_path, 60, 75, width=400, height=185, preserveAspectRatio=True)
        c.setFillColor(C_TEXT_MUTED)
        c.setFont('Arial-Italic', 7.5)
        c.drawString(60, 68, "Fig. 3: Thang log-log chứng minh k-KMP cắm đầu lao dốc, AC giữ tốc độ cao.")
        
    draw_card(c, 495, 60, 420, 400, "ĐÁNH ĐỔI BỘ NHỚ RAM TẠI K = 100 MẪU (FIG. 4)", accent_color=HexColor('#8B5CF6'))
    
    fig4_path = os.path.join(FIG_DIR, 'fig1_pie_memory_comparison.png')
    if os.path.exists(fig4_path):
        c.drawImage(fig4_path, 510, 205, width=390, height=215, preserveAspectRatio=True)
        
    c.setFont('Arial-Bold', 9.5)
    c.setFillColor(C_NAVY)
    c.drawString(510, 190, "Phân tích Đánh đổi Không gian - Thời gian (Space-Time Trade-off):")
    c.setFont('Arial', 8)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(510, 172, "• k-KMP chỉ tốn 66.8 MB (19.7%): Chỉ cấp phát k mảng số nguyên π.")
    c.drawString(510, 156, "• AC-DFA tốn 129.0 MB (38.0%): Bảng chuyển trạng thái dfa[states][256].")
    c.drawString(510, 140, "• AC-Fail tốn 143.6 MB (42.3%): Đối tượng node trên Trie byte.")
    c.setFont('Arial-Bold', 8.5)
    c.setFillColor(HexColor('#8B5CF6'))
    c.drawString(510, 118, "Lợi thế của AC-DFA: Bảng chuyển O(1) giúp AC-DFA nhanh hơn")
    c.drawString(510, 104, "AC-Fail ~12% thông lượng nhờ loại bỏ hoàn toàn việc duyệt failure links.")
    
    draw_footer(c, 6)
    c.showPage()
    
    # ==========================================
    # SLIDE 7: KẾT QUẢ TRÁI DỰ ĐOÁN & THREATS TO VALIDITY
    # ==========================================
    draw_header(c, "Kết quả Trái Dự đoán & Đánh giá Giới hạn (Threats to Validity)")
    
    draw_card(c, 45, 60, 420, 400, "KẾT QUẢ THỰC NGHIỆM TRÁI DỰ ĐOÁN BAN ĐẦU", accent_color=HexColor('#EC4899'))
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "1. Dự đoán ban đầu của nhóm (Initial Hypothesis):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 402, "• Nhóm từng giả định rằng trên log thông thường, Brute Force sẽ chạy")
    c.drawString(70, 386, "nhanh hơn KMP nhờ mã nguồn tối giản, tận dụng tối đa cache L1/L2 của")
    c.drawString(70, 370, "CPU và không tốn chi phí quản lý bảng tiền tố π hay bộ nhớ gián tiếp.")
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_RED)
    c.drawString(60, 336, "2. Kết quả đo thực tế bất ngờ (Empirical Finding):")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 318, "• KMP lại chiến thắng Brute Force từ 25% đến 51% về thông lượng")
    c.drawString(70, 302, "xuyên suốt từ 16 MiB đến 256 MiB trên tập log thật T1!")
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_GREEN)
    c.drawString(60, 268, "3. Nguyên nhân Khoa học được phát hiện:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 250, "• Cấu trúc log máy chủ: Các chuỗi timestamp, header, ID tiến trình")
    c.drawString(70, 234, "khiến dữ liệu có tần suất trùng tiền tố rất cao.")
    c.drawString(60, 216, "• Hiện tượng phạt lùi (Backtracking Penalty): Brute Force liên tục")
    c.drawString(70, 200, "phải so khớp lại từ ký tự i+1 mỗi khi gặp sai khác ở cuối mẫu.")
    c.drawString(60, 182, "• KMP dịch chuyển con trỏ mẫu tức thì nhờ bảng π mà không cần đọc lại")
    c.drawString(70, 166, "ký tự text -> Tối ưu hóa triệt để số lượng phép so sánh thực tế.")
    
    draw_card(c, 485, 60, 430, 400, "ĐÁNH GIÁ GIỚI HẠN NGHIÊN CỨU (THREATS TO VALIDITY)", accent_color=HexColor('#3B82F6'))
    
    threats = [
        ("Internal Validity (Nội tại)", 
         "Biến động do JIT Compilation và Garbage Collection của JVM.\n"
         "-> Kiểm soát: 3 vòng warm-up ép JIT biên dịch sẵn; cấp cố định 2 GB heap (-Xmx2g) để chặn GC thu gom giữa chừng; đo monotonic System.nanoTime()."),
        ("Construct Validity (Khái niệm)", 
         "Tốc độ đọc đĩa vật lý có thể làm méo mó thời gian đo thuật toán.\n"
         "-> Kiểm soát: Tích hợp đường cơ sở lib-read trong bộ nhớ cache; chứng minh I/O chỉ chiếm <5% thời gian, thuật toán chiếm >95% độ trễ."),
        ("External Validity (Bên ngoài)", 
         "Đặc tính log phân phối Zipf có thể khác biệt trên các hệ thống khác.\n"
         "-> Giới hạn: Kết quả này phản ánh đúng log văn bản ASCII có cấu trúc; các luồng nhị phân nén (Protobuf/Avro) sẽ có hành vi khác.")
    ]
    
    for idx, (t_title, t_desc) in enumerate(threats):
        ty = 360 - idx * 115
        c.setFillColor(HexColor('#F8FAFC'))
        c.setStrokeColor(C_BORDER)
        c.roundRect(500, ty, 400, 95, 4, fill=1, stroke=1)
        
        c.setFillColor(C_NAVY)
        c.setFont('Arial-Bold', 10)
        c.drawString(512, ty + 75, t_title)
        
        c.setFillColor(C_TEXT_DARK)
        c.setFont('Arial', 8)
        lines = t_desc.split('\n')
        c.drawString(512, ty + 54, lines[0])
        c.setFillColor(C_BLUE)
        c.drawString(512, ty + 36, lines[1][:80])
        c.drawString(512, ty + 22, lines[1][80:])

    draw_footer(c, 7)
    c.showPage()
    
    # ==========================================
    # SLIDE 8: KẾT LUẬN & KHUYẾN NGHỊ KỸ THUẬT
    # ==========================================
    draw_header(c, "Kết luận Nghiên cứu & Khuyến nghị Kiến trúc Kỹ thuật Thực tế")
    
    draw_card(c, 45, 60, 420, 400, "TỔNG KẾT ĐÓNG GÓP CỦA ĐỒ ÁN", accent_color=C_BLUE)
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_NAVY)
    c.drawString(60, 420, "1. Sản phẩm Kỹ thuật Hoàn chỉnh:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 402, "• Cài đặt độc lập 100% từ đầu 3 thuật toán: Brute Force, KMP, Aho-Corasick")
    c.drawString(70, 386, "(hỗ trợ cả Sparse Failure Link và Full DFA State Table) trên JDK 8.")
    c.drawString(60, 368, "• Kiến trúc Streaming Block đệm B = 1 MiB đọc file vô hạn không tràn RAM.")
    c.drawString(60, 350, "• Bộ Invariant Checksum 64-bit tự động kiểm tra tính đúng đắn 100%.")
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_NAVY)
    c.drawString(60, 318, "2. Bộ Thực nghiệm & Báo cáo Chuẩn IEEE:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 300, "• Bộ đo tự động Runner xuất CSV chuẩn 22 cột dữ liệu, tái lập 100%.")
    c.drawString(60, 282, "• Kịch bản vẽ 4 biểu đồ 300 DPI nhúng trực tiếp vào Paper hội nghị IEEE.")
    c.drawString(60, 264, "• Khai báo sử dụng AI minh bạch theo chuẩn xuất bản quốc tế.")
    
    c.setFont('Arial-Bold', 10.5)
    c.setFillColor(C_GREEN)
    c.drawString(60, 232, "3. Thành quả Năng lực Sinh viên:")
    c.setFont('Arial', 8.5)
    c.setFillColor(C_TEXT_DARK)
    c.drawString(60, 214, "• Cả 6 thành viên đều tự tay cài đặt, kiểm thử và nắm vững chuyên sâu")
    c.drawString(70, 198, "phần việc của cặp mình, sẵn sàng trả lời vấn đáp cá nhân 1-1.")
    c.drawString(60, 180, "• Cơ chế phản biện chéo (cross-review) giúp mã nguồn sạch và tin cậy.")
    
    draw_card(c, 485, 60, 430, 400, "KHUYẾN NGHỊ KIẾN TRÚC KỸ THUẬT (ENGINEERING GUIDELINES)", accent_color=C_GREEN)
    
    guidelines = [
        ("1. Tìm kiếm Đơn mẫu (k = 1) trên Log có cấu trúc", 
         "-> KHUYẾN NGHỊ: Chọn KMP.\n"
         "Lý do: Đạt thông lượng cao hơn 25–51% so với Brute Force nhờ loại bỏ quay lui; miễn nhiễm hoàn toàn với các mẫu đối kháng lặp tiền tố.", C_BLUE),
        ("2. Hệ thống Nhiệm vụ Trọng yếu & Chống Tấn công DoS", 
         "-> BẮT BUỘC: Sử dụng KMP.\n"
         "Lý do: KMP đảm bảo chặn trên O(n) bất biến; Brute Force có thể sụp đổ 165 lần (xuống 2.72 MiB/s), gây treo ứng dụng (Algorithmic Complexity Attack).", C_RED),
        ("3. Giám sát An ninh SIEM & Quét Đa mẫu (k ≥ 10)", 
         "-> BẮT BUỘC: Sử dụng Aho–Corasick.\n"
         "Lý do: k-KMP sụp đổ tuyến tính (chậm 13x ở k=10, mất 28s ở k=100); Aho-Corasick quét 1 lượt duy nhất, đạt tốc độ gấp 57.5 lần k-KMP.", HexColor('#F59E0B')),
        ("4. Lựa chọn giữa AC-Fail và AC-DFA", 
         "• Thiết bị IoT / Embedded / RAM hạn chế (<64MB): Chọn AC-Fail.\n"
         "• Cloud Log Ingestion / Yêu cầu độ trễ thấp: Chọn AC-DFA để đạt tốc độ O(1) ổn định tuyệt đối (zero branch jitter).", HexColor('#8B5CF6'))
    ]
    
    for idx, (g_title, g_desc, col) in enumerate(guidelines):
        gy = 370 - idx * 85
        c.setFillColor(HexColor('#F8FAFC'))
        c.setStrokeColor(C_BORDER)
        c.roundRect(500, gy, 400, 75, 4, fill=1, stroke=1)
        
        c.setFillColor(col)
        c.setFont('Arial-Bold', 9)
        c.drawString(512, gy + 58, g_title)
        
        c.setFillColor(C_TEXT_DARK)
        c.setFont('Arial', 7.5)
        lines = g_desc.split('\n')
        c.drawString(512, gy + 40, lines[0])
        if len(lines) > 1:
            c.drawString(512, gy + 24, lines[1])

    draw_footer(c, 8)
    c.showPage()
    
    c.save()
    print(f"-> [THÀNH CÔNG] Đã lưu file PDF: {PDF_OUT}")

def build_pptx_slides():
    print("-> Đang khởi tạo file PowerPoint PPTX với python-pptx...")
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]
    
    def add_slide_with_header(title, category="CSD201 — ĐỒ ÁN NHÓM: PATTERN MATCHING"):
        slide = prs.slides.add_slide(blank_layout)
        
        header = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(1.1))
        header.fill.solid()
        header.fill.fore_color.rgb = RGBColor(15, 23, 42)
        header.line.fill.background()
        
        txBox = slide.shapes.add_textbox(Inches(0.6), Inches(0.12), Inches(12.0), Inches(0.85))
        tf = txBox.text_frame
        tf.word_wrap = True
        p1 = tf.paragraphs[0]
        p1.text = category.upper()
        p1.font.name = 'Arial'
        p1.font.size = Pt(11)
        p1.font.bold = True
        p1.font.color.rgb = RGBColor(96, 165, 250)
        
        p2 = tf.add_paragraph()
        p2.text = title
        p2.font.name = 'Arial'
        p2.font.size = Pt(22)
        p2.font.bold = True
        p2.font.color.rgb = RGBColor(255, 255, 255)
        
        footer = slide.shapes.add_textbox(Inches(0.6), Inches(7.0), Inches(12.0), Inches(0.4))
        ftf = footer.text_frame
        fp = ftf.paragraphs[0]
        fp.text = "FPT University Quy Nhon — SE20C — Nhóm 4: Brute Force, KMP & Aho-Corasick"
        fp.font.name = 'Arial'
        fp.font.size = Pt(10)
        fp.font.color.rgb = RGBColor(100, 116, 139)
        
        return slide

    # Slide 1: Cover
    s1 = prs.slides.add_slide(blank_layout)
    bg1 = s1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    bg1.fill.solid()
    bg1.fill.fore_color.rgb = RGBColor(15, 23, 42)
    bg1.line.fill.background()
    
    t_box = s1.shapes.add_textbox(Inches(0.8), Inches(1.0), Inches(11.7), Inches(3.0))
    ttf = t_box.text_frame
    p = ttf.paragraphs[0]
    p.text = "CSD201 — DATA STRUCTURES & ALGORITHMS (JAVA) | SE20C"
    p.font.name = 'Arial'
    p.font.size = Pt(14)
    p.font.bold = True
    p.font.color.rgb = RGBColor(96, 165, 250)
    
    p2 = ttf.add_paragraph()
    p2.text = "ĐÁNH GIÁ THỰC NGHIỆM HIỆU NĂNG THUẬT TOÁN\nTÌM MẪU CHUỖI TRONG LOG HỆ THỐNG"
    p2.font.name = 'Arial'
    p2.font.size = Pt(32)
    p2.font.bold = True
    p2.font.color.rgb = RGBColor(255, 255, 255)
    
    p3 = ttf.add_paragraph()
    p3.text = "Empirical Performance Evaluation of Brute Force, KMP, and Aho-Corasick on Large-Scale Server Logs"
    p3.font.name = 'Arial'
    p3.font.size = Pt(16)
    p3.font.italic = True
    p3.font.color.rgb = RGBColor(148, 163, 184)
    
    pairs = [
        ("CẶP 1: RQ1 & INFRA", "Đinh Vũ Phương Khánh (Leader)\nHà Văn Sang"),
        ("CẶP 2: RQ2 & KMP LEAD", "Nguyễn Hoài Nhi (KMP Lead)\nNguyễn Đặng Trường Hải"),
        ("CẶP 3: RQ3 & AHO-CORASICK", "Lương Triệu Vỹ (AC Lead)\nPhan Ngọc Bảo Trâm (Paper Lead)")
    ]
    for idx, (p_title, mems) in enumerate(pairs):
        bx = Inches(0.8 + idx * 3.95)
        card = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, bx, Inches(4.3), Inches(3.7), Inches(2.2))
        card.fill.solid()
        card.fill.fore_color.rgb = RGBColor(30, 41, 59)
        card.line.color.rgb = RGBColor(51, 65, 85)
        ctf = card.text_frame
        ctf.word_wrap = True
        cp1 = ctf.paragraphs[0]
        cp1.text = p_title
        cp1.font.name = 'Arial'
        cp1.font.bold = True
        cp1.font.size = Pt(13)
        cp1.font.color.rgb = RGBColor(96, 165, 250)
        cp2 = ctf.add_paragraph()
        cp2.text = mems
        cp2.font.name = 'Arial'
        cp2.font.size = Pt(12)
        cp2.font.color.rgb = RGBColor(241, 245, 249)

    # Slide 2: RQs
    s2 = add_slide_with_header("Bối cảnh Thực tế & 3 Câu hỏi Nghiên cứu (Research Questions)")
    tb2 = s2.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(11.7), Inches(5.2))
    tf2 = tb2.text_frame
    tf2.word_wrap = True
    
    p = tf2.paragraphs[0]
    p.text = "BỐI CẢNH & ĐỘNG LỰC NGHIÊN CỨU:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(18)
    p.font.color.rgb = RGBColor(15, 41, 66)
    
    p = tf2.add_paragraph()
    p.text = "• Hệ thống cloud/microservices phát sinh hàng chục GiB log mỗi giờ. Việc lọc tìm mã lỗi (Error Signatures) đòi hỏi thông lượng cực cao trong điều kiện bộ nhớ heap bị giới hạn."
    p.font.name = 'Arial'
    p.font.size = Pt(14)
    
    p = tf2.add_paragraph()
    p.text = "\n3 CÂU HỎI NGHIÊN CỨU TRỌNG TÂM (RQS):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(18)
    p.font.color.rgb = RGBColor(15, 41, 66)
    
    p = tf2.add_paragraph()
    p.text = "1. RQ1: Brute Force vs. KMP khi quy mô file log tăng từ 16 MiB đến 256 MiB trên dữ liệu thật T1?\n" \
             "    -> Giả thuyết: Trong log có cấu trúc lặp, KMP tránh quay lui con trỏ nên đạt hiệu năng cao hơn Brute Force."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p.font.color.rgb = RGBColor(37, 99, 235)
    
    p = tf2.add_paragraph()
    p.text = "2. RQ2: Ảnh hưởng của bảng chữ cái và tập đối kháng bệnh lý T4 (a^(N-1)b với mẫu a^(m-1)b)?\n" \
             "    -> Giả thuyết: Brute Force sụp đổ O(n*m) khi m tăng; KMP giữ vững chặn trên tuyến tính O(n)."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p.font.color.rgb = RGBColor(220, 38, 38)
    
    p = tf2.add_paragraph()
    p.text = "3. RQ3: Tìm kiếm đa mẫu đồng thời k = 1..100 giữa Aho-Corasick và lặp k-KMP?\n" \
             "    -> Giả thuyết: k-KMP suy giảm nghiêm trọng O(k*n); Aho-Corasick quét 1 lượt duy nhất; AC-DFA đạt O(1) nhưng tốn thêm RAM."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p.font.color.rgb = RGBColor(16, 185, 129)

    # Slide 3: Architecture
    s3 = add_slide_with_header("Kiến trúc Streaming Block Buffer & Giao thức Đo lường")
    tb3 = s3.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(11.7), Inches(5.2))
    tf3 = tb3.text_frame
    tf3.word_wrap = True
    
    p = tf3.paragraphs[0]
    p.text = "KIẾN TRÚC STREAMING BLOCK & DUY TRÌ TÍNH LIÊN TỤC:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(18)
    p.font.color.rgb = RGBColor(15, 41, 66)
    
    p = tf3.add_paragraph()
    p.text = "• Block-buffered I/O: Đọc text theo từng khối cố định B = 1 MiB, xử lý file vô hạn không bao giờ tràn bộ nhớ RAM.\n" \
             "• Brute Force: Giữ đệm (m - 1) byte ở cuối block trước nối vào đầu block sau để không bỏ sót mẫu vắt qua ranh giới.\n" \
             "• KMP & Aho-Corasick: Duy trì liên tục con trỏ trạng thái (currState và globalOffset) xuyên suốt qua ranh giới các block."
    p.font.name = 'Arial'
    p.font.size = Pt(14)
    
    p = tf3.add_paragraph()
    p.text = "\nBỘ KIỂM ĐỊNH INVARIANT ORACLE (ChecksumConsumer) & GIAO THỨC ĐO:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(18)
    p.font.color.rgb = RGBColor(15, 41, 66)
    
    p = tf3.add_paragraph()
    p.text = "• Đếm khớp: Match Count = ∑ 1  (i = 1..z)\n" \
             "• Mã kiểm tra 64-bit: Checksum = ∑ ((offset_i + 1) * 1000003 + patternId_i) (mod 2^64)\n" \
             "• Đo lập trình độc lập: Tiến trình JVM con cô lập (-Xmx2g), 3 vòng warm-up, 5 vòng đo thực tế (System.nanoTime()), đo đỉnh heap qua JMX MemoryPoolMXBean."
    p.font.name = 'Arial'
    p.font.size = Pt(14)

    # Slide 4: RQ1
    s4 = add_slide_with_header("RQ1: Kết quả Thực nghiệm trên Log thật T1 (Brute Force vs. KMP)")
    tb4 = s4.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(6.0), Inches(5.2))
    tf4 = tb4.text_frame
    tf4.word_wrap = True
    p = tf4.paragraphs[0]
    p.text = "SỐ LIỆU ĐO ĐẠC THỰC TẾ TRÊN T1:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf4.add_paragraph()
    p.text = "• 16 MiB: KMP (292.4 MiB/s) nhanh hơn BF (200.9 MiB/s) 1.46×.\n" \
             "• 64 MiB: KMP (354.2 MiB/s) nhanh hơn BF (234.5 MiB/s) 1.51×.\n" \
             "• 256 MiB: KMP (153.0 MiB/s) nhanh hơn BF (122.5 MiB/s) 1.25×.\n" \
             "• Baseline lib-read đạt >4,289 MiB/s (I/O chỉ chiếm <5% thời gian)."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p = tf4.add_paragraph()
    p.text = "\nBẢN CHẤT KHOA HỌC:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf4.add_paragraph()
    p.text = "• Log server có cấu trúc (timestamp, thread tag, log level lặp lại) thường xuyên kích hoạt so khớp một phần.\n" \
             "• Khi sai khớp ở đuôi mẫu, Brute Force bị phạt nặng do lùi con trỏ (backtracking) về i+1.\n" \
             "• KMP tra cứu bảng π trượt mẫu tiến lên không lùi text, đảm bảo O(n) thực tế tối ưu."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    
    fig1 = os.path.join(FIG_DIR, 'fig3_line_rq1_scaling.png')
    if os.path.exists(fig1):
        s4.shapes.add_picture(fig1, Inches(7.0), Inches(1.5), width=Inches(5.7))

    # Slide 5: RQ2
    s5 = add_slide_with_header("RQ2: Ảnh hưởng của Bảng chữ cái & Sự sụp đổ của Brute Force trên T4")
    tb5 = s5.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(6.0), Inches(5.2))
    tf5 = tb5.text_frame
    tf5.word_wrap = True
    p = tf5.paragraphs[0]
    p.text = "TẬP NGẪU NHIÊN T3 (σ = 256):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf5.add_paragraph()
    p.text = "• Xác suất trùng ký tự đầu chỉ 0.39% -> Mismatch xảy ra ngay lập tức sau 1.004 phép so sánh.\n" \
             "• KMP đạt 829.9 MiB/s; Brute Force đạt 486.4 MiB/s."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p = tf5.add_paragraph()
    p.text = "\nTẬP ĐỐI KHÁNG T4 (a^(N-1)b, N = 64 MiB):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(220, 38, 38)
    p = tf5.add_paragraph()
    p.text = "• KMP bảo vệ hệ thống: Giữ vững 448.6 – 467.6 MiB/s bất chấp m tăng từ 8 lên 1024 (chứng minh O(n)).\n" \
             "• Brute Force sụp đổ O(n*m):\n" \
             "  - m = 8: 208.36 MiB/s (315.7 ms)\n" \
             "  - m = 64: 42.26 MiB/s (1,727.6 ms)\n" \
             "  - m = 1024: 2.72 MiB/s (25,297.8 ms ≈ 25.3 giây!)\n" \
             "=> Chậm hơn KMP 165 lần! Nguy cơ bị tấn công cạn kiệt CPU (Algorithmic DoS)."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    
    fig2 = os.path.join(FIG_DIR, 'fig2_bar_throughput_comparison.png')
    if os.path.exists(fig2):
        s5.shapes.add_picture(fig2, Inches(7.0), Inches(1.5), width=Inches(5.7))

    # Slide 6: RQ3
    s6 = add_slide_with_header("RQ3: Đa mẫu Aho–Corasick vs. Multi-KMP & Đánh đổi Bộ nhớ")
    tb6 = s6.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(5.8), Inches(5.2))
    tf6 = tb6.text_frame
    tf6.word_wrap = True
    p = tf6.paragraphs[0]
    p.text = "SỰ SUY THOÁI CỦA k-KMP:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf6.add_paragraph()
    p.text = "• k = 1: 192.40 MiB/s (336.7 ms)\n" \
             "• k = 10: 14.76 MiB/s (4,411.1 ms — chậm 13.1×)\n" \
             "• k = 100: 2.41 MiB/s (28,031.8 ms ≈ 28.03 giây!)\n" \
             "=> Minh chứng O(k*n) không thể mở rộng."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    p = tf6.add_paragraph()
    p.text = "\nSỨC MẠNH CỦA AHO-CORASICK (k = 100):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(16, 185, 129)
    p = tf6.add_paragraph()
    p.text = "• Quét 1 lượt duy nhất qua Trie Automaton:\n" \
             "  - AC-DFA: 138.95 MiB/s (487.4 ms) — Nhanh hơn 57.5×!\n" \
             "  - AC-Fail: 124.15 MiB/s (545.3 ms) — Nhanh hơn 51.4×!\n" \
             "• Đánh đổi RAM: k-KMP tốn 66.8 MB (19.7%), AC-DFA tốn 129.0 MB (38.0%), AC-Fail tốn 143.6 MB (42.3%).\n" \
             "• Bảng DFA dfa[state][byte] chuyển O(1) giúp AC-DFA nhanh hơn AC-Fail ~12% thông lượng."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    
    fig3 = os.path.join(FIG_DIR, 'fig4_line_rq3_multi_pattern.png')
    if os.path.exists(fig3):
        s6.shapes.add_picture(fig3, Inches(6.8), Inches(1.5), width=Inches(3.3))
    fig4 = os.path.join(FIG_DIR, 'fig1_pie_memory_comparison.png')
    if os.path.exists(fig4):
        s6.shapes.add_picture(fig4, Inches(10.0), Inches(1.5), width=Inches(2.8))

    # Slide 7: Counter-Intuitive & Threats
    s7 = add_slide_with_header("Kết quả Trái Dự đoán & Đánh giá Giới hạn (Threats to Validity)")
    tb7 = s7.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(11.7), Inches(5.2))
    tf7 = tb7.text_frame
    tf7.word_wrap = True
    p = tf7.paragraphs[0]
    p.text = "KẾT QUẢ THỰC NGHIỆM TRÁI DỰ ĐOÁN BAN ĐẦU:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(236, 72, 153)
    p = tf7.add_paragraph()
    p.text = "• Dự đoán ban đầu: Trên log thông thường, Brute Force sẽ nhanh hơn KMP do code gọn, tận dụng cache CPU và không tốn bảng π.\n" \
             "• Thực tế đo được: KMP lại thắng Brute Force từ 25% đến 51% trên tập log thật T1!\n" \
             "• Lý do khoa học: Dữ liệu log thực tế chứa nhiều cụm tiền tố lặp lại (timestamp, log level [INFO]). Brute Force liên tục bị phạt lùi con trỏ (backtracking) khi khớp hụt ở đuôi mẫu, trong khi KMP trượt mẫu tiến lên ngay lập tức nhờ bảng π."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    
    p = tf7.add_paragraph()
    p.text = "\nĐÁNH GIÁ GIỚI HẠN NGHIÊN CỨU (THREATS TO VALIDITY):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf7.add_paragraph()
    p.text = "• Internal Validity: Nhiễu do JIT & GC của JVM -> Đã triệt tiêu bằng 3 vòng warm-up, cố định 2GB heap (-Xmx2g), đo System.nanoTime().\n" \
             "• Construct Validity: Độ trễ đọc đĩa -> Kiểm soát bằng baseline lib-read (chứng minh I/O chỉ chiếm <5% thời gian).\n" \
             "• External Validity: Phân phối log ASCII có cấu trúc khác với các định dạng nhị phân nén (Protobuf/Avro)."
    p.font.name = 'Arial'
    p.font.size = Pt(13)

    # Slide 8: Conclusion & Guidelines
    s8 = add_slide_with_header("Kết luận Nghiên cứu & Khuyến nghị Kiến trúc Kỹ thuật")
    tb8 = s8.shapes.add_textbox(Inches(0.8), Inches(1.4), Inches(11.7), Inches(5.2))
    tf8 = tb8.text_frame
    tf8.word_wrap = True
    p = tf8.paragraphs[0]
    p.text = "TỔNG KẾT ĐÓNG GÓP CỦA ĐỒ ÁN:"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(15, 41, 66)
    p = tf8.add_paragraph()
    p.text = "1. Cài đặt from scratch 100% 3 thuật toán trên Java 8 với kiến trúc Streaming Block B = 1 MiB đọc file vô hạn.\n" \
             "2. Thiết kế bộ đo Runner chuẩn khoa học, tự động hóa toàn diện với Oracle Checksum 64-bit và tái lập 100%."
    p.font.name = 'Arial'
    p.font.size = Pt(13)
    
    p = tf8.add_paragraph()
    p.text = "\nKHUYẾN NGHỊ KIẾN TRÚC KỸ THUẬT (ENGINEERING GUIDELINES):"
    p.font.name = 'Arial'
    p.font.bold = True
    p.font.size = Pt(16)
    p.font.color.rgb = RGBColor(16, 185, 129)
    p = tf8.add_paragraph()
    p.text = "🔹 Tìm kiếm Đơn mẫu (k = 1) trên Log có cấu trúc: Chọn KMP để đạt thông lượng cao hơn 25–51% và tránh bị phạt lùi.\n" \
             "🔹 Hệ thống Nhiệm vụ Trọng yếu & Chống Tấn công DoS: Bắt buộc chọn KMP để bảo đảm chặn trên O(n) bất biến.\n" \
             "🔹 Giám sát An ninh SIEM & Quét Đa mẫu (k ≥ 10): Bắt buộc dùng Aho–Corasick (nhanh hơn tới 57.5 lần so với k-KMP).\n" \
             "🔹 Lựa chọn AC-Fail hay AC-DFA?\n" \
             "    • Thiết bị IoT / Embedded / RAM hẹp: Chọn AC-Fail để tiết kiệm dung lượng bảng trạng thái.\n" \
             "    • Cloud Ingestion / Yêu cầu độ trễ thấp: Chọn AC-DFA để đạt tốc độ chuyển trạng thái O(1) tuyệt đối không giật lag."
    p.font.name = 'Arial'
    p.font.size = Pt(13)

    prs.save(PPTX_OUT)
    print(f"-> [THÀNH CÔNG] Đã lưu file PowerPoint: {PPTX_OUT}")

if __name__ == '__main__':
    build_pdf_slides()
    build_pptx_slides()
    print("\n>>> HOÀN THÀNH ƯU TIÊN 3: ĐÃ XUẤT ĐẦY ĐỦ SLIDES.PDF VÀ SLIDES.PPTX! <<<")
