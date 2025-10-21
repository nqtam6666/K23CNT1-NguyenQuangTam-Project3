package com.Ngay02.nqt_Ngay02.tight_loosely_coupling;

import java.util.Arrays;

public class LooseCouplingService {

    // Khai báo phụ thuộc vào Interface, KHÔNG phải Class cụ thể
    private SortAlgorithm sortAlgorithm;

    // 1. Dependency Injection qua Constructor (Phương pháp tốt nhất)
    public LooseCouplingService(SortAlgorithm sortAlgorithm) {
        // Lớp dịch vụ không quan tâm thuật toán cụ thể là gì
        this.sortAlgorithm = sortAlgorithm;
    }

    // 2. Dependency Injection qua Setter (Tùy chọn)
    public void setSortAlgorithm(SortAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    public void complexBusinessSort(int[] arr){
        // Gọi phương thức trên Interface
        sortAlgorithm.sort(arr);
        System.out.println("Kết quả sắp xếp:");
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void main(String[] args) {

        // --- Loose Coupling in Action ---

        // Tạo instance của thuật toán cụ thể (ví dụ: BubbleSort)
        SortAlgorithm bubbleSorter = new BubbleSortAlgorithm();

        // Inject (tiêm) thuật toán vào dịch vụ
        LooseCouplingService lCouplingService = new LooseCouplingService(bubbleSorter);

        System.out.println("--- Chạy Bubble Sort ---");
        lCouplingService.complexBusinessSort(new int[]{11, 21, 13, 42, 15});

        // --- Khả năng mở rộng (Flexibility) ---
        // Nếu sau này bạn có QuickSortAlgorithm implements SortAlgorithm,
        // bạn chỉ cần tạo instance và truyền vào, KHÔNG cần sửa code của LooseCouplingService!
    }
}