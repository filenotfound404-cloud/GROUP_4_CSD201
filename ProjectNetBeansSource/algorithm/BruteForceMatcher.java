package algorithm;
import java.util.List;

public class BruteForceMatcher implements PatternMatching { 
    private List<byte[]> patterns;

    @Override
    public void compilePattern(List<byte[]> patterns) {
        this.patterns = patterns;
    }

    @Override
    public void searchPattern(byte[] data, ChecksumConsumer consumer) {
        //Vòng lặp đầu tiên: Chạy từ mảng mẫu(pattern) byte, đây là số lượng các mẫu được lưu trong 1 mảng
        for (int patternId = 0; patternId < patterns.size(); patternId++) {
            byte[] pattern = patterns.get(patternId); //Mỗi phần tử mẫu trong mảng ta sẽ lấy ra 1 phần từ và lưu từng kí tự byte của chúng vào trong mảng

            //Vòng lặp 2: Chạy từ offset - mảng dữ liệu, đến độ dài cảu mảng dữ liệu - độ dài của mảng mẫu ta vừa lưu ở trên + 1 
            for (int offset = 0; offset < data.length - pattern.length + 1; offset++) {
                boolean matched = true; // giả định đã tìm thấy mẫu có trong dữ liệu -- data

                //Vòng lặp 3: đây là độ dài của mảng mẫu ta đã lưu ở trong vòng lặp đầu tiên 
                for (int i = 0; i < pattern.length ; i++) {
                    //Bắt đầu so khớp thử giữa data và pattern 
                    //nếu có kí tự nào khác giả định ban đầu là sai và phá vòng lặp, chạy lại vòng lặp số 2
                    if(data[offset + i] != pattern[i]) {
                        matched = false;
                        break;
                    }
                }
                
                //Nếu không có phần tử nào khác thì giả định ban đầu là đúng và gọi tới hàm checkSum để cho ra kết quả.
                if (matched) {
                    consumer.onMatch(offset, patternId);
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "BruteForce";
    }

    @Override
    public void reset() {

    }
}
