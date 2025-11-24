package k23cnt1.nqt.lesson08_2.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nqt_book")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"bookAuthors"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "price")
    private Double price = 0.0;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookAuthor> bookAuthors = new HashSet<>();
}

