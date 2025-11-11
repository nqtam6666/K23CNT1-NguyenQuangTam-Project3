package com.nqtam_lab04.nqtam_lab04_Phan2.nqtController;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtEmployeeDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtEmployee;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtService.nqtEmployeeService;
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
@RequestMapping("/nqt-employee")
@AllArgsConstructor
@NoArgsConstructor
public class nqtEmployeeController {

    @Autowired
    nqtEmployeeService employeeService;

    // GET: Lấy danh sách tất cả nhân viên
    @GetMapping
    public List<nqtEmployee> getAllEmployee() {
        return employeeService.findAll();
    }

    // GET: Lấy nhân viên theo id
    @GetMapping("/{id}")
    public ResponseEntity<nqtEmployee> getEmployeeById(@PathVariable Long id) {
        Optional<nqtEmployee> employee = employeeService.findById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Thêm nhân viên mới
    @PostMapping
    public ResponseEntity<String> addEmployee(@Valid @RequestBody nqtEmployeeDTO employeeDTO) {
        Boolean result = employeeService.create(employeeDTO);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Employee created successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee creation failed");
    }

    // PUT: Sửa thông tin nhân viên theo id
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long id, @Valid @RequestBody nqtEmployeeDTO employeeDTO) {
        Boolean result = employeeService.update(id, employeeDTO);
        if (result) {
            return ResponseEntity.ok("Employee updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
    }

    // DELETE: Xóa nhân viên theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        Boolean result = employeeService.delete(id);
        if (result) {
            return ResponseEntity.ok("Employee deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
    }
}

