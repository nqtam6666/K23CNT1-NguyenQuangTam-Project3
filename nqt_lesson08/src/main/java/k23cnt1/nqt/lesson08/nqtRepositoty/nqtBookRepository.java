package k23cnt1.nqt.lesson08.nqtRepositoty;
import k23cnt1.nqt.lesson08.nqtEntity.nqtBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface nqtBookRepository extends JpaRepository<nqtBook, Long> {
}