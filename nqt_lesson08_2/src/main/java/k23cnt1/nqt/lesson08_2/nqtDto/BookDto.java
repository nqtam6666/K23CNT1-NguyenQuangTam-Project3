package k23cnt1.nqt.lesson08_2.nqtDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private Integer quantity;
    private Double price;
    private Boolean isActive;
    private List<Long> authorIds = new ArrayList<>();
    private List<String> authorNames = new ArrayList<>();
    private Long editorId; // ID của tác giả là chủ biên
}

