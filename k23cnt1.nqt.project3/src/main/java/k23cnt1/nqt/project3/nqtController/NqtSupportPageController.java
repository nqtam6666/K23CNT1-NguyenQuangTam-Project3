package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtService.NqtSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NqtSupportPageController {

    @Autowired
    private NqtSettingService nqtSettingService;
    
    /**
     * Test route to verify controller is working
     */
    @GetMapping("/nqtSupport/test")
    public String test() {
        System.out.println("NqtSupportPageController: Test route called");
        return "redirect:/nqtTrangChu";
    }

    /**
     * Parse support links with content
     * Format: "Tên|URL|Nội dung HTML" hoặc "Tên|URL" (nội dung mặc định)
     */
    private Map<String, Map<String, String>> parseSupportPages() {
        String links = nqtSettingService.getNqtValue("nqtWebsiteSupportLinks", "");
        Map<String, Map<String, String>> pages = new HashMap<>();
        
        if (links != null && !links.isEmpty()) {
            String[] lines = links.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && line.contains("|")) {
                    // Split by | và trim từng phần
                    String[] parts = line.split("\\|", 3);
                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        String url = parts[1].trim();
                        String content = parts.length >= 3 ? parts[2].trim() : "";
                        
                        System.out.println("Parsing line: name='" + name + "', url='" + url + "', content length=" + content.length());
                        
                        // Bỏ qua nếu name hoặc url rỗng
                        if (name.isEmpty() || url.isEmpty()) {
                            System.out.println("Skipping: name or url is empty");
                            continue;
                        }
                        
                        // Tạo slug từ URL hoặc tên
                        String slug;
                        if (url.startsWith("/nqtSupport/")) {
                            // Nếu URL đã có format /nqtSupport/{slug}, giữ nguyên slug (có thể có chữ hoa)
                            slug = url.substring("/nqtSupport/".length()).trim();
                            // Chỉ clean khoảng trắng, giữ nguyên chữ hoa/thường và dấu gạch ngang
                            slug = slug.replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9-]", "");
                        } else if (url.startsWith("/")) {
                            // Nếu URL bắt đầu bằng /, lấy phần sau
                            slug = url.substring(1).replaceAll("/", "-").trim();
                            // Clean: giữ chữ hoa/thường, số và dấu gạch ngang
                            slug = slug.replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9-]", "");
                        } else {
                            // Tạo slug từ tên (chuyển thành chữ thường)
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
                        
                        Map<String, String> pageInfo = new HashMap<>();
                        pageInfo.put("name", name);
                        pageInfo.put("url", "/nqtSupport/" + slug);
                        pageInfo.put("content", content);
                        pageInfo.put("slug", slug);
                        
                        pages.put(slug, pageInfo);
                    }
                }
            }
        }
        return pages;
    }

    /**
     * Dynamic route handler for support pages
     * Handles routes like /nqtSupport/{slug}
     */
    @GetMapping("/nqtSupport/{slug}")
    public String nqtSupportPage(@PathVariable String slug, Model model) {
        System.out.println("NqtSupportPageController: Requested slug = " + slug);
        
        Map<String, Map<String, String>> pages = parseSupportPages();
        System.out.println("NqtSupportPageController: Found " + pages.size() + " support pages");
        for (String key : pages.keySet()) {
            System.out.println("  - Slug: " + key + ", Name: " + pages.get(key).get("name"));
        }
        
        Map<String, String> pageInfo = pages.get(slug);
        
        if (pageInfo == null) {
            System.out.println("NqtSupportPageController: Page not found for slug: " + slug);
            // Trang không tồn tại, redirect về trang chủ
            return "redirect:/nqtTrangChu";
        }
        
        System.out.println("NqtSupportPageController: Found page - Name: " + pageInfo.get("name") + ", Content length: " + (pageInfo.get("content") != null ? pageInfo.get("content").length() : 0));
        
        model.addAttribute("pageTitle", pageInfo.get("name"));
        model.addAttribute("pageContent", pageInfo.get("content"));
        model.addAttribute("pageUrl", pageInfo.get("url"));
        
        return "nqtCustomer/nqtSupportPage";
    }

    /**
     * Get all support pages for navigation
     */
    @ModelAttribute("nqtSupportPages")
    public List<Map<String, String>> getSupportPages() {
        Map<String, Map<String, String>> pages = parseSupportPages();
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, String> pageInfo : pages.values()) {
            result.add(pageInfo);
        }
        return result;
    }
}

