package k23cnt1.nqt.lesson08.nqtRepositoty;
import k23cnt1.nqt.lesson08.nqtEntity.nqtAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface nqtAuthorRepository extends JpaRepository<nqtAuthor, Long> {
}