package k23cnt1.nqt.nqt_lesson07.nqtRepository;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface nqtBookRepository extends JpaRepository<nqtBook, Long> {
}

