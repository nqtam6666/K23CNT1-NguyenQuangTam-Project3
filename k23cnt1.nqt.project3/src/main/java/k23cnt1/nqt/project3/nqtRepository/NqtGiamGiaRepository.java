package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtGiamGia;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NqtGiamGiaRepository extends JpaRepository<NqtGiamGia, Integer> {

    // Tìm mã giảm giá theo mã code
    Optional<NqtGiamGia> findByNqtMaGiamGia(String nqtMaGiamGia);

    // Tìm mã giảm giá đang hoạt động theo mã code
    Optional<NqtGiamGia> findByNqtMaGiamGiaAndNqtStatusTrue(String nqtMaGiamGia);

    // Tìm mã giảm giá chung (không gắn với người dùng cụ thể) và đang hoạt động
    @Query("SELECT g FROM NqtGiamGia g WHERE g.nqtNguoiDung IS NULL AND g.nqtStatus = true")
    List<NqtGiamGia> findCommonActiveCodes();

    // Tìm mã giảm giá dành riêng cho một người dùng và đang hoạt động
    @Query("SELECT g FROM NqtGiamGia g WHERE g.nqtNguoiDung = :nguoiDung AND g.nqtStatus = true")
    List<NqtGiamGia> findByNguoiDungAndActive(@Param("nguoiDung") NqtNguoiDung nguoiDung);

    // Tìm mã giảm giá hợp lệ (đang hoạt động, trong thời gian áp dụng, chưa hết số lượng)
    @Query("SELECT g FROM NqtGiamGia g WHERE " +
           "g.nqtMaGiamGia = :maGiamGia AND " +
           "g.nqtStatus = true AND " +
           "(g.nqtNgayBatDau IS NULL OR g.nqtNgayBatDau <= :ngayHienTai) AND " +
           "(g.nqtNgayKetThuc IS NULL OR g.nqtNgayKetThuc >= :ngayHienTai) AND " +
           "(g.nqtSoLuongToiDa IS NULL OR g.nqtSoLuongDaDung < g.nqtSoLuongToiDa)")
    Optional<NqtGiamGia> findValidCode(@Param("maGiamGia") String maGiamGia, 
                                        @Param("ngayHienTai") LocalDate ngayHienTai);

    // Tìm mã giảm giá hợp lệ cho một người dùng cụ thể
    @Query("SELECT g FROM NqtGiamGia g WHERE " +
           "g.nqtMaGiamGia = :maGiamGia AND " +
           "g.nqtStatus = true AND " +
           "(g.nqtNguoiDung IS NULL OR g.nqtNguoiDung = :nguoiDung) AND " +
           "(g.nqtChiChoVip IS NULL OR g.nqtChiChoVip = false OR " +
           " (g.nqtChiChoVip = true AND :nguoiDungCapBac = 'KhachVip')) AND " +
           "(g.nqtNgayBatDau IS NULL OR g.nqtNgayBatDau <= :ngayHienTai) AND " +
           "(g.nqtNgayKetThuc IS NULL OR g.nqtNgayKetThuc >= :ngayHienTai) AND " +
           "(g.nqtSoLuongToiDa IS NULL OR g.nqtSoLuongDaDung < g.nqtSoLuongToiDa)")
    Optional<NqtGiamGia> findValidCodeForUser(@Param("maGiamGia") String maGiamGia,
                                               @Param("nguoiDung") NqtNguoiDung nguoiDung,
                                               @Param("nguoiDungCapBac") String nguoiDungCapBac,
                                               @Param("ngayHienTai") LocalDate ngayHienTai);

    // Tìm tất cả mã giảm giá đang hoạt động
    List<NqtGiamGia> findByNqtStatusTrue();
}

