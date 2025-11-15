package k23cnt1.nqt.lesson06.nqt_lesson06.nqtRepository;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtEntity.nqtStudent;
import
        org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface nqtStudentRepository extends
        JpaRepository<nqtStudent, Long> {
}