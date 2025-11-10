package com.nqtam_lab03.nqtam_lab03.nqtservice;

import com.nqtam_lab03.nqtam_lab03.nqtentity.nqtStudent;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class nqtStudentService {
    List<nqtStudent> students
            = new ArrayList<nqtStudent>();
    public nqtStudentService() {
        students.addAll(Arrays.asList(
                new nqtStudent(1L,"Devmaster 1",20,"Non","Số 25 VNP","0978611889","chungtrinhj@gmail.com"),
                new nqtStudent(2L,"Devmaster 2",25,"Non","Số 25 VNP","0978611889","contact@devmaster.edu.vn"),
                new nqtStudent(3L,"Devmaster 3",22,"Non","Số 25 VNP","0978611889","chungtrinhj@gmail.com")
        ));
    }
    // Lấy toàn bộ danh sách sinh viên
    public List<nqtStudent> getStudents() {
        return students;
    }
    // Lấy sinh viên theo id
    public nqtStudent getStudent(Long id) {
        return students.stream()
                .filter(student -> student
                        .getId().equals(id))
                .findFirst().orElse(null);
    }
    // Thêm mới một sinh viên
    public nqtStudent addStudent(nqtStudent student) {

        students.add(student);
        return student;
    }
    // Cập nhật thông tin sinh viên
    public nqtStudent updateStudent(Long id, nqtStudent student)
    {
        nqtStudent check = getStudent(id);
        if (check == null) {
            return null;
        }
        students.forEach(item -> {
            if (item.getId()==id) {
                item.setName(student.getName());
                item.setAddress(student.getAddress());
                item.setEmail(student.getEmail());
                item.setPhone(student.getPhone());
                item.setAge(student.getAge());
                item.setGender(student.getGender());
            }
        });
        return student;
    }
    // Xóa thông tin sinh viên
    public boolean deleteStudent(Long id){
        nqtStudent check = getStudent(id);
        return students.remove(check);
    }
}