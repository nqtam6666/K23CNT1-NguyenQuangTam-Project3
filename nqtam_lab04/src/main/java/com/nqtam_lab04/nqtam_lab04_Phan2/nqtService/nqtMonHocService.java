package com.nqtam_lab04.nqtam_lab04_Phan2.nqtService;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtMonHocDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtMonHoc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class nqtMonHocService {

    // Danh sách môn học được lưu trong bộ nhớ (in-memory)
    List<nqtMonHoc> monHocList = new ArrayList<>();

    // Khởi tạo dữ liệu mẫu khi service được tạo
    public nqtMonHocService() {
        monHocList.add(new nqtMonHoc("MH1", "Lập trình Java", 60));
        monHocList.add(new nqtMonHoc("MH2", "Cơ sở dữ liệu", 50));
        monHocList.add(new nqtMonHoc("MH3", "Mạng máy tính", 55));
        monHocList.add(new nqtMonHoc("MH4", "Hệ điều hành", 45));
        monHocList.add(new nqtMonHoc("MH5", "Cấu trúc dữ liệu", 65));
    }

    // Lấy tất cả môn học
    public List<nqtMonHoc> findAll() {
        return monHocList;
    }

    // Lấy môn học theo mã môn học (mamh)
    public Optional<nqtMonHoc> findByMamh(String mamh) {
        return monHocList.stream()
                .filter(monHoc -> monHoc.getMamh().equals(mamh))
                .findFirst();
    }

    // Thêm môn học mới
    public Boolean create(nqtMonHocDTO monHocDTO) {
        try {
            // Kiểm tra xem mã môn học đã tồn tại chưa
            if (findByMamh(monHocDTO.getMamh()).isPresent()) {
                return false;
            }

            nqtMonHoc monHoc = new nqtMonHoc();
            monHoc.setMamh(monHocDTO.getMamh());
            monHoc.setTenmh(monHocDTO.getTenmh());
            monHoc.setSotiet(monHocDTO.getSotiet());

            monHocList.add(monHoc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Sửa thông tin môn học theo mã môn học
    public Boolean update(String mamh, nqtMonHocDTO monHocDTO) {
        try {
            Optional<nqtMonHoc> existingMonHoc = findByMamh(mamh);
            if (existingMonHoc.isPresent()) {
                nqtMonHoc monHoc = existingMonHoc.get();
                monHoc.setTenmh(monHocDTO.getTenmh());
                monHoc.setSotiet(monHocDTO.getSotiet());
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Xóa môn học theo mã môn học
    public Boolean delete(String mamh) {
        try {
            return monHocList.removeIf(monHoc -> monHoc.getMamh().equals(mamh));
        } catch (Exception e) {
            return false;
        }
    }
}

