package com.nqtam.Lession01_spingboot.lambda_expression;
// Functional Interface trả về một giá trị
@FunctionalInterface
interface Calculator1 {
    int add(int a, int b);
}

// Functional Interface không trả về giá trị (void)
@FunctionalInterface
interface Calculator2 {
    void add(int a, int b);
}

public class LambdaExpression3 {
    public static void main(String[] args) {

        // Lambda cho Calculator1: Có kiểu dữ liệu tường minh cho tham số
        // Cú pháp: (kiểu_dữ_liệu a, kiểu_dữ_liệu b) -> biểu_thức_trả_về
        Calculator1 calc1 = (int a, int b) -> (a + b);
        System.out.println(calc1.add(11, 12)); // Output: 23

        // Lambda cho Calculator1: Không có kiểu dữ liệu (Java có thể suy luận)
        // Cú pháp: (a, b) -> biểu_thức_trả_về
        Calculator1 calc2 = (a, b) -> (a + b);
        System.out.println(calc2.add(21, 22)); // Output: 43

        // Lambda cho Calculator2 (void): Cú pháp ngắn gọn một dòng
        // Cú pháp: (a, b) -> câu_lệnh
        Calculator2 calc3 = (a, b) -> System.out.println(a + b);
        calc3.add(31, 32); // Output: 63

        // Lambda cho Calculator2 (void): Cú pháp khối lệnh
        // Cú pháp: (a, b) -> { khối_lệnh }
        Calculator2 calc4 = (a, b) -> {
            int sum = a + b;
            System.out.println(a + "+" + b + "=" + sum);
        };
        calc4.add(41, 42); // Output: 41+42=83
    }
}