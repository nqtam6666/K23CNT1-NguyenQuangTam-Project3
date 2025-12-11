package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import k23cnt1.nqt.project3.nqtService.NqtSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class NqtGlobalControllerAdvice {

    @Autowired
    private NqtSettingService nqtSettingService;
    
    @Autowired
    private NqtAdminPathService adminPathService;

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
        String value = nqtSettingService.getNqtValue("nqtTieuDe", null);
        if (value == null || value.isEmpty()) {
            value = nqtSettingService.getNqtValue("TieuDe", "Tiêu đề mặc định");
        }
        return value;
    }

    @ModelAttribute("nqtWebsiteFont")
    public String nqtWebsiteFont() {
        return nqtSettingService.getNqtValue("nqtWebsiteFont", "Inter");
    }

    @ModelAttribute("nqtWebsiteAddress")
    public String nqtWebsiteAddress() {
        return nqtSettingService.getNqtValue("nqtWebsiteAddress", "123 Đường ABC, Quận XYZ, Hà Nội, Việt Nam");
    }

    @ModelAttribute("nqtWebsiteFacebook")
    public String nqtWebsiteFacebook() {
        return nqtSettingService.getNqtValue("nqtWebsiteFacebook", "#");
    }

    @ModelAttribute("nqtWebsiteZalo")
    public String nqtWebsiteZalo() {
        return nqtSettingService.getNqtValue("nqtWebsiteZalo", "#");
    }

    @ModelAttribute("nqtWebsiteLink")
    public String nqtWebsiteLink() {
        return nqtSettingService.getNqtValue("nqtWebsiteLink", "#");
    }

    @ModelAttribute("nqtWebsiteFAQ")
    public String nqtWebsiteFAQ() {
        return nqtSettingService.getNqtValue("nqtWebsiteFAQ", "#");
    }

    @ModelAttribute("nqtWebsiteSupportLinks")
    public String nqtWebsiteSupportLinks() {
        return nqtSettingService.getNqtValue("nqtWebsiteSupportLinks", "");
    }

    @ModelAttribute("nqtWebsiteSupportLinksList")
    public List<String[]> nqtWebsiteSupportLinksList() {
        String links = nqtSettingService.getNqtValue("nqtWebsiteSupportLinks", "");
        List<String[]> result = new ArrayList<>();
        if (links != null && !links.isEmpty()) {
            String[] lines = links.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && line.contains("|")) {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        String url = parts[1].trim();
                        
                        // Tạo slug từ URL hoặc tên
                        String slug;
                        if (url.startsWith("/nqtSupport/")) {
                            slug = url.substring("/nqtSupport/".length());
                        } else if (url.startsWith("/")) {
                            slug = url.substring(1).replaceAll("/", "-");
                        } else {
                            // Tạo slug từ tên
                            slug = name.toLowerCase()
                                    .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                                    .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                                    .replaceAll("[ìíịỉĩ]", "i")
                                    .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                                    .replaceAll("[ùúụủũưừứựửữ]", "u")
                                    .replaceAll("[ỳýỵỷỹ]", "y")
                                    .replaceAll("[đ]", "d")
                                    .replaceAll("[^a-z0-9]+", "-")
                                    .replaceAll("^-|-$", "");
                        }
                        
                        if (slug.isEmpty()) {
                            slug = "support-page";
                        }
                        
                        // URL luôn là /nqtSupport/{slug}
                        String finalUrl = "/nqtSupport/" + slug;
                        result.add(new String[]{name, finalUrl});
                    }
                }
            }
        }
        return result;
    }
    
    @ModelAttribute("nqtAdminPath")
    public String nqtAdminPath() {
        return adminPathService.getAdminPathWithSlash();
    }
}
