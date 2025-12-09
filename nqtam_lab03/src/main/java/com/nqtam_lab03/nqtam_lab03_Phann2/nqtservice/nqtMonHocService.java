package com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtMonHoc;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class nqtMonHocService {
    List<nqtMonHoc> monHocs = new ArrayList<>();

    // Hàm tạo để khởi tạo dữ liệu mẫu
    public nqtMonHocService() {
        // Tạo 5 phần tử mẫu (Giả định Constructor có 3 tham số: mamh, tenmh, sotiet)
        monHocs.addAll(Arrays.asList(
                new nqtMonHoc("M01", "Lập trình Java", 45),
                new nqtMonHoc("M02", "Cơ sở Dữ liệu", 30),
                new nqtMonHoc("M03", "Thiết kế Web", 60),
                new nqtMonHoc("M04", "Toán rời rạc", 45),
                new nqtMonHoc("M05", "Mạng máy tính", 30)
        ));
    }

    // 1. Lấy toàn bộ danh sách
    public List<nqtMonHoc> getMonHocs() {
        return monHocs;
    }

    // 2. Lấy danh sách theo mamh (mã môn học)
    public nqtMonHoc getMonHoc(String mamh) {
        return monHocs.stream()
                .filter(mh -> mh.getMamh().equals(mamh))
                .findFirst().orElse(null);
    }

    // 3. Thêm mới một môn học
    public nqtMonHoc addMonHoc(nqtMonHoc monHoc) {
        monHocs.add(monHoc);
        return monHoc;
    }

    // 4. Sửa đổi thông tin môn học theo mã
    public nqtMonHoc updateMonHoc(String mamh, nqtMonHoc monHocMoi) {
        nqtMonHoc check = getMonHoc(mamh);
        if (check == null) {
            return null; // Không tìm thấy môn học
        }

        monHocs.forEach(item -> {
            if (item.getMamh().equals(mamh)) {
                item.setTenmh(monHocMoi.getTenmh());
                item.setSotiet(monHocMoi.getSotiet());
            }
        });
        return getMonHoc(mamh);
    }

    // 5. Xóa thông tin môn học theo mã
    public boolean deleteMonHoc(String mamh) {
        nqtMonHoc check = getMonHoc(mamh);
        if (check == null) {
            return false;
        }
        return monHocs.remove(check);
    }
}