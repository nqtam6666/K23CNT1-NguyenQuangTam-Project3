package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.*;
import k23cnt1.nqt.project3.nqtRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
