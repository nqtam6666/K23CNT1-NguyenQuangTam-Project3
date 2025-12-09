package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtCronLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NqtCronLogRepository extends JpaRepository<NqtCronLog, Integer> {

    // Lấy log mới nhất
    List<NqtCronLog> findTop100ByOrderByNqtCreatedAtDesc();

    // Lấy log theo task name
    List<NqtCronLog> findByNqtTaskNameOrderByNqtCreatedAtDesc(String nqtTaskName);

    // Lấy log theo status
    List<NqtCronLog> findByNqtStatusOrderByNqtCreatedAtDesc(String nqtStatus);

    // Đếm số lượng log
    long count();

    // Xóa log cũ hơn một thời điểm
    @Modifying
    @Query("DELETE FROM NqtCronLog WHERE nqtCreatedAt < :beforeDate")
    void deleteByNqtCreatedAtBefore(@Param("beforeDate") LocalDateTime beforeDate);

    // Xóa log cũ nhất (giữ lại N bản ghi mới nhất)
    @Modifying
    @Query(value = "DELETE FROM nqtCronLog WHERE nqtId NOT IN (SELECT nqtId FROM (SELECT nqtId FROM nqtCronLog ORDER BY nqtCreatedAt DESC LIMIT :keepCount) AS temp)", nativeQuery = true)
    void deleteOldLogsKeepLatest(@Param("keepCount") int keepCount);

    // Lấy log với phân trang
    Page<NqtCronLog> findAllByOrderByNqtCreatedAtDesc(Pageable pageable);
}

