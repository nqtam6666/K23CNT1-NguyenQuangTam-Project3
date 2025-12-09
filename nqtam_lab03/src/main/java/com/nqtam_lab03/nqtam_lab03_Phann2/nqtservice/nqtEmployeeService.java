package com.nqtam_lab03.nqtam_lab03_Phann2.nqtservice;

import com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity.nqtEmployee;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class nqtEmployeeService {
    List<nqtEmployee> employees = new ArrayList<>();

    // Hàm tạo để khởi tạo dữ liệu mẫu
    public nqtEmployeeService() {
        employees.addAll(Arrays.asList(
                new nqtEmployee(222L, "Nguyễn Quang Tâm", "Nam", 21, 1500.0),
                new nqtEmployee(101L, "Lê Thị C", "Nữ", 28, 1200.0),
                new nqtEmployee(102L, "Phạm Văn D", "Nam", 42, 1800.0),
                new nqtEmployee(103L, "Hoàng Thị E", "Nữ", 25, 1100.0),
                new nqtEmployee(104L, "Nguyễn Văn F", "Nam", 30, 1350.0)
        ));
    }

    // 1. Lấy toàn bộ danh sách
    public List<nqtEmployee> getEmployees() {
        return employees;
    }

    // 2. Lấy danh sách theo id
    public nqtEmployee getEmployee(Long id) {
        return employees.stream()
                .filter(emp -> emp.getId().equals(id))
                .findFirst().orElse(null);
    }

    // 3. Thêm mới một nhân viên
    public nqtEmployee addEmployee(nqtEmployee employee) {
        employees.add(employee);
        return employee;
    }

    // 4. Sửa đổi thông tin nhân viên theo id
    public nqtEmployee updateEmployee(Long id, nqtEmployee employeeMoi) {
        nqtEmployee check = getEmployee(id);
        if (check == null) {
            return null; // Không tìm thấy nhân viên
        }

        employees.forEach(item -> {
            if (item.getId().equals(id)) {
                item.setFullName(employeeMoi.getFullName());
                item.setGender(employeeMoi.getGender());
                item.setAge(employeeMoi.getAge());
                item.setSalary(employeeMoi.getSalary());
            }
        });
        return getEmployee(id);
    }

    // 5. Xóa thông tin nhân viên theo id
    public boolean deleteEmployee(Long id) {
        nqtEmployee check = getEmployee(id);
        if (check == null) {
            return false;
        }
        return employees.remove(check);
    }
}