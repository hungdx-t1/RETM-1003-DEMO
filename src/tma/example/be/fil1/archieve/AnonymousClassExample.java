package tma.example.be.fil1.archieve;

@SuppressWarnings("Convert2Lambda")
public class AnonymousClassExample {
    public static void main(String[] args) {
        // Java 7- Quá nhiều chữ thừa thãi
        Runnable oldWay = new Runnable() {
            @Override
            public void run() {
                System.out.println("Xin chào từ Anonymous Class!");
            }
        };

        // ✅ Java 8+ Chỉ còn đúng 1 dòng
        Runnable newWay = () -> System.out.println("Xin chào từ Lambda!");

        // Gọi thực thi
        oldWay.run();
        newWay.run();
    }
}
