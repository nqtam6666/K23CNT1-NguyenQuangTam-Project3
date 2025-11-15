package k23cnt1.nqt.lesson06.nqt_lesson06.nqtDto;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class nqtStudentDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
}
