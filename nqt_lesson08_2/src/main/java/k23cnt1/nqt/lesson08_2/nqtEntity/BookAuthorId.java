package k23cnt1.nqt.lesson08_2.nqtEntity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAuthorId implements Serializable {
    private Long bookId;
    private Long authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAuthorId that = (BookAuthorId) o;
        return Objects.equals(bookId, that.bookId) &&
               Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, authorId);
    }
}

