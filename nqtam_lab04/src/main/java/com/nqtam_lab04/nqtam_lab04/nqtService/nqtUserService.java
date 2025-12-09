package com.nqtam_lab04.nqtam_lab04.nqtService;

import com.nqtam_lab04.nqtam_lab04.nqtDto.nqtUserDTO;
import com.nqtam_lab04.nqtam_lab04.nqtEntity.nqtUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Import này có thể cần thiết nếu dùng stream()

@Service
public class nqtUserService {

    // Danh sách người dùng được lưu trong bộ nhớ (in-memory)
    List<nqtUser> userList = new ArrayList<>();

    // Khởi tạo dữ liệu mẫu khi service được tạo
    public nqtUserService() {
        // Giả định constructor của Users là: (Long id, String username, String password, String email, String phone, int age, LocalDate birthday, boolean status)
        userList.add(new nqtUser(1L, "user1", "pass1", "John Doe", "john@example.com", "1234567890", 34, LocalDate.parse("1990-01-01"), true));
        userList.add(new nqtUser(2L, "user2", "pass2", "Jane Smith", "jane@example.com", "0987654321", 32, LocalDate.parse("1992-05-15"), false));
        userList.add(new nqtUser(3L, "user3", "pass3", "Alice Johnson", "alice@example.com", "1122334455", 39, LocalDate.parse("1985-11-22"), true));
        userList.add(new nqtUser(4L, "user4", "pass4", "Bob Brown", "bob@example.com", "6677889900", 36, LocalDate.parse("1988-03-18"), true));
        userList.add(new nqtUser(5L, "user5", "pass5", "Charlie White", "charlie@example.com", "4433221100", 29, LocalDate.parse("1995-09-30"), false));
    }

    // Lấy tất cả người dùng
    public List<nqtUser> findAll() {
        return userList;
    }

    // Tạo người dùng mới
    public Boolean create(nqtUserDTO usersDTO) {
        try {
            nqtUser user = new nqtUser();

            // 1. Tự động tạo ID bằng cách đếm số lượng hiện tại và thêm 1
            user.setId(userList.stream().count() + 1);

            // 2. Sao chép dữ liệu từ DTO sang Entity
            user.setUsername(usersDTO.getUsername());
            user.setPassword(usersDTO.getPassword());
            user.setEmail(usersDTO.getEmail());
            user.setFullName(usersDTO.getFullName());
            user.setPhone(usersDTO.getPhone());
            user.setAge(usersDTO.getAge());
            user.setBirthDay(usersDTO.getBirthDay());
            user.setStatus(usersDTO.getStatus());

            // 3. Thêm Entity vào danh sách
            userList.add(user);

            return true;
        } catch (Exception e) {
            // Xử lý lỗi (ví dụ: in stack trace hoặc log lỗi)
            return false;
        }
    }
}