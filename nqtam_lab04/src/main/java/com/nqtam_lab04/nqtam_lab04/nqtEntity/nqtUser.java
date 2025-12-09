package com.nqtam_lab04.nqtam_lab04.nqtEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data // Tự động tạo Getter, Setter, toString, equals, hashCode
@Builder // Cho phép xây dựng đối tượng dễ dàng
@AllArgsConstructor // Constructor với tất cả các tham số
@NoArgsConstructor // Constructor không tham số
@FieldDefaults(level = AccessLevel.PRIVATE) // Đặt tất cả các fields là private
@Entity // Đánh dấu đây là một Entity JPA
@Getter
@Setter
public class nqtUser {
    @Id // Đánh dấu là khóa chính
    // Giả định constructor của Users là: (Long id, String username, String password, String email, String phone, int age, LocalDate birthday, boolean status)

    @GeneratedValue(strategy = GenerationType.IDENTITY) // Chiến lược tự động tăng ID
    Long id;

    // Các trường cơ bản (từ Users DTO và Entity mẫu)
    String username;
    String password;
    String fullName;
    String email;
    String phone;

    int age;
    // Các trường bổ sung (từ DTO mẫu)
    LocalDate birthDay;
    @Column(unique = true) // Đảm bảo email là duy nhất trong CSDL

    Boolean status;
}