package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtService.NqtSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NqtGlobalControllerAdvice {

    @Autowired
    private NqtSettingService nqtSettingService;

    @ModelAttribute("nqtWebsiteName")
    public String nqtWebsiteName() {
        return nqtSettingService.getNqtValue("nqtWebsiteName", "Quản lý Khách sạn");
    }

    @ModelAttribute("nqtWebsiteColor")
    public String nqtWebsiteColor() {
        return nqtSettingService.getNqtValue("nqtWebsiteColor", "#4e73df");
    }

    @ModelAttribute("nqtWebsiteLogo")
    public String nqtWebsiteLogo() {
        return nqtSettingService.getNqtValue("nqtWebsiteLogo", "/images/logo.png");
    }

    @ModelAttribute("nqtWebsiteEmail")
    public String nqtWebsiteEmail() {
        return nqtSettingService.getNqtValue("nqtWebsiteEmail", "contact@example.com");
    }

    @ModelAttribute("nqtWebsitePhone")
    public String nqtWebsitePhone() {
        return nqtSettingService.getNqtValue("nqtWebsitePhone", "0123456789");
    }

    @ModelAttribute("nqtTieuDe")
    public String nqtTieuDe() {
        return nqtSettingService.getNqtValue("TieuDe", "Tiêu đề mặc định");
    }
}
