package com.nqtam.Lession01_spingboot.pkg_stream_api;

import java.util.Arrays;
import java.util.List;

public class StreamExample {

    List<Integer> integerList = Arrays.asList(11, 22, 55, 33, 44, 66);

    // Phương thức KHÔNG dùng Stream
    public void withoutStream() {
        int count = 0;
        for (Integer integer : integerList) {
            if (integer % 2 == 0) {
                count++;
            }
        }
        System.out.println("WithoutStream -> Số phần tử chẵn: " + count); // Output: 3
    }

    // Phương thức DÙNG Stream
    public void withStream() {
        // Sử dụng Stream: filter(Lambda) để lọc, sau đó count() để đếm
        long count = integerList.stream()
                .filter(num -> num % 2 == 0)
                .count();
        System.out.println("WithStream-> Số phần tử chẵn: " + count); // Output: 3
    }

    public static void main(String[] args) {
        StreamExample streamExample = new StreamExample();
        streamExample.withoutStream();
        streamExample.withStream();
    }
}