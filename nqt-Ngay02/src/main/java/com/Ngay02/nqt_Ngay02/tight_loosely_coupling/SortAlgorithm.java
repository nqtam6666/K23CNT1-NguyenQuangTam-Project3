package com.Ngay02.nqt_Ngay02.tight_loosely_coupling;

import java.util.Arrays;
import java.util.stream.Stream;
// Loosely coupling
interface SortAlgorithm {
    void sort(int[] array);
}
class LooselyBubbleSortAlgorithm implements SortAlgorithm{
    @Override
    public void sort(int[] array) {
        System.out.println("Sorted using bubble sort algorithm");
                Arrays.stream(array).sorted().forEach(System.out::println);
    }
}
