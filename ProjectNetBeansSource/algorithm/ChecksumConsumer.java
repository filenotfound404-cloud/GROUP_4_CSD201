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
      //Công thức chuẩn theo Mục 3.3 Đề tài: (vị trí bắt đầu + 1) * 1000003 + mã mẫu, dùng phép cộng long cho phép tràn.
      public void onMatch(long offSet, int patternId) {
          //offSet: Vị trí byte bắt đầu tìm thấy từ trong file log (từ 0 đến n-1).
          //patternId: Mã số thứ tự của từ vừa tìm thấy. Khi tìm 1 mẫu thì mã mẫu bằng 0.
          checksum += (offSet + 1) * 1000003L + patternId;
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