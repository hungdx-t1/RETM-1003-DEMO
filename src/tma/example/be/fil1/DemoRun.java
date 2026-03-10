package tma.example.be.fil1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"Convert2Lambda", "Java8ListSort", "ComparatorCombinators"})
public class DemoRun {
    public static void main(String... args) {
        List<String> names = Arrays.asList("Alexander", "Bob", "Catherine", "Zoe");

        // ❌ Dùng Anonymous Class (Đọc rất nhức mắt)
        Collections.sort(names, new Comparator<>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });

        // ✅ Dùng Lambda (Ngắn gọn, rành mạch)
        // Giải thích: "Cho tôi 2 chuỗi s1 và s2, tôi sẽ trả về kết quả so sánh độ dài của chúng"
        names.sort((s1, s2) -> Integer.compare(s1.length(), s2.length()));

        System.out.println(names); // [Bob, Zoe, Alexander, Catherine]
    }
}
