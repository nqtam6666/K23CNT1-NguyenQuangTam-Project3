package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class NqtLoginController {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/admin/login")
    public String nqtLogin() {
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String nqtLoginSubmit(@RequestParam("nqtTaiKhoan") String nqtTaiKhoan,
            @RequestParam("nqtMatKhau") String nqtMatKhau,
            HttpSession session,
            Model model) {
        // Tìm kiếm theo Tài khoản hoặc Email
        Optional<NqtNguoiDung> nqtNguoiDungOptional = nqtNguoiDungRepository.findByNqtTaiKhoanOrNqtEmail(nqtTaiKhoan,
                nqtTaiKhoan);

        if (nqtNguoiDungOptional.isPresent()) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungOptional.get();

            // Kiểm tra mật khẩu (ưu tiên BCrypt)
            boolean isMatch = passwordEncoder.matches(nqtMatKhau, nqtNguoiDung.getNqtMatKhau());

            // Nếu không khớp hash, kiểm tra plain text (để hỗ trợ mật khẩu cũ)
            if (!isMatch && nqtNguoiDung.getNqtMatKhau().equals(nqtMatKhau)) {
                isMatch = true;
                // Lazy Migration: Cập nhật mật khẩu sang BCrypt
                nqtNguoiDung.setNqtMatKhau(passwordEncoder.encode(nqtMatKhau));
                nqtNguoiDungRepository.save(nqtNguoiDung);
            }

            if (isMatch) {
                if (nqtNguoiDung.getNqtVaiTro() != null && nqtNguoiDung.getNqtVaiTro() == 99) {
                    session.setAttribute("nqtAdminSession", nqtNguoiDung.getNqtTaiKhoan());
                    return "redirect:/admin";
                } else {
                    model.addAttribute("nqtError", "Bạn không có quyền truy cập Admin!");
                }
            } else {
                model.addAttribute("nqtError", "Sai mật khẩu!");
            }
        } else {
            model.addAttribute("nqtError", "Tài khoản hoặc Email không tồn tại!");
        }

        return "admin/login";
    }

    @GetMapping("/admin/logout")
    public String nqtLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
