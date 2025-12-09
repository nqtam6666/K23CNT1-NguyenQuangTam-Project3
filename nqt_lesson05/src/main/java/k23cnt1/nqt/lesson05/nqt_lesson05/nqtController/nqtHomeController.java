package k23cnt1.nqt.lesson05.nqt_lesson05.nqtController;
import k23cnt1.nqt.lesson05.nqt_lesson05.nqtEntity.nqtInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
public class nqtHomeController {

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Devmaster::Trang chủ");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // Tạo thông tin profile
        List<nqtInfo> profile = new ArrayList<>();
        profile.add(new nqtInfo(
                null,
                "Nguyễn Quang Tâm",
                "Nam",
                20,
                10000.0
        ));
        // Đưa profile vào model
        model.addAttribute("DevmasterProfile", profile);
        return "profile";
    }
}
