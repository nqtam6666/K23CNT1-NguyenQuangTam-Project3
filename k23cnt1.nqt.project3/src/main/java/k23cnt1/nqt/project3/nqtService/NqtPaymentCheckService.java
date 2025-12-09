package k23cnt1.nqt.project3.nqtService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NqtPaymentCheckService {

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtNganHangService nqtNganHangService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String PAYMENT_API_URL = "https://clonemmo.com/cron/auto_bank_v2/response.json";

    /**
     * Kiểm tra xem đơn đặt phòng đã được thanh toán chưa
     * @param bookingId ID đơn đặt phòng
     * @return true nếu đã thanh toán, false nếu chưa
     */
    public boolean checkPaymentStatus(Integer bookingId) {
        try {
            // Lấy thông tin booking
            var bookingOptional = nqtDatPhongRepository.findById(bookingId);
            if (bookingOptional.isEmpty()) {
                return false;
            }

            var booking = bookingOptional.get();
            
            // Nếu đã thanh toán rồi thì return true
            if (booking.getNqtStatus() != null && booking.getNqtStatus() == 1) {
                return true;
            }

            // Lấy nội dung chuyển khoản và số tiền
            String noiDungChuyenKhoan = booking.getNqtNoiDungChuyenKhoan();
            Float tongTien = booking.getNqtTongTien();

            if (noiDungChuyenKhoan == null || noiDungChuyenKhoan.isEmpty() || tongTien == null) {
                return false;
            }

            // Lấy danh sách số tài khoản đang hoạt động
            var nganHangList = nqtNganHangService.nqtGetActive();
            java.util.Set<String> activeAccountNumbers = new java.util.HashSet<>();
            for (var nganHang : nganHangList) {
                if (nganHang.getNqtSoTaiKhoan() != null && !nganHang.getNqtSoTaiKhoan().isEmpty()) {
                    activeAccountNumbers.add(nganHang.getNqtSoTaiKhoan());
                }
            }

            if (activeAccountNumbers.isEmpty()) {
                return false;
            }

            // Gọi API để lấy lịch sử giao dịch
            String response = restTemplate.getForObject(PAYMENT_API_URL, String.class);
            if (response == null) {
                return false;
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode == null || !dataNode.isArray()) {
                return false;
            }

            // Duyệt qua các giao dịch
            for (JsonNode transaction : dataNode) {
                // Chỉ xét giao dịch tiền vào
                if (!"IN".equals(transaction.get("type").asText())) {
                    continue;
                }

                // Kiểm tra số tài khoản
                String accountNumber = String.valueOf(transaction.get("account").asLong());
                if (!activeAccountNumbers.contains(accountNumber)) {
                    continue;
                }

                // Kiểm tra số tiền (cho phép sai số nhỏ do làm tròn)
                double transactionAmount = transaction.get("amount").asDouble();
                double bookingAmount = tongTien.doubleValue();
                double difference = Math.abs(transactionAmount - bookingAmount);
                
                // Cho phép sai số 1000 VNĐ
                if (difference > 1000) {
                    continue;
                }

                // Kiểm tra nội dung chuyển khoản trong description
                String description = transaction.get("description").asText("");
                if (description != null && !description.isEmpty()) {
                    // Tìm kiếm nội dung chuyển khoản trong description (case-insensitive)
                    String descriptionUpper = description.toUpperCase();
                    String noiDungUpper = noiDungChuyenKhoan.toUpperCase();
                    
                    if (descriptionUpper.contains(noiDungUpper)) {
                        // Tìm thấy giao dịch khớp, cập nhật trạng thái
                        booking.setNqtStatus((byte) 1); // Đã thanh toán
                        nqtDatPhongRepository.save(booking);
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

