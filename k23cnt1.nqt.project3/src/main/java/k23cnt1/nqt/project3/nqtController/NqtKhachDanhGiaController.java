package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtDanhGia;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtDanhGiaRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class NqtKhachDanhGiaController {

    @Autowired
    private NqtDanhGiaRepository nqtDanhGiaRepository;

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    // Form để đánh giá đặt phòng
    @GetMapping("/nqtDanhGia/dat-phong/{datPhongId}")
    public String nqtDanhGiaForm(@PathVariable("datPhongId") Integer datPhongId,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(datPhongId);

        if (bookingOptional.isEmpty()
                || !bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
            model.addAttribute("nqtError", "Không tìm thấy đặt phòng!");
            return "redirect:/nqtDatPhongCuaToi";
        }

        NqtDatPhong booking = bookingOptional.get();

        // Kiểm tra xem đã có đánh giá chưa
        List<NqtDanhGia> existingReviews = nqtDanhGiaRepository.findByNqtDatPhong(booking);
        if (!existingReviews.isEmpty()) {
            model.addAttribute("nqtError", "Bạn đã đánh giá đặt phòng này rồi!");
            return "redirect:/nqtDatPhongCuaToi/" + datPhongId;
        }

        model.addAttribute("booking", booking);
        return "nqtCustomer/nqtDanhGia/nqtForm";
    }

    // Submit đánh giá
    @PostMapping("/nqtDanhGia/dat-phong/{datPhongId}")
    public String nqtDanhGiaSubmit(@PathVariable("datPhongId") Integer datPhongId,
            @RequestParam("nqtNoiDungDanhGia") String nqtNoiDungDanhGia,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(datPhongId);

        if (bookingOptional.isEmpty()
                || !bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
            model.addAttribute("nqtError", "Không tìm thấy đặt phòng!");
            return "redirect:/nqtDatPhongCuaToi";
        }

        NqtDatPhong booking = bookingOptional.get();

        // Kiểm tra xem đã có đánh giá chưa
        List<NqtDanhGia> existingReviews = nqtDanhGiaRepository.findByNqtDatPhong(booking);
        if (!existingReviews.isEmpty()) {
            model.addAttribute("nqtError", "Bạn đã đánh giá đặt phòng này rồi!");
            return "redirect:/nqtDatPhongCuaToi/" + datPhongId;
        }

        // Validate nội dung đánh giá
        if (nqtNoiDungDanhGia == null || nqtNoiDungDanhGia.trim().isEmpty()) {
            model.addAttribute("nqtError", "Vui lòng nhập nội dung đánh giá!");
            model.addAttribute("booking", booking);
            return "nqtCustomer/nqtDanhGia/nqtForm";
        }

        // Tạo đánh giá mới
        NqtDanhGia nqtDanhGia = new NqtDanhGia();
        nqtDanhGia.setNqtDatPhong(booking);
        nqtDanhGia.setNqtNoiDungDanhGia(nqtNoiDungDanhGia.trim());
        nqtDanhGia.setNqtStatus(true); // Mặc định hiển thị
        nqtDanhGia.setNqtNgayDanhGia(LocalDateTime.now());

        nqtDanhGiaRepository.save(nqtDanhGia);

        model.addAttribute("nqtSuccess", "Đánh giá thành công! Cảm ơn bạn đã đánh giá.");
        return "redirect:/nqtDatPhongCuaToi/" + datPhongId;
    }

    // Xem danh sách đánh giá của khách hàng
    @GetMapping("/nqtDanhGiaCuaToi")
    public String nqtDanhGiaCuaToi(HttpSession session, Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        // Lấy tất cả đặt phòng của khách hàng
        List<NqtDatPhong> bookings = nqtDatPhongRepository.findByNqtNguoiDungOrderByNqtIdDesc(nqtCustomerUser);

        // Lấy tất cả đánh giá của các đặt phòng này
        List<NqtDanhGia> reviews = bookings.stream()
                .flatMap(booking -> nqtDanhGiaRepository.findByNqtDatPhong(booking).stream())
                .sorted((r1, r2) -> r2.getNqtNgayDanhGia().compareTo(r1.getNqtNgayDanhGia()))
                .toList();

        model.addAttribute("reviews", reviews);
        return "nqtCustomer/nqtDanhGia/nqtList";
    }
}

