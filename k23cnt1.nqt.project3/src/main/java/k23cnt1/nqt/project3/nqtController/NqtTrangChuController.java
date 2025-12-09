package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.*;
import k23cnt1.nqt.project3.nqtRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NqtTrangChuController {

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    @Autowired
    private NqtLoaiPhongRepository nqtLoaiPhongRepository;

    @Autowired
    private NqtDichVuRepository nqtDichVuRepository;

    @Autowired
    private NqtBlogRepository nqtBlogRepository;

    @Autowired
    private NqtDanhGiaRepository nqtDanhGiaRepository;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtRoomStatusService nqtRoomStatusService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtEmailService nqtEmailService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtSettingService nqtSettingService;

    @GetMapping({"/", "/nqtTrangChu"})
    public String nqtTrangChu(Model model) {
        // Tự động kiểm tra và cập nhật trạng thái phòng
        nqtRoomStatusService.checkAndUpdateAllRooms();
        // Lấy danh sách phòng nổi bật (status = true, limit 6)
        List<NqtPhong> featuredRooms = nqtPhongRepository.findByNqtStatusOrderByNqtIdDesc(true)
                .stream()
                .limit(6)
                .toList();

        // Lấy danh sách loại phòng
        List<NqtLoaiPhong> roomTypes = nqtLoaiPhongRepository.findByNqtStatus(true);

        // Lấy danh sách dịch vụ
        List<NqtDichVu> services = nqtDichVuRepository.findByNqtStatus(true)
                .stream()
                .limit(4)
                .toList();

        // Lấy tin tức mới nhất
        List<NqtBlog> latestBlogs = nqtBlogRepository.findByNqtStatusOrderByNqtNgayTaoDesc(true)
                .stream()
                .limit(3)
                .toList();

        // Lấy đánh giá đã duyệt cho testimonials
        List<NqtDanhGia> testimonials = nqtDanhGiaRepository.findByNqtStatusOrderByNqtNgayDanhGiaDesc(true)
                .stream()
                .limit(6)
                .toList();

        model.addAttribute("featuredRooms", featuredRooms);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("services", services);
        model.addAttribute("latestBlogs", latestBlogs);
        model.addAttribute("testimonials", testimonials);

        return "nqtCustomer/nqtIndex";
    }

    @GetMapping("/nqtGioiThieu")
    public String nqtGioiThieu(Model model) {
        return "nqtCustomer/nqtGioiThieu";
    }

    @GetMapping("/nqtLienHe")
    public String nqtLienHe(Model model) {
        return "nqtCustomer/nqtLienHe";
    }

    @PostMapping("/nqtLienHe")
    public String nqtLienHeSubmit(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            Model model) {
        try {
            // Validate input
            if (name == null || name.trim().isEmpty()) {
                model.addAttribute("nqtError", "Vui lòng nhập họ và tên!");
                model.addAttribute("email", email);
                model.addAttribute("message", message);
                return "nqtCustomer/nqtLienHe";
            }
            
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                model.addAttribute("nqtError", "Vui lòng nhập email hợp lệ!");
                model.addAttribute("name", name);
                model.addAttribute("message", message);
                return "nqtCustomer/nqtLienHe";
            }
            
            if (message == null || message.trim().isEmpty()) {
                model.addAttribute("nqtError", "Vui lòng nhập tin nhắn!");
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                return "nqtCustomer/nqtLienHe";
            }

            // Get admin email from settings
            String adminEmail = nqtSettingService.getNqtValue("nqtWebsiteEmail", "contact@example.com");
            String websiteName = nqtSettingService.getNqtValue("nqtWebsiteName", "Quản lý Khách sạn");

            // Prepare email content
            String subject = "Tin nhắn liên hệ từ " + websiteName + " - " + name;
            String emailBody = "Bạn nhận được tin nhắn liên hệ từ website:\n\n" +
                    "Họ và tên: " + name + "\n" +
                    "Email: " + email + "\n\n" +
                    "Tin nhắn:\n" +
                    message + "\n\n" +
                    "---\n" +
                    "Email này được gửi tự động từ hệ thống " + websiteName;

            // Send email to admin
            nqtEmailService.sendTextEmail(adminEmail, subject, emailBody);

            // Send confirmation email to user
            String userSubject = "Cảm ơn bạn đã liên hệ với " + websiteName;
            String userBody = "Xin chào " + name + ",\n\n" +
                    "Cảm ơn bạn đã liên hệ với chúng tôi. Chúng tôi đã nhận được tin nhắn của bạn và sẽ phản hồi trong thời gian sớm nhất.\n\n" +
                    "Nội dung tin nhắn của bạn:\n" +
                    message + "\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ " + websiteName;

            nqtEmailService.sendTextEmail(email, userSubject, userBody);

            model.addAttribute("nqtSuccess", "Cảm ơn bạn đã liên hệ! Chúng tôi đã nhận được tin nhắn và sẽ phản hồi sớm nhất có thể.");

        } catch (Exception e) {
            model.addAttribute("nqtError", "Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau hoặc liên hệ trực tiếp qua email/điện thoại.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("message", message);
        }

        return "nqtCustomer/nqtLienHe";
    }
}
