package com.nqtam.Lession01_spingboot.method_ref;
import java.util.Arrays;
import java.util.List;


public class ForEachListExample {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java Spring", "C#", "NetCore API", "PHP Laravel", "Javascript");

        // Sử dụng biểu thức Lambda
        System.out.println("Sử dụng biểu thức Lambda: ");
        languages.forEach(lang -> System.out.println(lang));

        // Sử dụng Method Reference (rút gọn)
        System.out.println("Sử dụng method reference: ");
        languages.forEach(System.out::println);
    }
}