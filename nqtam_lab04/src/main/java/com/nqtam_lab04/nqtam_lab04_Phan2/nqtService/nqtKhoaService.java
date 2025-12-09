package com.nqtam_lab04.nqtam_lab04_Phan2.nqtService;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtKhoaDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtKhoa;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class nqtKhoaService {

    // Danh sách khoa được lưu trong bộ nhớ (in-memory)
    List<nqtKhoa> khoaList = new ArrayList<>();

    // Khởi tạo dữ liệu mẫu khi service được tạo
    public nqtKhoaService() {
        khoaList.add(new nqtKhoa("KH01", "Khoa Công nghệ thông tin"));
        khoaList.add(new nqtKhoa("KH02", "Khoa Kinh tế"));
        khoaList.add(new nqtKhoa("KH03", "Khoa Ngoại ngữ"));
        khoaList.add(new nqtKhoa("KH04", "Khoa Kỹ thuật"));
        khoaList.add(new nqtKhoa("KH05", "Khoa Y dược"));
    }

    // Lấy tất cả khoa
    public List<nqtKhoa> findAll() {
        return khoaList;
    }

    // Lấy khoa theo mã khoa (makh)
    public Optional<nqtKhoa> findByMakh(String makh) {
        return khoaList.stream()
                .filter(khoa -> khoa.getMakh().equals(makh))
                .findFirst();
    }

    // Thêm khoa mới
    public Boolean create(nqtKhoaDTO khoaDTO) {
        try {
            // Kiểm tra xem mã khoa đã tồn tại chưa
            if (findByMakh(khoaDTO.getMakh()).isPresent()) {
                return false;
            }

            nqtKhoa khoa = new nqtKhoa();
            khoa.setMakh(khoaDTO.getMakh());
            khoa.setTenkh(khoaDTO.getTenkh());

            khoaList.add(khoa);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Sửa thông tin khoa theo mã khoa
    public Boolean update(String makh, nqtKhoaDTO khoaDTO) {
        try {
            Optional<nqtKhoa> existingKhoa = findByMakh(makh);
            if (existingKhoa.isPresent()) {
                nqtKhoa khoa = existingKhoa.get();
                khoa.setTenkh(khoaDTO.getTenkh());
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Xóa khoa theo mã khoa
    public Boolean delete(String makh) {
        try {
            return khoaList.removeIf(khoa -> khoa.getMakh().equals(makh));
        } catch (Exception e) {
            return false;
        }
    }
}

