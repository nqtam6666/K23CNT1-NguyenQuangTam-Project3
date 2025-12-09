package com.nqtam_lab04.nqtam_lab04_Phan2.nqtController;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtKhoaDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtKhoa;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtService.nqtKhoaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nqt-khoa")
@AllArgsConstructor
@NoArgsConstructor
public class nqtKhoaController {

    @Autowired
    nqtKhoaService khoaService;

    // GET: Lấy danh sách tất cả khoa
    @GetMapping
    public List<nqtKhoa> getAllKhoa() {
        return khoaService.findAll();
    }

    // GET: Lấy khoa theo mã khoa
    @GetMapping("/{makh}")
    public ResponseEntity<nqtKhoa> getKhoaByMakh(@PathVariable String makh) {
        Optional<nqtKhoa> khoa = khoaService.findByMakh(makh);
        return khoa.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Thêm khoa mới
    @PostMapping
    public ResponseEntity<String> addKhoa(@Valid @RequestBody nqtKhoaDTO khoaDTO) {
        Boolean result = khoaService.create(khoaDTO);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Khoa created successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Khoa already exists or creation failed");
    }

    // PUT: Sửa thông tin khoa theo mã khoa
    @PutMapping("/{makh}")
    public ResponseEntity<String> updateKhoa(@PathVariable String makh, @Valid @RequestBody nqtKhoaDTO khoaDTO) {
        Boolean result = khoaService.update(makh, khoaDTO);
        if (result) {
            return ResponseEntity.ok("Khoa updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Khoa not found");
    }

    // DELETE: Xóa khoa theo mã khoa
    @DeleteMapping("/{makh}")
    public ResponseEntity<String> deleteKhoa(@PathVariable String makh) {
        Boolean result = khoaService.delete(makh);
        if (result) {
            return ResponseEntity.ok("Khoa deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Khoa not found");
    }
}

