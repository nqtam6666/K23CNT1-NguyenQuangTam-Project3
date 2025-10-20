package com.nqtam.Lession01_spingboot.lambda_expression;
// Giao diện chức năng (Functional Interface)
@FunctionalInterface
interface SayHello2 {
    // Phương thức có một tham số String
    void sayHello(String name);
}
public class LambdaExpression2 {
    public static void main(String[] args) {

    // // Lambda sử dụng 1 tham số (có dấu ngoặc đơn)
    // Cú pháp: (tham_số) -> { logic }
    SayHello2 say1 = (name) -> {
        System.out.println("Hello " + name);
    };
    say1.sayHello("Devmaster");

    // // Ngắn gọn: Bỏ dấu ngoặc đơn bao quanh tham số (chỉ áp dụng khi có 1 tham số)
    // Cú pháp: tham_số -> { logic }
    SayHello2 say2 = name -> {
        System.out.println("Hello " + name);
    };
    say2.sayHello("Devmaster");

    // // Ngắn gọn hơn: Bỏ dấu ngoặc nhọn và từ khóa return (chỉ áp dụng khi logic là một câu lệnh duy nhất)
    // Cú pháp: tham_số -> logic_một_dòng
    SayHello2 say3 = name -> System.out.println("Hello " + name);
    say3.sayHello("Devmaster");
}
}
