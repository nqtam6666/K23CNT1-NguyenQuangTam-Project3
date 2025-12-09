package com.nqtam_lab03.nqtam_lab03_Phann2.nqtcontroller;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtEmployee;
import com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice.nqtEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee") // Tiền tố route
public class nqtEmployeeController {

    @Autowired
    private nqtEmployeeService nqtEmployeeService;

    // 1. Lấy toàn bộ danh sách nhân viên (GET /api/employee/list)
    @GetMapping("/list")
    public List<nqtEmployee> getAllEmployees() {
        return nqtEmployeeService.getEmployees();
    }

    // 2. Lấy nhân viên theo ID (GET /api/employee/{id})
    @GetMapping("/{id}")
    public nqtEmployee getEmployee(@PathVariable String id) {
        Long param = Long.parseLong(id); // Chuyển String ID sang Long
        return nqtEmployeeService.getEmployee(param);
    }

    // 3. Thêm mới một nhân viên (POST /api/employee/add)
    @PostMapping("/add")
    public nqtEmployee addEmployee(@RequestBody nqtEmployee employee) {
        return nqtEmployeeService.addEmployee(employee);
    }

    // 4. Cập nhật thông tin nhân viên (PUT /api/employee/{id})
    @PutMapping("/{id}")
    public nqtEmployee updateEmployee(@PathVariable String id,
                                      @RequestBody nqtEmployee employee) {
        Long param = Long.parseLong(id); // Chuyển String ID sang Long
        return nqtEmployeeService.updateEmployee(param, employee);
    }

    // 5. Xóa thông tin nhân viên (DELETE /api/employee/{id})
    @DeleteMapping("/{id}")
    public boolean deleteEmployee(@PathVariable String id) {
        Long param = Long.parseLong(id); // Chuyển String ID sang Long
        return nqtEmployeeService.deleteEmployee(param);
    }
}