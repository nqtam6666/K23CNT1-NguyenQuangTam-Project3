package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BcryptCheckController {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @GetMapping("/check-bcrypt")
    public String checkBcrypt() {
        List<NqtNguoiDung> users = nqtNguoiDungRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>Danh sách tài khoản và mật khẩu (Hash)</h1>");
        sb.append(
                "<p>Truy cập đường dẫn này để kiểm tra xem mật khẩu đã được mã hóa BCrypt chưa (bắt đầu bằng $2a$).</p>");
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        sb.append(
                "<tr style='background-color: #f2f2f2;'><th>ID</th><th>Tài khoản</th><th>Mật khẩu (Hash)</th><th>Trạng thái</th></tr>");

        for (NqtNguoiDung user : users) {
            String pass = user.getNqtMatKhau();
            boolean isBcrypt = pass != null && pass.startsWith("$2a$");
            sb.append("<tr>");
            sb.append("<td style='padding: 8px;'>").append(user.getNqtId()).append("</td>");
            sb.append("<td style='padding: 8px;'>").append(user.getNqtTaiKhoan()).append("</td>");
            sb.append("<td style='padding: 8px; font-family: monospace;'>").append(pass).append("</td>");
            sb.append("<td style='padding: 8px; font-weight: bold; color:").append(isBcrypt ? "green" : "red")
                    .append("'>")
                    .append(isBcrypt ? "Đã mã hóa (BCrypt)" : "Chưa mã hóa (Plain text)").append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
