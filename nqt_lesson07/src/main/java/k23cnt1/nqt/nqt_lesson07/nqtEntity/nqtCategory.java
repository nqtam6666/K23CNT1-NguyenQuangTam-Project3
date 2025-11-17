package k23cnt1.nqt.nqt_lesson07.nqtEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Entity
@Table(name = "categories")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "name")
    String name;
    
    @Column(name = "status")
    Boolean status;
}