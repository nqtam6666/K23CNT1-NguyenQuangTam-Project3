package k23cnt1.nqt.nqt_lesson07.nqtRepository;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface nqtProductRepository extends
        JpaRepository<nqtProduct, Long> {
}