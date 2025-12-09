package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtCronLog;
import k23cnt1.nqt.project3.nqtRepository.NqtCronLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NqtCronLogService {

    @Autowired
    private NqtCronLogRepository nqtCronLogRepository;

    private static final int MAX_LOG_COUNT = 100;

    /**
     * Ghi log cho cron task
     */
    @Transactional
    public void logCronTask(String taskName, String status, String message, 
                           Integer recordsProcessed, Long executionTime, String errorDetails) {
        NqtCronLog log = new NqtCronLog();
        log.setNqtTaskName(taskName);
        log.setNqtStatus(status);
        log.setNqtMessage(message);
        log.setNqtRecordsProcessed(recordsProcessed);
        log.setNqtExecutionTime(executionTime);
        log.setNqtErrorDetails(errorDetails);
        
        nqtCronLogRepository.save(log);
        
        // Tự động làm sạch nếu quá 100 dòng
        autoCleanupLogs();
    }

    /**
     * Ghi log thành công
     */
    @Transactional
    public void logSuccess(String taskName, String message, Integer recordsProcessed, Long executionTime) {
        logCronTask(taskName, "SUCCESS", message, recordsProcessed, executionTime, null);
    }

    /**
     * Ghi log lỗi
     */
    @Transactional
    public void logError(String taskName, String message, String errorDetails, Long executionTime) {
        logCronTask(taskName, "ERROR", message, null, executionTime, errorDetails);
    }

    /**
     * Ghi log cảnh báo
     */
    @Transactional
    public void logWarning(String taskName, String message, Long executionTime) {
        logCronTask(taskName, "WARNING", message, null, executionTime, null);
    }

    /**
     * Tự động làm sạch log nếu quá 100 dòng
     */
    @Transactional
    public void autoCleanupLogs() {
        long totalCount = nqtCronLogRepository.count();
        
        if (totalCount > MAX_LOG_COUNT) {
            // Xóa các log cũ, chỉ giữ lại 100 bản ghi mới nhất
            // Lấy danh sách 100 bản ghi mới nhất
            List<NqtCronLog> keepLogs = nqtCronLogRepository.findTop100ByOrderByNqtCreatedAtDesc();
            
            if (keepLogs.size() == MAX_LOG_COUNT) {
                // Xóa tất cả log cũ hơn log mới nhất trong danh sách giữ lại
                LocalDateTime oldestKeepDate = keepLogs.get(keepLogs.size() - 1).getNqtCreatedAt();
                nqtCronLogRepository.deleteByNqtCreatedAtBefore(oldestKeepDate);
            }
        }
    }

    /**
     * Lấy tất cả log với phân trang
     */
    public Page<NqtCronLog> getAllLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "nqtCreatedAt"));
        return nqtCronLogRepository.findAllByOrderByNqtCreatedAtDesc(pageable);
    }

    /**
     * Lấy log mới nhất
     */
    public List<NqtCronLog> getLatestLogs(int limit) {
        return nqtCronLogRepository.findTop100ByOrderByNqtCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Lấy log theo task name
     */
    public List<NqtCronLog> getLogsByTaskName(String taskName) {
        return nqtCronLogRepository.findByNqtTaskNameOrderByNqtCreatedAtDesc(taskName);
    }

    /**
     * Lấy log theo status
     */
    public List<NqtCronLog> getLogsByStatus(String status) {
        return nqtCronLogRepository.findByNqtStatusOrderByNqtCreatedAtDesc(status);
    }

    /**
     * Xóa log thủ công
     */
    @Transactional
    public void deleteLog(Integer logId) {
        nqtCronLogRepository.deleteById(logId);
    }

    /**
     * Xóa tất cả log
     */
    @Transactional
    public void deleteAllLogs() {
        nqtCronLogRepository.deleteAll();
    }

    /**
     * Lấy thống kê log
     */
    public long getTotalLogCount() {
        return nqtCronLogRepository.count();
    }
}

