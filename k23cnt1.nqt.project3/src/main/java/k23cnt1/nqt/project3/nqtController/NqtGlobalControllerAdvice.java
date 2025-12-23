package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import k23cnt1.nqt.project3.nqtService.NqtSettingService;
import k23cnt1.nqt.project3.nqtService.NqtGiamGiaService;
import k23cnt1.nqt.project3.nqtDto.NqtGiamGiaResponse;
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
    
    @Autowired
    private NqtGiamGiaService nqtGiamGiaService;

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

    @ModelAttribute("nqtCustomerFontBody")
    public String nqtCustomerFontBody() {
        // Nếu chưa có setting riêng, dùng nqtWebsiteFont làm mặc định
        String customFont = nqtSettingService.getNqtValue("nqtCustomerFontBody", null);
        if (customFont == null || customFont.isEmpty()) {
            return nqtSettingService.getNqtValue("nqtWebsiteFont", "Inter");
        }
        return customFont;
    }

    @ModelAttribute("nqtCustomerFontHeading")
    public String nqtCustomerFontHeading() {
        // Mặc định dùng Playfair Display cho heading
        return nqtSettingService.getNqtValue("nqtCustomerFontHeading", "Playfair Display");
    }

    @ModelAttribute("nqtCustomerFontSerif")
    public String nqtCustomerFontSerif() {
        // Mặc định dùng Playfair Display cho serif
        return nqtSettingService.getNqtValue("nqtCustomerFontSerif", "Playfair Display");
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

    @ModelAttribute("nqtBannerImages")
    public List<String> nqtBannerImages() {
        String bannerImagesJson = nqtSettingService.getNqtValue("nqtBannerImages", "[]");
        List<String> bannerImagesList = new ArrayList<>();
        if (bannerImagesJson != null && !bannerImagesJson.trim().isEmpty() && !bannerImagesJson.equals("[]")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                bannerImagesList = objectMapper.readValue(bannerImagesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                // If JSON parsing fails, return empty list
            }
        }
        return bannerImagesList;
    }

    @ModelAttribute("nqtActiveVouchers")
    public List<NqtGiamGiaResponse> nqtActiveVouchers() {
        try {
            return nqtGiamGiaService.nqtGetActiveVouchers();
        } catch (Exception e) {
            // Return empty list if error
            return new ArrayList<>();
        }
    }

    // Popup Settings
    @ModelAttribute("nqtPopupEnabled")
    public String nqtPopupEnabled() {
        return nqtSettingService.getNqtValue("nqtPopupEnabled", "true");
    }

    @ModelAttribute("nqtPopupTitle")
    public String nqtPopupTitle() {
        String popupTitle = nqtSettingService.getNqtValue("nqtPopupTitle", "");
        if (popupTitle == null || popupTitle.trim().isEmpty()) {
            String websiteName = nqtSettingService.getNqtValue("nqtWebsiteName", "Hotel NQT");
            return "🎉 Chào mừng đến với " + websiteName + "!";
        }
        return popupTitle;
    }

    @ModelAttribute("nqtPopupContent")
    public String nqtPopupContent() {
        return nqtSettingService.getNqtValue("nqtPopupContent", "Chúng tôi rất vui được chào đón bạn đến với khách sạn của chúng tôi. Hãy tận hưởng những ưu đãi độc quyền ngay hôm nay!");
    }

    @ModelAttribute("nqtPopupOfferText")
    public String nqtPopupOfferText() {
        return nqtSettingService.getNqtValue("nqtPopupOfferText", "Giảm 20%");
    }

    @ModelAttribute("nqtPopupOfferDesc")
    public String nqtPopupOfferDesc() {
        return nqtSettingService.getNqtValue("nqtPopupOfferDesc", "Cho đơn đặt phòng đầu tiên");
    }

    @ModelAttribute("nqtPopupButtonText")
    public String nqtPopupButtonText() {
        return nqtSettingService.getNqtValue("nqtPopupButtonText", "Đặt phòng ngay");
    }

    @ModelAttribute("nqtPopupButtonLink")
    public String nqtPopupButtonLink() {
        return nqtSettingService.getNqtValue("nqtPopupButtonLink", "#booking");
    }
}
