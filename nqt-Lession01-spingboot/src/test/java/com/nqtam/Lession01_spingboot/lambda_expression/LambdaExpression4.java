package com.nqtam.Lession01_spingboot.lambda_expression;
import java.util.Arrays;
import java.util.List;

public class LambdaExpression4 {
    public static void main(String[] args) {

        List<String> list = Arrays.asList("Java SpringBoot", "C# NetCore", "PHP", "Javascript");

        // // Using Lambda expression
        // Cú pháp: item -> câu_lệnh
        list.forEach(System.out::println);

        System.out.println("================");

        // // Using Method Reference (cách viết rút gọn của Lambda)
        // Cú pháp: ClassName::methodName
        list.forEach(System.out::println);
    }
}