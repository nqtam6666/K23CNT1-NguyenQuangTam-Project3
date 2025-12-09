package k23cnt1.nqt.lesson08_2.nqtRepository;

import k23cnt1.nqt.lesson08_2.nqtEntity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByCode(String code);
    
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookAuthors ba LEFT JOIN FETCH ba.author")
    List<Book> findAllWithAuthors();
}

