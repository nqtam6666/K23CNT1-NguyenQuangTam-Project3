package k23cnt1.nqt.lesson08_2.nqtEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "nqt_book_author")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"book", "author"})
public class BookAuthor implements Serializable {
    @EmbeddedId
    private BookAuthorId id = new BookAuthorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "bookid")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorId")
    @JoinColumn(name = "authorid")
    private Author author;

    @Column(name = "is_editor")
    private Boolean isEditor = false;

    public BookAuthor(Book book, Author author, Boolean isEditor) {
        this.book = book;
        this.author = author;
        this.isEditor = isEditor != null ? isEditor : false;
        if (book != null && book.getId() != null && author != null && author.getId() != null) {
            this.id = new BookAuthorId(book.getId(), author.getId());
        } else {
            this.id = new BookAuthorId();
        }
    }
}

