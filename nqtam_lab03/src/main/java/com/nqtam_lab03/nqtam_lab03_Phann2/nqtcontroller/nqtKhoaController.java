package com.nqtam_lab03.nqtam_lab03_Phann2.nqtcontroller;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtKhoa;
import com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice.nqtKhoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/khoa") // Tiền tố route cho tất cả các phương thức trong Controller này
public class nqtKhoaController {

    @Autowired
    private nqtKhoaService nqtKhoaService;

    // 1. Lấy toàn bộ danh sách khoa (GET /api/khoa/list)
    @GetMapping("/list")
    public List<nqtKhoa> getAllKhoas() {
        return nqtKhoaService.getKhoas();
    }

    // 2. Lấy khoa theo mã (GET /api/khoa/{makh})
    @GetMapping("/{makh}")
    public nqtKhoa getKhoa(@PathVariable String makh) {
        return nqtKhoaService.getKhoa(makh);
    }

    // 3. Thêm mới một khoa (POST /api/khoa/add)
    @PostMapping("/add")
    public nqtKhoa addKhoa(@RequestBody nqtKhoa khoa) {
        return nqtKhoaService.addKhoa(khoa);
    }

    // 4. Cập nhật thông tin khoa (PUT /api/khoa/{makh})
    @PutMapping("/{makh}")
    public nqtKhoa updateKhoa(@PathVariable String makh,
                              @RequestBody nqtKhoa khoa) {
        return nqtKhoaService.updateKhoa(makh, khoa);
    }

    // 5. Xóa thông tin khoa (DELETE /api/khoa/{makh})
    @DeleteMapping("/{makh}")
    public boolean deleteKhoa(@PathVariable String makh) {
        return nqtKhoaService.deleteKhoa(makh);
    }
}