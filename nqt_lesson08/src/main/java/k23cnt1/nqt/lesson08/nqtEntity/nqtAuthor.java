package k23cnt1.nqt.lesson08.nqtEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class nqtAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private String email;
    private String phone;
    private String address;
    private boolean isActive;
    // Tạo mối quan hệ với bảng book
    @ManyToMany(mappedBy = "authors")
    private List<nqtBook> books = new ArrayList<>();
}
/*
Giải thích chi tiết
mappedBy = "authors": Thuộc tính mappedBy này chỉ ra rằng
cột liên kết giữa Book và Author đã được định nghĩa ở bên
Book, trong trường authors.
List<Book> books: Đây là một tập hợp các Book liên kết với
Author. Với mappedBy, tập hợp này chỉ là "bản sao" của mối
quan hệ và không phải là nơi tạo ra mối quan hệ đó trong cơ
sở dữ liệu.
*/
/*
Tại sao cần mappedBy?
Trong mối quan hệ nhiều-nhiều giữa hai thực thể Book và
Author, nếu không sử dụng mappedBy, mỗi thực thể sẽ tự động
tạo ra một bảng trung gian để quản lý quan hệ. Tuy nhiên,
điều này sẽ dẫn đến việc tạo ra nhiều bảng trung gian cho
cùng một mối quan hệ, gây trùng lặp và lỗi.
*/