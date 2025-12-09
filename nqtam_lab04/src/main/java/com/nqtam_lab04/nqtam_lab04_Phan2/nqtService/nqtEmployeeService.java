package com.nqtam_lab04.nqtam_lab04_Phan2.nqtService;

import com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto.nqtEmployeeDTO;
import com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity.nqtEmployee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class nqtEmployeeService {

    // Danh sách nhân viên được lưu trong bộ nhớ (in-memory)
    List<nqtEmployee> employeeList = new ArrayList<>();

    // Khởi tạo dữ liệu mẫu khi service được tạo
    public nqtEmployeeService() {
        employeeList.add(new nqtEmployee(1L, "Nguyễn Văn A", "Nam", 25, 5000000.0));
        employeeList.add(new nqtEmployee(2L, "Trần Thị B", "Nữ", 30, 6000000.0));
        employeeList.add(new nqtEmployee(3L, "Lê Văn C", "Nam", 28, 5500000.0));
        employeeList.add(new nqtEmployee(4L, "Phạm Thị D", "Nữ", 35, 7000000.0));
        employeeList.add(new nqtEmployee(5L, "Hoàng Văn E", "Nam", 22, 4500000.0));
    }

    // Lấy tất cả nhân viên
    public List<nqtEmployee> findAll() {
        return employeeList;
    }

    // Lấy nhân viên theo id
    public Optional<nqtEmployee> findById(Long id) {
        return employeeList.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst();
    }

    // Thêm nhân viên mới
    public Boolean create(nqtEmployeeDTO employeeDTO) {
        try {
            nqtEmployee employee = new nqtEmployee();

            // Tự động tạo ID bằng cách đếm số lượng hiện tại và thêm 1
            employee.setId(employeeList.stream().count() + 1);

            employee.setFullName(employeeDTO.getFullName());
            employee.setGender(employeeDTO.getGender());
            employee.setAge(employeeDTO.getAge());
            employee.setSalary(employeeDTO.getSalary());

            employeeList.add(employee);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Sửa thông tin nhân viên theo id
    public Boolean update(Long id, nqtEmployeeDTO employeeDTO) {
        try {
            Optional<nqtEmployee> existingEmployee = findById(id);
            if (existingEmployee.isPresent()) {
                nqtEmployee employee = existingEmployee.get();
                employee.setFullName(employeeDTO.getFullName());
                employee.setGender(employeeDTO.getGender());
                employee.setAge(employeeDTO.getAge());
                employee.setSalary(employeeDTO.getSalary());
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Xóa nhân viên theo id
    public Boolean delete(Long id) {
        try {
            return employeeList.removeIf(employee -> employee.getId().equals(id));
        } catch (Exception e) {
            return false;
        }
    }
}

