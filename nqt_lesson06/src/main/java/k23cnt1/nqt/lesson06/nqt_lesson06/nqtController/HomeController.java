package k23cnt1.nqt.lesson06.nqt_lesson06.nqtController; // Thay bằng package của bạn

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/students";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home(@AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            return "Chào mừng, " + user.getFullName() + "! Email: " + user.getEmail();
        }
        return "Không có user.";
    }
}