package com.nqtam_lab04.nqtam_lab04_Phan2.nqtController;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtMonHocDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtMonHoc;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtService.nqtMonHocService;
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
@RequestMapping("/nqt-monhoc")
@AllArgsConstructor
@NoArgsConstructor
public class nqtMonHocController {

    @Autowired
    nqtMonHocService monHocService;

    // GET: Lấy danh sách tất cả môn học
    @GetMapping
    public List<nqtMonHoc> getAllMonHoc() {
        return monHocService.findAll();
    }

    // GET: Lấy môn học theo mã môn học
    @GetMapping("/{mamh}")
    public ResponseEntity<nqtMonHoc> getMonHocByMamh(@PathVariable String mamh) {
        Optional<nqtMonHoc> monHoc = monHocService.findByMamh(mamh);
        return monHoc.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Thêm môn học mới
    @PostMapping
    public ResponseEntity<String> addMonHoc(@Valid @RequestBody nqtMonHocDTO monHocDTO) {
        Boolean result = monHocService.create(monHocDTO);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("MonHoc created successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MonHoc already exists or creation failed");
    }

    // PUT: Sửa thông tin môn học theo mã môn học
    @PutMapping("/{mamh}")
    public ResponseEntity<String> updateMonHoc(@PathVariable String mamh, @Valid @RequestBody nqtMonHocDTO monHocDTO) {
        Boolean result = monHocService.update(mamh, monHocDTO);
        if (result) {
            return ResponseEntity.ok("MonHoc updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MonHoc not found");
    }

    // DELETE: Xóa môn học theo mã môn học
    @DeleteMapping("/{mamh}")
    public ResponseEntity<String> deleteMonHoc(@PathVariable String mamh) {
        Boolean result = monHocService.delete(mamh);
        if (result) {
            return ResponseEntity.ok("MonHoc deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MonHoc not found");
    }
}

