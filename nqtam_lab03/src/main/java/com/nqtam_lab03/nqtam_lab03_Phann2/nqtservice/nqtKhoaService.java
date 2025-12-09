package com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtKhoa;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors; // Cần thiết cho phương thức delete

@Service
public class nqtKhoaService {
    List<nqtKhoa> khoas = new ArrayList<>();

    // Hàm tạo để khởi tạo dữ liệu mẫu
    public nqtKhoaService() {
        // Tạo 5 phần tử mẫu (Giả định Constructor có 2 tham số: makh, tenkh)
        khoas.addAll(Arrays.asList(
                new nqtKhoa("K01", "Công nghệ Thông tin"),
                new nqtKhoa("K02", "Kinh tế"),
                new nqtKhoa("K03", "Ngoại ngữ"),
                new nqtKhoa("K04", "Thiết kế Đồ họa"),
                new nqtKhoa("K05", "Quản trị Khách sạn")
        ));
    }

    // 1. Lấy toàn bộ danh sách
    public List<nqtKhoa> getKhoas() {
        return khoas;
    }

    // 2. Lấy danh sách theo makh
    public nqtKhoa getKhoa(String makh) {
        return khoas.stream()
                .filter(khoa -> khoa.getMakh().equals(makh))
                .findFirst().orElse(null);
    }

    // 3. Thêm mới một khoa
    public nqtKhoa addKhoa(nqtKhoa khoa) {
        // Logic kiểm tra trùng makh có thể được thêm vào đây
        khoas.add(khoa);
        return khoa;
    }

    // 4. Sửa đổi thông tin khoa theo mã
    public nqtKhoa updateKhoa(String makh, nqtKhoa khoaMoi) {
        nqtKhoa check = getKhoa(makh);
        if (check == null) {
            return null; // Không tìm thấy khoa để sửa
        }

        khoas.forEach(item -> {
            if (item.getMakh().equals(makh)) {
                item.setTenkh(khoaMoi.getTenkh());
            }
        });
        // Trả về đối tượng mới đã được cập nhật
        return getKhoa(makh);
    }

    // 5. Xóa thông tin khoa theo mã
    public boolean deleteKhoa(String makh) {
        nqtKhoa check = getKhoa(makh);
        if (check == null) {
            return false; // Không tìm thấy khoa để xóa
        }
        return khoas.remove(check);
    }
}