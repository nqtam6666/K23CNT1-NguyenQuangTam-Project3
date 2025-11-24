package k23cnt1.nqt.lesson08_2.nqtRepository;

import k23cnt1.nqt.lesson08_2.nqtEntity.BookAuthor;
import k23cnt1.nqt.lesson08_2.nqtEntity.BookAuthorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {
    @Modifying
    @Query("DELETE FROM BookAuthor ba WHERE ba.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
}

