package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtPhongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class NqtPaymentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NqtPaymentScheduler.class);

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtPaymentCheckService nqtPaymentCheckService;

    @Autowired
    private NqtEmailService nqtEmailService;

    @Autowired
    private NqtSettingService nqtSettingService;

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    /**
     * Kiểm tra thanh toán tự động mỗi 30 giây
     * Chỉ kiểm tra các đơn đặt phòng chưa thanh toán (status = 0)
     * CHỈ GỌI API KHI CÓ HÓA ĐƠN CHƯA THANH TOÁN CẦN XỬ LÝ
     */
    @Scheduled(fixedRate = 30000) // 30 seconds = 30000 milliseconds
    public void checkPendingPayments() {
        try {
            logger.info("Bắt đầu kiểm tra thanh toán tự động...");
            
            // Lấy tất cả đơn đặt phòng chưa thanh toán (status = 0)
            List<NqtDatPhong> pendingBookings = nqtDatPhongRepository.findAll().stream()
                    .filter(booking -> booking.getNqtStatus() != null && booking.getNqtStatus() == 0)
                    .filter(booking -> booking.getNqtNoiDungChuyenKhoan() != null && !booking.getNqtNoiDungChuyenKhoan().isEmpty())
                    .filter(booking -> booking.getNqtTongTien() != null && booking.getNqtTongTien() > 0)
                    .toList();

            logger.info("Tìm thấy {} đơn đặt phòng chưa thanh toán", pendingBookings.size());

            // NẾU KHÔNG CÓ HÓA ĐƠN CHƯA THANH TOÁN, KHÔNG GỌI API
            if (pendingBookings.isEmpty()) {
                logger.info("Không có hóa đơn chưa thanh toán cần xử lý. Bỏ qua việc gọi API.");
                return;
            }

            // CHỈ KHI CÓ HÓA ĐƠN CHƯA THANH TOÁN MỚI XỬ LÝ
            List<NqtDatPhong> paidBookings = new ArrayList<>();
            int successCount = 0;
            for (NqtDatPhong booking : pendingBookings) {
                try {
                    boolean isPaid = nqtPaymentCheckService.checkPaymentStatus(booking.getNqtId());
                    if (isPaid) {
                        successCount++;
                        // Lấy lại booking từ database để có thông tin mới nhất (status đã được cập nhật)
                        var updatedBooking = nqtDatPhongRepository.findById(booking.getNqtId());
                        if (updatedBooking.isPresent() && updatedBooking.get().getNqtStatus() != null && updatedBooking.get().getNqtStatus() == 1) {
                            paidBookings.add(updatedBooking.get());
                        }
                        logger.info("Đã phát hiện thanh toán cho đơn đặt phòng ID: {}", booking.getNqtId());
                    }
                } catch (Exception e) {
                    logger.error("Lỗi khi kiểm tra thanh toán cho đơn đặt phòng ID: {}", booking.getNqtId(), e);
                }
            }

            logger.info("Hoàn thành kiểm tra thanh toán. Đã xác nhận {} đơn thanh toán thành công.", successCount);

            // Gửi email cảm ơn cho các khách hàng đã thanh toán thành công
            if (successCount > 0 && !paidBookings.isEmpty()) {
                sendThankYouEmails(paidBookings);
            }
        } catch (Exception e) {
            logger.error("Lỗi trong scheduled task kiểm tra thanh toán", e);
        }
    }

    /**
     * Tự động hủy các đơn đặt phòng quá 1 giờ chưa thanh toán
     * Chạy mỗi 5 phút
     */
    @Scheduled(fixedRate = 300000) // 5 phút = 300000 milliseconds
    public void autoCancelExpiredBookings() {
        try {
            logger.info("Bắt đầu kiểm tra và hủy các đơn đặt phòng quá hạn...");
            
            java.time.LocalDateTime oneHourAgo = java.time.LocalDateTime.now().minusHours(1);
            
            // Lấy tất cả đơn đặt phòng chưa thanh toán (status = 0) và đã tạo quá 1 giờ
            List<NqtDatPhong> expiredBookings = nqtDatPhongRepository.findAll().stream()
                    .filter(booking -> booking.getNqtStatus() != null && booking.getNqtStatus() == 0)
                    .filter(booking -> booking.getNqtNgayTao() != null && booking.getNqtNgayTao().isBefore(oneHourAgo))
                    .toList();

            logger.info("Tìm thấy {} đơn đặt phòng quá hạn cần hủy", expiredBookings.size());

            int cancelledCount = 0;
            for (NqtDatPhong booking : expiredBookings) {
                try {
                    // Hủy đơn đặt phòng
                    booking.setNqtStatus((byte) 2); // Đã hủy
                    nqtDatPhongRepository.save(booking);

                    // Giải phóng phòng
                    if (booking.getNqtPhong() != null) {
                        booking.getNqtPhong().setNqtStatus(true); // Trả phòng về trạng thái trống
                        nqtPhongRepository.save(booking.getNqtPhong());
                    }

                    cancelledCount++;
                    logger.info("Đã tự động hủy đơn đặt phòng ID: {} - Quá 1 giờ chưa thanh toán", booking.getNqtId());
                } catch (Exception e) {
                    logger.error("Lỗi khi hủy đơn đặt phòng ID: {}", booking.getNqtId(), e);
                }
            }

            logger.info("Hoàn thành kiểm tra. Đã hủy {} đơn đặt phòng quá hạn.", cancelledCount);
        } catch (Exception e) {
            logger.error("Lỗi trong scheduled task hủy đơn quá hạn", e);
        }
    }

    /**
     * Gửi email cảm ơn cho khách hàng đã thanh toán thành công
     */
    private void sendThankYouEmails(List<NqtDatPhong> paidBookings) {
        String websiteName = nqtSettingService.getNqtValue("nqtWebsiteName", "Hotel NQT");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        
        for (NqtDatPhong booking : paidBookings) {
            try {
                if (booking.getNqtNguoiDung() == null || booking.getNqtNguoiDung().getNqtEmail() == null || booking.getNqtNguoiDung().getNqtEmail().isEmpty()) {
                    logger.warn("Không thể gửi email cảm ơn cho đơn đặt phòng ID: {} - Không có email khách hàng", booking.getNqtId());
                    continue;
                }

                String customerEmail = booking.getNqtNguoiDung().getNqtEmail();
                String customerName = booking.getNqtNguoiDung().getNqtHoVaTen() != null ? booking.getNqtNguoiDung().getNqtHoVaTen() : "Quý khách";
                String roomName = booking.getNqtPhong() != null && booking.getNqtPhong().getNqtTenPhong() != null 
                    ? booking.getNqtPhong().getNqtTenPhong() 
                    : "Phòng";
                String roomNumber = booking.getNqtPhong() != null && booking.getNqtPhong().getNqtSoPhong() != null 
                    ? booking.getNqtPhong().getNqtSoPhong() 
                    : "";
                String checkInDate = booking.getNqtNgayDen() != null 
                    ? booking.getNqtNgayDen().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                    : "";
                String checkOutDate = booking.getNqtNgayDi() != null 
                    ? booking.getNqtNgayDi().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                    : "";
                String totalAmount = booking.getNqtTongTien() != null 
                    ? numberFormat.format(booking.getNqtTongTien()) + " VNĐ" 
                    : "0 VNĐ";

                String subject = "Cảm ơn bạn đã thanh toán - " + websiteName;
                String emailBody = String.format(
                    "Kính gửi %s,\n\n" +
                    "Chúng tôi xin chân thành cảm ơn bạn đã thanh toán thành công cho đơn đặt phòng của bạn.\n\n" +
                    "Thông tin đặt phòng:\n" +
                    "- Mã đặt phòng: #%d\n" +
                    "- Phòng: %s %s\n" +
                    "- Ngày đến: %s\n" +
                    "- Ngày đi: %s\n" +
                    "- Tổng thanh toán: %s\n\n" +
                    "Đơn đặt phòng của bạn đã được xác nhận và sẵn sàng cho kỳ nghỉ của bạn.\n\n" +
                    "Chúng tôi rất mong được đón tiếp bạn tại %s.\n\n" +
                    "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.\n\n" +
                    "Trân trọng,\n" +
                    "%s",
                    customerName,
                    booking.getNqtId(),
                    roomName,
                    roomNumber,
                    checkInDate,
                    checkOutDate,
                    totalAmount,
                    websiteName,
                    websiteName
                );

                nqtEmailService.sendTextEmail(customerEmail, subject, emailBody);
                logger.info("Đã gửi email cảm ơn cho khách hàng: {} (Email: {}) - Đơn đặt phòng ID: {}", 
                    customerName, customerEmail, booking.getNqtId());

            } catch (Exception e) {
                logger.error("Lỗi khi gửi email cảm ơn cho đơn đặt phòng ID: {}", booking.getNqtId(), e);
            }
        }
    }
}

