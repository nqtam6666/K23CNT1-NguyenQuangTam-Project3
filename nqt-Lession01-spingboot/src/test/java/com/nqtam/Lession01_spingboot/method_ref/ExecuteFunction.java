package com.nqtam.Lession01_spingboot.method_ref;

import java.util.Arrays;

@FunctionalInterface
public interface ExecuteFunction {
    int execute(int a, int b);
}

class MathUtils {

    // Constructor
    public MathUtils() {
        // ...
    }

    // Constructor với tham số
    public MathUtils(String str) {
        System.out.println("MathUtils: " + str);
    }

    // Static Method
    public static int sum(int a, int b) {
        return a + b;
    }

    // Static Method
    public static int minus(int a, int b) {
        return a - b;
    }

    // Instance Method
    public int multiply(int a, int b) {
        return a * b;
    }
}

 class DemoMethodRef {

    // Phương thức trợ giúp để thực thi ExecuteFunction
    public static int doAction(int a, int b, ExecuteFunction func) {
        return func.execute(a, b);
    }

    public static void main(String[] args) {
        int a = 10;
        int b = 20;

        // // Tham chiếu đến Static Method (ClassName::staticMethod)
        int sum = doAction(a, b, MathUtils::sum);
        System.out.println(a + " + " + b + " = " + sum); // Output: 10 + 20 = 30

        int minus = doAction(a, b, MathUtils::minus);
        System.out.println(a + " - " + b + " = " + minus); // Output: 10 - 20 = -10

        // // Tham chiếu đến Instance Method của một đối tượng cụ thể (instance::instanceMethod)
        MathUtils mathUtils = new MathUtils();
        int multiply = doAction(a, b, mathUtils::multiply);
        System.out.println(a + " * " + b + " = " + multiply); // Output: 10 * 20 = 200

        // // Tham chiếu đến Instance Method của một đối tượng tùy ý của một kiểu cụ thể
        String[] stringArray = {"Java", "C++", "PHP", "Javascript"};

        // Sắp xếp mảng String, sử dụng Method Reference cho String::compareToIgnoreCase
        Arrays.sort(stringArray, String::compareToIgnoreCase);

        for (String str : stringArray) {
            System.out.println(str);
        }
        /* Output: C++, Java, Javascript, PHP */
    }
}