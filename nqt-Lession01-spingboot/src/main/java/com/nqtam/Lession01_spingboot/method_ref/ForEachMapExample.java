package com.nqtam.Lession01_spingboot.method_ref;
import java.util.HashMap;
import java.util.Map;

public class ForEachMapExample {
    public static void main(String[] args) {
        Map<Integer, String> hmap = new HashMap<>();
        hmap.put(1, "Java Spring");
        hmap.put(2, "Javascript");
        hmap.put(3, "PHP Laravel");
        hmap.put(4, "C# NetCore");

        // Hiển thị dữ liệu, sử dụng Lambda expression với hai tham số (key, value)
        hmap.forEach((key, value) -> {
            System.out.println(key + " - " + value);
        });
    }
}