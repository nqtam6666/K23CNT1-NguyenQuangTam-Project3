package k23cnt1.nqt.nqt_lesson07.nqtEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "products")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "name")
    String name;
    
    @Column(name = "image_url")
    String imageUrl;
    
    @Column(name = "quantity")
    Integer quantity;
    
    @Column(name = "price")
    Double price;
    
    @Column(name = "content")
    String content;
    
    @Column(name = "status")
    Boolean status;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    nqtCategory nqtCategory;
}