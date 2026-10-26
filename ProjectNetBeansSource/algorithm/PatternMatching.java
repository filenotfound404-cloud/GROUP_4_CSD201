package algorithm;

import java.util.List;

     //Cần 1 khuôn mẫu chung cho các thuật toán tìm kiếm mẫu
     //Phục vụ cho việc đo lường công bằng, độ chính xác cao trên log lớn (16MB - 1GB)
     //Gồm 3 hàm chính và 1 hàm bổ trợ
      public interface PatternMatching {
      
      //Hàm tiền xử lí mẫu - nhận danh sách các mẫu cần tìm kiếm 
      //phân tích mẫu và tạo ra 1 cấu trúc dữ liệu thích hợp để phục vụ cho việc tìm kiếm mẫu 
      //Dùng cho chỉ số build_ms trong file csv
      public void compilePattern(List<byte[]> patterns);


      //Hàm tìm kiếm mẫu, Nhận dữ liệu log cần quét và trả về danh sách các vị trí tìm thấy mẫu trong log
      public void searchPattern(byte[] data, ChecksumConsumer consumer);


      //Hàm định danh tên thuật toán, trả về tên chuẩn của thuật toán
      //Dùng cho chỉ số algorith_name trong filse csv và để cho phần tự động hóa cho BỘ ĐO CHỈ SỐ TRONG BENCHMARK
      public String getAlgorithmName();

      //Hàm làm mới trạng thái của thuật toán về lại trạng thái ban đầu, để có thể tái sử dụng thuật toán cho các lần Warm-up -> chính thức chạy.
      //Làm sạch trạng thái của thuật toán giữa các lần chạy mà không cần phải new() ra 1 đối tượng mới gây tốn bộ nhớ và thời gian
      public void reset();
}