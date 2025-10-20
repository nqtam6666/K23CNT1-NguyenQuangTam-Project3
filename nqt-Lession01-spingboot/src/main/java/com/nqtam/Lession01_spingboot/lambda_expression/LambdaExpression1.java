package com.nqtam.Lession01_spingboot.lambda_expression;

// Giao diện chức năng (Functional Interface)
@FunctionalInterface
interface SayHello1 {
    void sayHello();
}
public class LambdaExpression1 {
    public static void main(String[] args) {
    // Khai báo và gán lambda expression cho SayHello1
    // Cú pháp: () -> { logic }
    SayHello1 sayHello = () -> {
        System.out.println("Hello World");
    };

    // Gọi phương thức thông qua biến interface
    sayHello.sayHello();
}
}
