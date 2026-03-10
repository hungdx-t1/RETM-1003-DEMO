package tma.example.be.fil1;

import tma.example.be.fil1.interfaces.MathOperation;

@SuppressWarnings({"Convert2MethodRef"})
public class AppRun {
    public static void main(String... args) {
        // định nghĩa phép cộng và trừ thông qua lambda
        MathOperation addition = (a, b) -> a + b;
        MathOperation subtraction = (a, b) -> a - b;

        // test
        System.out.println("10 + 5 = " + addition.calculate(10, 5)); // 5
        System.out.println("10 - 5 = " + subtraction.calculate(10, 5)); // 15
    }
}
