package k23cnt1.nqt.nqt_lesson07.nqtEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "books")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "name")
    String name;
    
    @Column(name = "img_url")
    String imgUrl;
    
    @Column(name = "qty")
    Integer Qty;
    
    @Column(name = "price")
    Double Price;
    
    @Column(name = "year_release")
    Integer yearRelease;
    
    @Column(name = "author")
    String author;
    
    @Column(name = "status")
    Boolean status;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    nqtCategory category;
}

