package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtNganHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NqtNganHangRepository extends JpaRepository<NqtNganHang, Integer> {

    // Lấy danh sách ngân hàng đang hoạt động, sắp xếp theo thứ tự
    List<NqtNganHang> findByNqtStatusTrueOrderByNqtThuTuAsc();

    // Lấy tất cả ngân hàng, sắp xếp theo thứ tự
    List<NqtNganHang> findAllByOrderByNqtThuTuAsc();

    // Kiểm tra mã ngân hàng đã tồn tại chưa
    boolean existsByNqtMaNganHang(String nqtMaNganHang);

    // Tìm theo mã ngân hàng
    java.util.Optional<NqtNganHang> findByNqtMaNganHang(String nqtMaNganHang);
}

