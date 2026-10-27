package algorithm;

public class ChecksumConsumer {
  private long checksum;
  private long count;
  //Ta dùng kiểu long thay vì int - Vì file log lên đến 1GB, nếu dùng int thì khi sau này mở rộng file log lên 2GB thì sẽ bị tràn số, gây sai lệch kết quả
  //Dùng long tránh hiện tượng hash_collision, 2 kết quả kahcs nhau lại có cùng 1 checksum
      public ChecksumConsumer() {
          this.checksum = 0;
          this.count = 0;
      }
      
      //QUAN TRỌNG: Hàm được gọi mỗi khi tìm thấy 1 mẫu trong log, tính toán checksum và số lượng mẫu tìm được
      // Chống JIT Compiler xóa code vì phải chạy thuật toán để tính toán giá trị checksum và count
      public void onMatch(long offSet, int patternId) {
          //offset: Vị trí byte bắt đầu tìm thấy từ trong file log (từ 0 đến n-1).
          //patternId: Mã số thứ tự của từ vừa tìm thấy (ví dụ: tìm thấy từ thứ 0, từ thứ 1,... trong danh sách mẫu).
          checksum = checksum + ( offSet + 1 ) * 1000003L + patternId;
          count++;
      }

      public long getChecksum() {
        return checksum;
      }

      public long getCount() {
        return count;
      }

      public void reset() {
        this.count = 0;
        this.checksum = 0;
      }

}