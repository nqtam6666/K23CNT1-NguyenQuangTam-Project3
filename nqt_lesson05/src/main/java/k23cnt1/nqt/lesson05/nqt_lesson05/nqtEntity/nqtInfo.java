package k23cnt1.nqt.lesson05.nqt_lesson05.nqtEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class nqtInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String fullName;
    String gender;
    Integer age;
    Double salary;
}
