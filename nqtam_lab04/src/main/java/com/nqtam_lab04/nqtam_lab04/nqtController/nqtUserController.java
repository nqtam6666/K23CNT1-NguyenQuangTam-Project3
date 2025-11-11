package com.nqtam_lab04.nqtam_lab04.nqtController;

import com.nqtam_lab04.nqtam_lab04.nqtDto.nqtUserDTO;
import com.nqtam_lab04.nqtam_lab04.nqtEntity.nqtUser;
import com.nqtam_lab04.nqtam_lab04.nqtService.nqtUserService;
import jakarta.validation.Valid; // Có thể là javax.validation.Valid nếu dùng Spring cũ
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@NoArgsConstructor // Các annotations Lombok
public class nqtUserController {

    @Autowired
    nqtUserService usersService;

    // Endpoint để lấy danh sách tất cả người dùng
    @GetMapping("/user-list")
    public List<nqtUser> getAllUsers() {
        return usersService.findAll();
    }

    // Endpoint để thêm người dùng mới
    @PostMapping("/user-add")
    public ResponseEntity<String> addUser(@Valid @RequestBody nqtUserDTO user) {
        usersService.create(user);

        // Lưu ý: Mã trả về đang là BadRequest, nhưng nội dung là "created successfully".
        // Thông thường, nên trả về status 201 Created hoặc 200 OK.
        return ResponseEntity.badRequest().body("Users created successfully");
    }
}