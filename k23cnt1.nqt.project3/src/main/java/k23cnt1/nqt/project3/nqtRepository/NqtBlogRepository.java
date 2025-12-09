package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtBlogRepository extends JpaRepository<NqtBlog, Integer> {
    List<NqtBlog> findByNqtStatusOrderByNqtNgayTaoDesc(Boolean nqtStatus);

    List<NqtBlog> findByNqtTieuDeContainingOrNqtNoiDungContaining(String nqtTieuDe, String nqtNoiDung);
}
