package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NqtRoomStatusService {

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    @Autowired
    private NqtCronLogService nqtCronLogService;

    /**
     * Tự động cập nhật trạng thái phòng
     * Chạy mỗi 1 phút
     */
    @Scheduled(cron = "0 * * * * ?") // Mỗi phút (giây 0 của mỗi phút)
    @Transactional
    public void autoUpdateRoomStatus() {
        long startTime = System.currentTimeMillis();
        String taskName = "autoUpdateRoomStatus";
        int roomsUpdated = 0;
        String errorMessage = null;
        
        try {
            LocalDate today = LocalDate.now();
            
            // Lấy tất cả các booking đang active (status = 0 hoặc 1)
            List<NqtDatPhong> activeBookings = nqtDatPhongRepository.findAll();
            
            for (NqtDatPhong booking : activeBookings) {
            NqtPhong room = booking.getNqtPhong();
            if (room == null) continue;
            
            LocalDate ngayDi = booking.getNqtNgayDi();
            LocalDate ngayDen = booking.getNqtNgayDen();
            
            // Nếu ngày đi đã qua (ngày hiện tại > ngày đi)
            if (ngayDi != null && today.isAfter(ngayDi)) {
                // Giải phóng phòng (set về trống)
                if (!room.getNqtStatus()) { // Chỉ update nếu đang bị đặt
                    room.setNqtStatus(true);
                    nqtPhongRepository.save(room);
                    roomsUpdated++;
                }
            }
            // Nếu ngày hiện tại nằm trong khoảng ngày đến - ngày đi
            else if (ngayDen != null && ngayDi != null 
                    && !today.isBefore(ngayDen) && !today.isAfter(ngayDi)) {
                // Phòng đang được sử dụng (set thành đã đặt)
                if (booking.getNqtStatus() == 1 && room.getNqtStatus()) { // Chỉ khi đã thanh toán và phòng đang trống
                    room.setNqtStatus(false);
                    nqtPhongRepository.save(room);
                    roomsUpdated++;
                }
            }
            // Nếu ngày đến chưa đến và booking đã thanh toán
            else if (ngayDen != null && today.isBefore(ngayDen) && booking.getNqtStatus() == 1) {
                // Phòng đã được đặt nhưng chưa đến ngày check-in
                if (room.getNqtStatus()) { // Chỉ update nếu phòng đang trống
                    room.setNqtStatus(false);
                    nqtPhongRepository.save(room);
                    roomsUpdated++;
                }
            }
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            nqtCronLogService.logSuccess(taskName, 
                "Đã cập nhật " + roomsUpdated + " phòng", 
                roomsUpdated, 
                executionTime);
                
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            errorMessage = e.getMessage();
            nqtCronLogService.logError(taskName, 
                "Lỗi khi cập nhật trạng thái phòng: " + errorMessage, 
                getStackTrace(e), 
                executionTime);
        }
    }
    
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Cập nhật trạng thái phòng cho một booking cụ thể
     * Gọi từ các controller khi cần
     */
    @Transactional
    public void updateRoomStatusForBooking(NqtDatPhong booking) {
        if (booking == null || booking.getNqtPhong() == null) {
            return;
        }
        
        NqtPhong room = booking.getNqtPhong();
        LocalDate today = LocalDate.now();
        LocalDate ngayDen = booking.getNqtNgayDen();
        LocalDate ngayDi = booking.getNqtNgayDi();
        
        // Nếu ngày đi đã qua -> giải phóng phòng
        if (ngayDi != null && today.isAfter(ngayDi)) {
            room.setNqtStatus(true);
            nqtPhongRepository.save(room);
            return;
        }
        
        // Nếu booking đã thanh toán và ngày đến chưa đến hoặc đang trong khoảng thời gian
        if (booking.getNqtStatus() == 1) {
            if (ngayDen != null && (today.isBefore(ngayDen) || 
                (ngayDi != null && !today.isBefore(ngayDen) && !today.isAfter(ngayDi)))) {
                room.setNqtStatus(false);
                nqtPhongRepository.save(room);
            }
        }
    }

    /**
     * Kiểm tra và cập nhật tất cả phòng khi có request
     * Có thể gọi từ interceptor hoặc filter
     */
    @Transactional
    public void checkAndUpdateAllRooms() {
        LocalDate today = LocalDate.now();
        
        // Lấy tất cả phòng đang bị đặt
        List<NqtPhong> bookedRooms = nqtPhongRepository.findByNqtStatus(false);
        
        for (NqtPhong room : bookedRooms) {
            // Tìm booking active gần nhất cho phòng này
            List<NqtDatPhong> bookings = nqtDatPhongRepository.findByNqtPhong(room);
            
            boolean shouldBeAvailable = true;
            
            for (NqtDatPhong booking : bookings) {
                LocalDate ngayDi = booking.getNqtNgayDi();
                LocalDate ngayDen = booking.getNqtNgayDen();
                
                // Nếu có booking đang active (đã thanh toán và trong khoảng thời gian)
                if (booking.getNqtStatus() == 1 && ngayDen != null && ngayDi != null) {
                    if (!today.isBefore(ngayDen) && !today.isAfter(ngayDi)) {
                        // Đang trong thời gian sử dụng
                        shouldBeAvailable = false;
                        break;
                    }
                    if (today.isBefore(ngayDen)) {
                        // Chưa đến ngày check-in nhưng đã thanh toán
                        shouldBeAvailable = false;
                        break;
                    }
                }
            }
            
            // Nếu không có booking nào đang active -> giải phóng phòng
            if (shouldBeAvailable) {
                room.setNqtStatus(true);
                nqtPhongRepository.save(room);
            }
        }
    }
}

