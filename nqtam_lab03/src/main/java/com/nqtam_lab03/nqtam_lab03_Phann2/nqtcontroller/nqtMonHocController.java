package com.nqtam_lab03.nqtam_lab03_Phann2.nqtcontroller;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtMonHoc;
import com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice.nqtMonHocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/monhoc") // Tiền tố route
public class nqtMonHocController {

    @Autowired
    private nqtMonHocService nqtMonHocService;

    // 1. Lấy toàn bộ danh sách môn học (GET /api/monhoc/list)
    @GetMapping("/list")
    public List<nqtMonHoc> getAllMonHocs() {
        return nqtMonHocService.getMonHocs();
    }

    // 2. Lấy môn học theo mã (GET /api/monhoc/{mamh})
    @GetMapping("/{mamh}")
    public nqtMonHoc getMonHoc(@PathVariable String mamh) {
        return nqtMonHocService.getMonHoc(mamh);
    }

    // 3. Thêm mới một môn học (POST /api/monhoc/add)
    @PostMapping("/add")
    public nqtMonHoc addMonHoc(@RequestBody nqtMonHoc monHoc) {
        return nqtMonHocService.addMonHoc(monHoc);
    }

    // 4. Cập nhật thông tin môn học (PUT /api/monhoc/{mamh})
    @PutMapping("/{mamh}")
    public nqtMonHoc updateMonHoc(@PathVariable String mamh,
                                  @RequestBody nqtMonHoc monHoc) {
        return nqtMonHocService.updateMonHoc(mamh, monHoc);
    }

    // 5. Xóa thông tin môn học (DELETE /api/monhoc/{mamh})
    @DeleteMapping("/{mamh}")
    public boolean deleteMonHoc(@PathVariable String mamh) {
        return nqtMonHocService.deleteMonHoc(mamh);
    }
}