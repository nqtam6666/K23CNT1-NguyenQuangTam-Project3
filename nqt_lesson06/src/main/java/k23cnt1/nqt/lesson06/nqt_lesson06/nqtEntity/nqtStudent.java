package k23cnt1.nqt.lesson06.nqt_lesson06.nqtEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class nqtStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private int age;
}