package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NqtNguoiDungRepository extends JpaRepository<NqtNguoiDung, Integer> {
    Optional<NqtNguoiDung> findByNqtTaiKhoan(String nqtTaiKhoan);

    Optional<NqtNguoiDung> findByNqtTaiKhoanOrNqtEmail(String nqtTaiKhoan, String nqtEmail);
    
    Optional<NqtNguoiDung> findByNqtPasswordResetToken(String token);

    Optional<NqtNguoiDung> findByNqtEmail(String nqtEmail);

    boolean existsByNqtTaiKhoan(String nqtTaiKhoan);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE NqtNguoiDung n SET n.nqtMatKhau = :password WHERE n.nqtId = :userId")
    int updatePassword(@Param("userId") Integer userId, @Param("password") String password);
}
