package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.*;
import k23cnt1.nqt.project3.nqtService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import java.nio.file.*;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

@Controller
public class NqtAdminController {

    @Autowired
    private NqtNguoiDungService nqtNguoiDungService;

    @Autowired
    private NqtLoaiPhongService nqtLoaiPhongService;

    @Autowired
    private NqtPhongService nqtPhongService;

    @Autowired
    private NqtDatPhongService nqtDatPhongService;

    @Autowired
    private NqtDichVuService nqtDichVuService;

    @Autowired
    private NqtDonGiaDichVuService nqtDonGiaDichVuService;

    @Autowired
    private NqtDanhGiaService nqtDanhGiaService;

    @Autowired
    private NqtBlogService nqtBlogService;

    @Autowired
    private NqtSettingService nqtSettingService;
    
    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtAdminPathService adminPathService;

    @Autowired
    private NqtReportService nqtReportService;

    @Autowired
    private NqtGiamGiaService nqtGiamGiaService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtCronLogService nqtCronLogService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtNganHangService nqtNganHangService;

    // Global attributes for all admin pages
    @ModelAttribute
    public void globalSettings(Model model) {
        model.addAttribute("nqtWebsiteName", nqtSettingService.getNqtValue("nqtWebsiteName", "Quản lý Khách sạn"));
        model.addAttribute("nqtWebsiteColor", nqtSettingService.getNqtValue("nqtWebsiteColor", "#4e73df"));
        model.addAttribute("nqtWebsiteFont", nqtSettingService.getNqtValue("nqtWebsiteFont", "Nunito"));
        model.addAttribute("nqtWebsiteLogo", nqtSettingService.getNqtValue("nqtWebsiteLogo", ""));
    }

    @ModelAttribute
    public void addCurrentURI(jakarta.servlet.http.HttpServletRequest request, Model model) {
        model.addAttribute("nqtCurrentURI", request.getRequestURI());
    }

    // ... (rest of the file until configuration section)

    @GetMapping({ "/admin", "/admin/", "/admin/dashboard" })
    public String nqtDashboard(Model model) {
        System.out.println("NqtAdminController: nqtDashboard called");

        List<NqtDatPhongResponse> allBookings = nqtDatPhongService.nqtGetAll();

        // 1. Calculate Total Revenue (All time)
        Double nqtTongDoanhThu = 0.0;
        for (NqtDatPhongResponse dp : allBookings) {
            if (dp.getNqtStatus() == 1 && dp.getNqtTongTien() != null) { // 1: Paid
                nqtTongDoanhThu += dp.getNqtTongTien();
            }
        }
        model.addAttribute("nqtTongDoanhThu", nqtTongDoanhThu);

        // 2. Calculate Revenue Growth (Current Month vs Last Month)
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        int lastMonth = now.minusMonths(1).getMonthValue();
        int lastMonthYear = now.minusMonths(1).getYear();

        Double currentMonthRevenue = 0.0;
        Double lastMonthRevenue = 0.0;
        int newBookingsCount = 0;

        for (NqtDatPhongResponse dp : allBookings) {
            if (dp.getNqtNgayDen() != null) {
                int bookingMonth = dp.getNqtNgayDen().getMonthValue();
                int bookingYear = dp.getNqtNgayDen().getYear();

                // Revenue for Current Month
                if (bookingMonth == currentMonth && bookingYear == currentYear && dp.getNqtStatus() == 1
                        && dp.getNqtTongTien() != null) {
                    currentMonthRevenue += dp.getNqtTongTien();
                }

                // Revenue for Last Month
                if (bookingMonth == lastMonth && bookingYear == lastMonthYear && dp.getNqtStatus() == 1
                        && dp.getNqtTongTien() != null) {
                    lastMonthRevenue += dp.getNqtTongTien();
                }

                // Count New Bookings (Current Month)
                if (bookingMonth == currentMonth && bookingYear == currentYear) {
                    newBookingsCount++;
                }
            }
        }

        double growthPercentage = 0.0;
        if (lastMonthRevenue > 0) {
            growthPercentage = ((currentMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100;
        } else if (currentMonthRevenue > 0) {
            growthPercentage = 100.0; // 100% growth if last month was 0 and this month > 0
        }

        model.addAttribute("nqtRevenueGrowth", (int) growthPercentage);
        model.addAttribute("nqtNewBookingsCount", newBookingsCount);

        // 3. Calculate Monthly Revenue (Current Year)
        Double[] nqtMonthlyRevenue = new Double[12];
        for (int i = 0; i < 12; i++)
            nqtMonthlyRevenue[i] = 0.0;

        for (NqtDatPhongResponse dp : allBookings) {
            if (dp.getNqtStatus() == 1 && dp.getNqtTongTien() != null && dp.getNqtNgayDen() != null) {
                if (dp.getNqtNgayDen().getYear() == currentYear) {
                    int monthIndex = dp.getNqtNgayDen().getMonthValue() - 1; // 0-11
                    nqtMonthlyRevenue[monthIndex] += dp.getNqtTongTien();
                }
            }
        }
        model.addAttribute("nqtMonthlyRevenue", nqtMonthlyRevenue);

        // 4. Calculate Room Occupancy (Real-time)
        List<k23cnt1.nqt.project3.nqtDto.NqtPhongResponse> rooms = nqtPhongService.nqtGetAll();
        int nqtTotalRooms = rooms.size();
        int nqtOccupiedRooms = 0;

        for (NqtDatPhongResponse dp : allBookings) {
            if (dp.getNqtStatus() != 2 && dp.getNqtNgayDen() != null && dp.getNqtNgayDi() != null) { // Not Cancelled
                // Check if Today is within [NgayDen, NgayDi]
                if (!now.isBefore(dp.getNqtNgayDen()) && !now.isAfter(dp.getNqtNgayDi())) {
                    nqtOccupiedRooms++;
                }
            }
        }
        int nqtVacantRooms = nqtTotalRooms - nqtOccupiedRooms;
        if (nqtVacantRooms < 0)
            nqtVacantRooms = 0; // Safety check

        model.addAttribute("nqtOccupiedRooms", nqtOccupiedRooms);
        model.addAttribute("nqtVacantRooms", nqtVacantRooms);

        model.addAttribute("nqtNguoiDungList", nqtNguoiDungService.nqtGetAll());
        model.addAttribute("nqtPhongList", nqtPhongService.nqtGetAll());
        model.addAttribute("nqtDatPhongList", nqtDatPhongService.nqtGetAll());
        model.addAttribute("nqtDichVuList", nqtDichVuService.nqtGetAll());
        return "admin/dashboard";
    }

    // ========== NGƯỜI DÙNG ==========
    @GetMapping("/admin/nguoi-dung")
    public String nqtNguoiDungList(Model model) {
        List<NqtNguoiDungResponse> nqtList = nqtNguoiDungService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/nguoi-dung/list";
    }

    @GetMapping("/admin/nguoi-dung/create")
    public String nqtNguoiDungCreateForm(Model model) {
        model.addAttribute("nqtRequest", new NqtNguoiDungRequest());
        return "admin/nguoi-dung/form";
    }

    @PostMapping("/admin/nguoi-dung/create")
    public String nqtNguoiDungCreate(@ModelAttribute NqtNguoiDungRequest nqtRequest,
            @RequestParam(value = "nqtAvatarFile", required = false) MultipartFile nqtAvatarFile,
            RedirectAttributes redirectAttributes) {
        try {
            // Handle avatar upload
            if (nqtAvatarFile != null && !nqtAvatarFile.isEmpty()) {
                String avatarPath = saveFile(nqtAvatarFile);
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    nqtRequest.setNqtAvatar(avatarPath);
                }
            }
            nqtNguoiDungService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    @GetMapping("/admin/nguoi-dung/edit/{nqtId}")
    public String nqtNguoiDungEditForm(@PathVariable Integer nqtId, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        NqtNguoiDungResponse nqtResponse = nqtNguoiDungService.nqtGetById(nqtId);

        // Check permission
        NqtNguoiDung currentUser = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
        if (currentUser != null && currentUser.getNqtVaiTro() != 99 && nqtResponse.getNqtVaiTro() == 99) {
            redirectAttributes.addFlashAttribute("nqtError", "Bạn không được phép chỉnh sửa tài khoản Admin!");
            return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
        }
        NqtNguoiDungRequest nqtRequest = new NqtNguoiDungRequest();
        nqtRequest.setNqtHoVaTen(nqtResponse.getNqtHoVaTen());
        nqtRequest.setNqtTaiKhoan(nqtResponse.getNqtTaiKhoan());
        nqtRequest.setNqtSoDienThoai(nqtResponse.getNqtSoDienThoai());
        nqtRequest.setNqtEmail(nqtResponse.getNqtEmail());
        nqtRequest.setNqtDiaChi(nqtResponse.getNqtDiaChi());
        nqtRequest.setNqtVaiTro(nqtResponse.getNqtVaiTro());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtCapBac(nqtResponse.getNqtCapBac());
        nqtRequest.setNqtAvatar(nqtResponse.getNqtAvatar());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/nguoi-dung/form";
    }

    @PostMapping("/admin/nguoi-dung/edit/{nqtId}")
    public String nqtNguoiDungUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtNguoiDungRequest nqtRequest,
            @RequestParam(value = "nqtAvatarFile", required = false) MultipartFile nqtAvatarFile,
            RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            // Check permission
            NqtNguoiDung currentUser = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
            NqtNguoiDungResponse targetUser = nqtNguoiDungService.nqtGetById(nqtId);
            if (currentUser != null && currentUser.getNqtVaiTro() != 99 && targetUser.getNqtVaiTro() == 99) {
                redirectAttributes.addFlashAttribute("nqtError", "Bạn không được phép chỉnh sửa tài khoản Admin!");
                return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
            }
            // Handle avatar upload
            if (nqtAvatarFile != null && !nqtAvatarFile.isEmpty()) {
                String avatarPath = saveFile(nqtAvatarFile);
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    nqtRequest.setNqtAvatar(avatarPath);
                }
            }
            nqtNguoiDungService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    @GetMapping("/admin/nguoi-dung/delete/{nqtId}")
    public String nqtNguoiDungDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            // Check permission
            NqtNguoiDung currentUser = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
            NqtNguoiDungResponse targetUser = nqtNguoiDungService.nqtGetById(nqtId);
            if (currentUser != null && currentUser.getNqtVaiTro() != 99 && targetUser.getNqtVaiTro() == 99) {
                redirectAttributes.addFlashAttribute("nqtError", "Bạn không được phép xóa tài khoản Admin!");
                return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
            }
            nqtNguoiDungService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    // Bulk Actions for Người dùng
    @PostMapping("/admin/nguoi-dung/bulk-activate")
    public String nqtNguoiDungBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtNguoiDungResponse user = nqtNguoiDungService.nqtGetById(id);
                    NqtNguoiDungRequest request = new NqtNguoiDungRequest();
                    request.setNqtHoVaTen(user.getNqtHoVaTen());
                    request.setNqtTaiKhoan(user.getNqtTaiKhoan());
                    request.setNqtSoDienThoai(user.getNqtSoDienThoai());
                    request.setNqtEmail(user.getNqtEmail());
                    request.setNqtDiaChi(user.getNqtDiaChi());
                    request.setNqtVaiTro(user.getNqtVaiTro());
                    request.setNqtStatus(true);
                    request.setNqtCapBac(user.getNqtCapBac());
                    nqtNguoiDungService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã kích hoạt " + count + " người dùng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung/bulk-deactivate")
    public String nqtNguoiDungBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtNguoiDungResponse user = nqtNguoiDungService.nqtGetById(id);
                    NqtNguoiDungRequest request = new NqtNguoiDungRequest();
                    request.setNqtHoVaTen(user.getNqtHoVaTen());
                    request.setNqtTaiKhoan(user.getNqtTaiKhoan());
                    request.setNqtSoDienThoai(user.getNqtSoDienThoai());
                    request.setNqtEmail(user.getNqtEmail());
                    request.setNqtDiaChi(user.getNqtDiaChi());
                    request.setNqtVaiTro(user.getNqtVaiTro());
                    request.setNqtStatus(false);
                    request.setNqtCapBac(user.getNqtCapBac());
                    nqtNguoiDungService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã vô hiệu hóa " + count + " người dùng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung/bulk-delete")
    public String nqtNguoiDungBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            NqtNguoiDung currentUser = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
            int count = 0;
            int skipped = 0;
            for (Integer id : ids) {
                try {
                    // Prevent deleting own account or admin accounts (if not super admin)
                    if (currentUser != null && currentUser.getNqtId().equals(id)) {
                        skipped++;
                        continue;
                    }
                    NqtNguoiDungResponse user = nqtNguoiDungService.nqtGetById(id);
                    if (currentUser != null && currentUser.getNqtVaiTro() != 99 && user.getNqtVaiTro() == 99) {
                        skipped++;
                        continue;
                    }
                    nqtNguoiDungService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    skipped++;
                }
            }
            String message = "Đã xóa " + count + " người dùng!";
            if (skipped > 0) {
                message += " (" + skipped + " mục bị bỏ qua)";
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/nguoi-dung";
    }

    // ========== LOẠI PHÒNG ==========
    @GetMapping("/admin/loai-phong")
    public String nqtLoaiPhongList(Model model) {
        List<NqtLoaiPhongResponse> nqtList = nqtLoaiPhongService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/loai-phong/list";
    }

    @GetMapping("/admin/loai-phong/create")
    public String nqtLoaiPhongCreateForm(Model model) {
        model.addAttribute("nqtRequest", new NqtLoaiPhongRequest());
        return "admin/loai-phong/form";
    }

    @PostMapping("/admin/loai-phong/create")
    public String nqtLoaiPhongCreate(@ModelAttribute NqtLoaiPhongRequest nqtRequest,
            @RequestParam("nqtImage") MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtLoaiPhongService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo loại phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    @GetMapping("/admin/loai-phong/edit/{nqtId}")
    public String nqtLoaiPhongEditForm(@PathVariable Integer nqtId, Model model) {
        NqtLoaiPhongResponse nqtResponse = nqtLoaiPhongService.nqtGetById(nqtId);
        NqtLoaiPhongRequest nqtRequest = new NqtLoaiPhongRequest();
        nqtRequest.setNqtTenLoaiPhong(nqtResponse.getNqtTenLoaiPhong());
        nqtRequest.setNqtGia(nqtResponse.getNqtGia());
        nqtRequest.setNqtSoNguoi(nqtResponse.getNqtSoNguoi());
        nqtRequest.setNqtHinhAnh(nqtResponse.getNqtHinhAnh());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtMetaTitle(nqtResponse.getNqtMetaTitle());
        nqtRequest.setNqtMetaKeyword(nqtResponse.getNqtMetaKeyword());
        nqtRequest.setNqtMetaDescription(nqtResponse.getNqtMetaDescription());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/loai-phong/form";
    }

    @PostMapping("/admin/loai-phong/edit/{nqtId}")
    public String nqtLoaiPhongUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtLoaiPhongRequest nqtRequest,
            @RequestParam("nqtImage") MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtLoaiPhongService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    @GetMapping("/admin/loai-phong/delete/{nqtId}")
    public String nqtLoaiPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtLoaiPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    // Bulk Actions for Loại phòng
    @PostMapping("/admin/loai-phong/bulk-activate")
    public String nqtLoaiPhongBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtLoaiPhongResponse loaiPhong = nqtLoaiPhongService.nqtGetById(id);
                    NqtLoaiPhongRequest request = new NqtLoaiPhongRequest();
                    request.setNqtTenLoaiPhong(loaiPhong.getNqtTenLoaiPhong());
                    request.setNqtGia(loaiPhong.getNqtGia());
                    request.setNqtSoNguoi(loaiPhong.getNqtSoNguoi());
                    request.setNqtHinhAnh(loaiPhong.getNqtHinhAnh());
                    request.setNqtStatus(true);
                    request.setNqtMetaTitle(loaiPhong.getNqtMetaTitle());
                    request.setNqtMetaKeyword(loaiPhong.getNqtMetaKeyword());
                    request.setNqtMetaDescription(loaiPhong.getNqtMetaDescription());
                    nqtLoaiPhongService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã kích hoạt " + count + " loại phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    @PostMapping("/admin/loai-phong/bulk-deactivate")
    public String nqtLoaiPhongBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtLoaiPhongResponse loaiPhong = nqtLoaiPhongService.nqtGetById(id);
                    NqtLoaiPhongRequest request = new NqtLoaiPhongRequest();
                    request.setNqtTenLoaiPhong(loaiPhong.getNqtTenLoaiPhong());
                    request.setNqtGia(loaiPhong.getNqtGia());
                    request.setNqtSoNguoi(loaiPhong.getNqtSoNguoi());
                    request.setNqtHinhAnh(loaiPhong.getNqtHinhAnh());
                    request.setNqtStatus(false);
                    request.setNqtMetaTitle(loaiPhong.getNqtMetaTitle());
                    request.setNqtMetaKeyword(loaiPhong.getNqtMetaKeyword());
                    request.setNqtMetaDescription(loaiPhong.getNqtMetaDescription());
                    nqtLoaiPhongService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã vô hiệu hóa " + count + " loại phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    @PostMapping("/admin/loai-phong/bulk-delete")
    public String nqtLoaiPhongBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtLoaiPhongService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " loại phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/loai-phong";
    }

    // ========== PHÒNG ==========
    @GetMapping("/admin/phong")
    public String nqtPhongList(Model model) {
        List<NqtPhongResponse> nqtList = nqtPhongService.nqtGetAll();
        List<NqtLoaiPhongResponse> nqtLoaiPhongList = nqtLoaiPhongService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        model.addAttribute("nqtLoaiPhongList", nqtLoaiPhongList);
        return "admin/phong/list";
    }

    @GetMapping("/admin/phong/create")
    public String nqtPhongCreateForm(Model model) {
        List<NqtLoaiPhongResponse> nqtLoaiPhongList = nqtLoaiPhongService.nqtGetAll();
        model.addAttribute("nqtRequest", new NqtPhongRequest());
        model.addAttribute("nqtLoaiPhongList", nqtLoaiPhongList);
        return "admin/phong/form";
    }

    @PostMapping("/admin/phong/create")
    public String nqtPhongCreate(@ModelAttribute NqtPhongRequest nqtRequest, RedirectAttributes redirectAttributes) {
        try {
            nqtPhongService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    @GetMapping("/admin/phong/edit/{nqtId}")
    public String nqtPhongEditForm(@PathVariable Integer nqtId, Model model) {
        NqtPhongResponse nqtResponse = nqtPhongService.nqtGetById(nqtId);
        List<NqtLoaiPhongResponse> nqtLoaiPhongList = nqtLoaiPhongService.nqtGetAll();
        NqtPhongRequest nqtRequest = new NqtPhongRequest();
        nqtRequest.setNqtSoPhong(nqtResponse.getNqtSoPhong());
        nqtRequest.setNqtTenPhong(nqtResponse.getNqtTenPhong());
        nqtRequest.setNqtLoaiPhongId(nqtResponse.getNqtLoaiPhongId());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtLoaiPhongList", nqtLoaiPhongList);
        model.addAttribute("nqtId", nqtId);
        return "admin/phong/form";
    }

    @PostMapping("/admin/phong/edit/{nqtId}")
    public String nqtPhongUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtPhongRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtPhongService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    @GetMapping("/admin/phong/delete/{nqtId}")
    public String nqtPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    // Bulk Actions for Phòng
    @PostMapping("/admin/phong/bulk-activate")
    public String nqtPhongBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtPhongResponse room = nqtPhongService.nqtGetById(id);
                    NqtPhongRequest request = new NqtPhongRequest();
                    request.setNqtSoPhong(room.getNqtSoPhong());
                    request.setNqtTenPhong(room.getNqtTenPhong());
                    request.setNqtLoaiPhongId(room.getNqtLoaiPhongId());
                    request.setNqtStatus(true);
                    nqtPhongService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã đặt trống " + count + " phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    @PostMapping("/admin/phong/bulk-deactivate")
    public String nqtPhongBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtPhongResponse room = nqtPhongService.nqtGetById(id);
                    NqtPhongRequest request = new NqtPhongRequest();
                    request.setNqtSoPhong(room.getNqtSoPhong());
                    request.setNqtTenPhong(room.getNqtTenPhong());
                    request.setNqtLoaiPhongId(room.getNqtLoaiPhongId());
                    request.setNqtStatus(false);
                    nqtPhongService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã đặt đã đặt " + count + " phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    @PostMapping("/admin/phong/bulk-delete")
    public String nqtPhongBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtPhongService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/phong";
    }

    // ========== ĐẶT PHÒNG ==========
    @GetMapping("/admin/dat-phong")
    public String nqtDatPhongList(Model model) {
        List<NqtDatPhongResponse> nqtList = nqtDatPhongService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/dat-phong/list";
    }

    @GetMapping("/admin/dat-phong/create")
    public String nqtDatPhongCreateForm(Model model) {
        List<NqtNguoiDungResponse> nqtNguoiDungList = nqtNguoiDungService.nqtGetAll();
        List<NqtPhongResponse> nqtPhongList = nqtPhongService.nqtGetAll();
        List<NqtGiamGiaResponse> nqtGiamGiaList = nqtGiamGiaService.nqtGetAll();
        model.addAttribute("nqtRequest", new NqtDatPhongRequest());
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        model.addAttribute("nqtPhongList", nqtPhongList);
        model.addAttribute("nqtGiamGiaList", nqtGiamGiaList);
        return "admin/dat-phong/form";
    }

    @PostMapping("/admin/dat-phong/create")
    public String nqtDatPhongCreate(@ModelAttribute NqtDatPhongRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDatPhongService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo đặt phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dat-phong";
    }

    @GetMapping("/admin/dat-phong/edit/{nqtId}")
    public String nqtDatPhongEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDatPhongResponse nqtResponse = nqtDatPhongService.nqtGetById(nqtId);
        List<NqtNguoiDungResponse> nqtNguoiDungList = nqtNguoiDungService.nqtGetAll();
        List<NqtPhongResponse> nqtPhongList = nqtPhongService.nqtGetAll();
        List<NqtGiamGiaResponse> nqtGiamGiaList = nqtGiamGiaService.nqtGetAll();
        NqtDatPhongRequest nqtRequest = new NqtDatPhongRequest();
        nqtRequest.setNqtNguoiDungId(nqtResponse.getNqtNguoiDungId());
        nqtRequest.setNqtPhongId(nqtResponse.getNqtPhongId());
        nqtRequest.setNqtNgayDen(nqtResponse.getNqtNgayDen());
        nqtRequest.setNqtNgayDi(nqtResponse.getNqtNgayDi());
        nqtRequest.setNqtTongTien(nqtResponse.getNqtTongTien());
        nqtRequest.setNqtGiamGia(nqtResponse.getNqtGiamGia());
        nqtRequest.setNqtGiamGiaId(nqtResponse.getNqtGiamGiaId());
        nqtRequest.setNqtGhiChu(nqtResponse.getNqtGhiChu());
        nqtRequest.setNqtNoiDungChuyenKhoan(nqtResponse.getNqtNoiDungChuyenKhoan());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        model.addAttribute("nqtPhongList", nqtPhongList);
        model.addAttribute("nqtGiamGiaList", nqtGiamGiaList);
        model.addAttribute("nqtId", nqtId);
        return "admin/dat-phong/form";
    }

    @PostMapping("/admin/dat-phong/edit/{nqtId}")
    public String nqtDatPhongUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtDatPhongRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDatPhongService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dat-phong";
    }

    @GetMapping("/admin/dat-phong/delete/{nqtId}")
    public String nqtDatPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDatPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dat-phong";
    }

    // Bulk Actions for Đặt phòng
    @PostMapping("/admin/dat-phong/bulk-delete")
    public String nqtDatPhongBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtDatPhongService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " đặt phòng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dat-phong";
    }

    // ========== DỊCH VỤ ==========
    @GetMapping("/admin/dich-vu")
    public String nqtDichVuList(Model model) {
        List<NqtDichVuResponse> nqtList = nqtDichVuService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/dich-vu/list";
    }

    @GetMapping("/admin/dich-vu/create")
    public String nqtDichVuCreateForm(Model model) {
        model.addAttribute("nqtRequest", new NqtDichVuRequest());
        return "admin/dich-vu/form";
    }

    @PostMapping("/admin/dich-vu/create")
    public String nqtDichVuCreate(@ModelAttribute NqtDichVuRequest nqtRequest,
            @RequestParam("nqtImage") MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtDichVuService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    @GetMapping("/admin/dich-vu/edit/{nqtId}")
    public String nqtDichVuEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDichVuResponse nqtResponse = nqtDichVuService.nqtGetById(nqtId);
        NqtDichVuRequest nqtRequest = new NqtDichVuRequest();
        nqtRequest.setNqtTen(nqtResponse.getNqtTen());
        nqtRequest.setNqtDonGia(nqtResponse.getNqtDonGia());
        nqtRequest.setNqtHinhAnh(nqtResponse.getNqtHinhAnh());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtMetaTitle(nqtResponse.getNqtMetaTitle());
        nqtRequest.setNqtMetaKeyword(nqtResponse.getNqtMetaKeyword());
        nqtRequest.setNqtMetaDescription(nqtResponse.getNqtMetaDescription());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/dich-vu/form";
    }

    @PostMapping("/admin/dich-vu/edit/{nqtId}")
    public String nqtDichVuUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtDichVuRequest nqtRequest,
            @RequestParam(value = "nqtImage", required = false) MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtDichVuService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    @GetMapping("/admin/dich-vu/delete/{nqtId}")
    public String nqtDichVuDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDichVuService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    // Bulk Actions for Dịch vụ
    @PostMapping("/admin/dich-vu/bulk-activate")
    public String nqtDichVuBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtDichVuResponse service = nqtDichVuService.nqtGetById(id);
                    NqtDichVuRequest request = new NqtDichVuRequest();
                    request.setNqtTen(service.getNqtTen());
                    request.setNqtDonGia(service.getNqtDonGia());
                    request.setNqtHinhAnh(service.getNqtHinhAnh());
                    request.setNqtStatus(true);
                    request.setNqtMetaTitle(service.getNqtMetaTitle());
                    request.setNqtMetaKeyword(service.getNqtMetaKeyword());
                    request.setNqtMetaDescription(service.getNqtMetaDescription());
                    nqtDichVuService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã kích hoạt " + count + " dịch vụ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    @PostMapping("/admin/dich-vu/bulk-deactivate")
    public String nqtDichVuBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtDichVuResponse service = nqtDichVuService.nqtGetById(id);
                    NqtDichVuRequest request = new NqtDichVuRequest();
                    request.setNqtTen(service.getNqtTen());
                    request.setNqtDonGia(service.getNqtDonGia());
                    request.setNqtHinhAnh(service.getNqtHinhAnh());
                    request.setNqtStatus(false);
                    request.setNqtMetaTitle(service.getNqtMetaTitle());
                    request.setNqtMetaKeyword(service.getNqtMetaKeyword());
                    request.setNqtMetaDescription(service.getNqtMetaDescription());
                    nqtDichVuService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã vô hiệu hóa " + count + " dịch vụ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    @PostMapping("/admin/dich-vu/bulk-delete")
    public String nqtDichVuBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtDichVuService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " dịch vụ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/dich-vu";
    }

    // ========== ĐƠN GIÁ DỊCH VỤ ==========
    @GetMapping("/admin/don-gia-dich-vu")
    public String nqtDonGiaDichVuList(Model model) {
        List<NqtDonGiaDichVuResponse> nqtList = nqtDonGiaDichVuService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/don-gia-dich-vu/list";
    }

    @GetMapping("/admin/don-gia-dich-vu/create")
    public String nqtDonGiaDichVuCreateForm(Model model) {
        List<NqtDatPhongResponse> nqtDatPhongList = nqtDatPhongService.nqtGetAll();
        List<NqtDichVuResponse> nqtDichVuList = nqtDichVuService.nqtGetAll();
        model.addAttribute("nqtRequest", new NqtDonGiaDichVuRequest());
        model.addAttribute("nqtDatPhongList", nqtDatPhongList);
        model.addAttribute("nqtDichVuList", nqtDichVuList);
        return "admin/don-gia-dich-vu/form";
    }

    @PostMapping("/admin/don-gia-dich-vu/create")
    public String nqtDonGiaDichVuCreate(@ModelAttribute NqtDonGiaDichVuRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDonGiaDichVuService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo đơn giá dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/don-gia-dich-vu";
    }

    @GetMapping("/admin/don-gia-dich-vu/edit/{nqtId}")
    public String nqtDonGiaDichVuEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDonGiaDichVuResponse nqtResponse = nqtDonGiaDichVuService.nqtGetById(nqtId);
        List<NqtDatPhongResponse> nqtDatPhongList = nqtDatPhongService.nqtGetAll();
        List<NqtDichVuResponse> nqtDichVuList = nqtDichVuService.nqtGetAll();
        NqtDonGiaDichVuRequest nqtRequest = new NqtDonGiaDichVuRequest();
        nqtRequest.setNqtSoLuong(nqtResponse.getNqtSoLuong());
        nqtRequest.setNqtThanhTien(nqtResponse.getNqtThanhTien());
        nqtRequest.setNqtDatPhongId(nqtResponse.getNqtDatPhongId());
        nqtRequest.setNqtDichVuId(nqtResponse.getNqtDichVuId());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtDatPhongList", nqtDatPhongList);
        model.addAttribute("nqtDichVuList", nqtDichVuList);
        model.addAttribute("nqtId", nqtId);
        return "admin/don-gia-dich-vu/form";
    }

    @PostMapping("/admin/don-gia-dich-vu/edit/{nqtId}")
    public String nqtDonGiaDichVuUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtDonGiaDichVuRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDonGiaDichVuService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/don-gia-dich-vu";
    }

    @GetMapping("/admin/don-gia-dich-vu/delete/{nqtId}")
    public String nqtDonGiaDichVuDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDonGiaDichVuService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/don-gia-dich-vu";
    }

    // Bulk Actions for Đơn giá dịch vụ
    @PostMapping("/admin/don-gia-dich-vu/bulk-delete")
    public String nqtDonGiaDichVuBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtDonGiaDichVuService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " đơn giá dịch vụ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/don-gia-dich-vu";
    }

    // ========== ĐÁNH GIÁ ==========
    @GetMapping("/admin/danh-gia")
    public String nqtDanhGiaList(Model model) {
        List<NqtDanhGiaResponse> nqtList = nqtDanhGiaService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/danh-gia/list";
    }

    @GetMapping("/admin/danh-gia/create")
    public String nqtDanhGiaCreateForm(Model model) {
        List<NqtDatPhongResponse> nqtDatPhongList = nqtDatPhongService.nqtGetAll();
        model.addAttribute("nqtRequest", new NqtDanhGiaRequest());
        model.addAttribute("nqtDatPhongList", nqtDatPhongList);
        return "admin/danh-gia/form";
    }

    @PostMapping("/admin/danh-gia/create")
    public String nqtDanhGiaCreate(@ModelAttribute NqtDanhGiaRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDanhGiaService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    @GetMapping("/admin/danh-gia/edit/{nqtId}")
    public String nqtDanhGiaEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDanhGiaResponse nqtResponse = nqtDanhGiaService.nqtGetById(nqtId);
        List<NqtDatPhongResponse> nqtDatPhongList = nqtDatPhongService.nqtGetAll();
        NqtDanhGiaRequest nqtRequest = new NqtDanhGiaRequest();
        nqtRequest.setNqtDatPhongId(nqtResponse.getNqtDatPhongId());
        nqtRequest.setNqtNoiDungDanhGia(nqtResponse.getNqtNoiDungDanhGia());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtDatPhongList", nqtDatPhongList);
        model.addAttribute("nqtId", nqtId);
        return "admin/danh-gia/form";
    }

    @PostMapping("/admin/danh-gia/edit/{nqtId}")
    public String nqtDanhGiaUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtDanhGiaRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtDanhGiaService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    @GetMapping("/admin/danh-gia/delete/{nqtId}")
    public String nqtDanhGiaDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDanhGiaService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    // Bulk Actions for Đánh giá
    @PostMapping("/admin/danh-gia/bulk-activate")
    public String nqtDanhGiaBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtDanhGiaResponse danhGia = nqtDanhGiaService.nqtGetById(id);
                    NqtDanhGiaRequest request = new NqtDanhGiaRequest();
                    request.setNqtDatPhongId(danhGia.getNqtDatPhongId());
                    request.setNqtNoiDungDanhGia(danhGia.getNqtNoiDungDanhGia());
                    request.setNqtStatus(true);
                    nqtDanhGiaService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã hiển thị " + count + " đánh giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    @PostMapping("/admin/danh-gia/bulk-deactivate")
    public String nqtDanhGiaBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtDanhGiaResponse danhGia = nqtDanhGiaService.nqtGetById(id);
                    NqtDanhGiaRequest request = new NqtDanhGiaRequest();
                    request.setNqtDatPhongId(danhGia.getNqtDatPhongId());
                    request.setNqtNoiDungDanhGia(danhGia.getNqtNoiDungDanhGia());
                    request.setNqtStatus(false);
                    nqtDanhGiaService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã ẩn " + count + " đánh giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    @PostMapping("/admin/danh-gia/bulk-delete")
    public String nqtDanhGiaBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtDanhGiaService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " đánh giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/danh-gia";
    }

    // ========== BLOG ==========
    @GetMapping("/admin/blog")
    public String nqtBlogList(Model model) {
        List<NqtBlogResponse> nqtList = nqtBlogService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/blog/list";
    }

    @GetMapping("/admin/blog/create")
    public String nqtBlogCreateForm(Model model) {
        model.addAttribute("nqtRequest", new NqtBlogRequest());
        return "admin/blog/form";
    }

    @PostMapping("/admin/blog/create")
    public String nqtBlogCreate(@ModelAttribute NqtBlogRequest nqtRequest,
            @RequestParam("nqtImage") MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtBlogService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo blog thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    @GetMapping("/admin/blog/edit/{nqtId}")
    public String nqtBlogEditForm(@PathVariable Integer nqtId, Model model) {
        NqtBlogResponse nqtResponse = nqtBlogService.nqtGetById(nqtId);
        NqtBlogRequest nqtRequest = new NqtBlogRequest();
        nqtRequest.setNqtTieuDe(nqtResponse.getNqtTieuDe());
        nqtRequest.setNqtNoiDung(nqtResponse.getNqtNoiDung());
        nqtRequest.setNqtHinhAnh(nqtResponse.getNqtHinhAnh());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtMetaTitle(nqtResponse.getNqtMetaTitle());
        nqtRequest.setNqtMetaKeyword(nqtResponse.getNqtMetaKeyword());
        nqtRequest.setNqtMetaDescription(nqtResponse.getNqtMetaDescription());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/blog/form";
    }

    @PostMapping("/admin/blog/edit/{nqtId}")
    public String nqtBlogUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtBlogRequest nqtRequest,
            @RequestParam("nqtImage") MultipartFile nqtImage,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtImage != null && !nqtImage.isEmpty()) {
                nqtRequest.setNqtHinhAnh(saveFile(nqtImage));
            }
            nqtBlogService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    @GetMapping("/admin/blog/delete/{nqtId}")
    public String nqtBlogDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtBlogService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    // Bulk Actions for Blog
    @PostMapping("/admin/blog/bulk-activate")
    public String nqtBlogBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtBlogResponse blog = nqtBlogService.nqtGetById(id);
                    NqtBlogRequest request = new NqtBlogRequest();
                    request.setNqtTieuDe(blog.getNqtTieuDe());
                    request.setNqtNoiDung(blog.getNqtNoiDung());
                    request.setNqtHinhAnh(blog.getNqtHinhAnh());
                    request.setNqtStatus(true);
                    request.setNqtMetaTitle(blog.getNqtMetaTitle());
                    request.setNqtMetaKeyword(blog.getNqtMetaKeyword());
                    request.setNqtMetaDescription(blog.getNqtMetaDescription());
                    nqtBlogService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã hiển thị " + count + " blog!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    @PostMapping("/admin/blog/bulk-deactivate")
    public String nqtBlogBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtBlogResponse blog = nqtBlogService.nqtGetById(id);
                    NqtBlogRequest request = new NqtBlogRequest();
                    request.setNqtTieuDe(blog.getNqtTieuDe());
                    request.setNqtNoiDung(blog.getNqtNoiDung());
                    request.setNqtHinhAnh(blog.getNqtHinhAnh());
                    request.setNqtStatus(false);
                    request.setNqtMetaTitle(blog.getNqtMetaTitle());
                    request.setNqtMetaKeyword(blog.getNqtMetaKeyword());
                    request.setNqtMetaDescription(blog.getNqtMetaDescription());
                    nqtBlogService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã ẩn " + count + " blog!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    @PostMapping("/admin/blog/bulk-delete")
    public String nqtBlogBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtBlogService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " blog!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/blog";
    }

    // ========== CẤU HÌNH ==========
    @GetMapping("/admin/setting")
    public String nqtSetting(Model model) {
        model.addAttribute("nqtWebsiteName", nqtSettingService.getNqtValue("nqtWebsiteName", "Quản lý Khách sạn"));
        model.addAttribute("nqtWebsiteColor", nqtSettingService.getNqtValue("nqtWebsiteColor", "#4e73df"));
        model.addAttribute("nqtTieuDe", nqtSettingService.getNqtValue("TieuDe", "Tiêu đề mặc định"));
        model.addAttribute("nqtWebsiteLogo", nqtSettingService.getNqtValue("nqtWebsiteLogo", ""));
        model.addAttribute("nqtWebsiteFont", nqtSettingService.getNqtValue("nqtWebsiteFont", "Nunito"));
        // Customer Font Settings
        model.addAttribute("nqtCustomerFontBody", nqtSettingService.getNqtValue("nqtCustomerFontBody", ""));
        model.addAttribute("nqtCustomerFontHeading", nqtSettingService.getNqtValue("nqtCustomerFontHeading", "Playfair Display"));
        model.addAttribute("nqtCustomerFontSerif", nqtSettingService.getNqtValue("nqtCustomerFontSerif", "Playfair Display"));
        model.addAttribute("nqtWebsiteAddress", nqtSettingService.getNqtValue("nqtWebsiteAddress", "123 Đường ABC, Quận XYZ, Hà Nội, Việt Nam"));
        model.addAttribute("nqtWebsitePhone", nqtSettingService.getNqtValue("nqtWebsitePhone", "0123456789"));
        model.addAttribute("nqtWebsiteEmail", nqtSettingService.getNqtValue("nqtWebsiteEmail", "contact@example.com"));
        model.addAttribute("nqtWebsiteFacebook", nqtSettingService.getNqtValue("nqtWebsiteFacebook", "#"));
        model.addAttribute("nqtWebsiteZalo", nqtSettingService.getNqtValue("nqtWebsiteZalo", "#"));
        model.addAttribute("nqtWebsiteLink", nqtSettingService.getNqtValue("nqtWebsiteLink", "#"));
        model.addAttribute("nqtWebsiteFAQ", nqtSettingService.getNqtValue("nqtWebsiteFAQ", "#"));
        model.addAttribute("nqtWebsiteSupportLinks", nqtSettingService.getNqtValue("nqtWebsiteSupportLinks", ""));
        model.addAttribute("nqtVipDiscountPercent", nqtSettingService.getNqtValue("nqtVipDiscountPercent", "10"));
        
        // SMTP Settings
        model.addAttribute("nqtSmtpHost", nqtSettingService.getNqtValue("nqtSmtpHost", "smtp.gmail.com"));
        model.addAttribute("nqtSmtpPort", nqtSettingService.getNqtValue("nqtSmtpPort", "587"));
        model.addAttribute("nqtSmtpUsername", nqtSettingService.getNqtValue("nqtSmtpUsername", ""));
        model.addAttribute("nqtSmtpPassword", ""); // Never show password
        model.addAttribute("nqtSmtpFromEmail", nqtSettingService.getNqtValue("nqtSmtpFromEmail", ""));
        model.addAttribute("nqtSmtpFromName", nqtSettingService.getNqtValue("nqtSmtpFromName", ""));
        
        // Rate Limiting Settings
        model.addAttribute("rate_limit_max_failed_attempts", nqtSettingService.getNqtValue("rate_limit_max_failed_attempts", "5"));
        model.addAttribute("rate_limit_lockout_duration_minutes", nqtSettingService.getNqtValue("rate_limit_lockout_duration_minutes", "15"));
        model.addAttribute("rate_limit_max_attempts", nqtSettingService.getNqtValue("rate_limit_max_attempts", "10"));
        model.addAttribute("rate_limit_window_minutes", nqtSettingService.getNqtValue("rate_limit_window_minutes", "15"));
        model.addAttribute("rate_limit_ip_max_attempts", nqtSettingService.getNqtValue("rate_limit_ip_max_attempts", "20"));
        model.addAttribute("rate_limit_ip_window_minutes", nqtSettingService.getNqtValue("rate_limit_ip_window_minutes", "15"));
        model.addAttribute("rate_limit_cleanup_days", nqtSettingService.getNqtValue("rate_limit_cleanup_days", "30"));
        
        // Admin Path Setting - use service to get sanitized path (without leading slash)
        model.addAttribute("admin_path", adminPathService.getAdminPath());
        
        // Banner Images
        String bannerImagesJson = nqtSettingService.getNqtValue("nqtBannerImages", "[]");
        try {
            java.util.List<String> bannerImagesList = new java.util.ArrayList<>();
            if (bannerImagesJson != null && !bannerImagesJson.trim().isEmpty() && !bannerImagesJson.equals("[]")) {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                bannerImagesList = objectMapper.readValue(bannerImagesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, String.class));
            }
            model.addAttribute("bannerImagesList", bannerImagesList);
            model.addAttribute("bannerImagesJson", bannerImagesJson);
        } catch (Exception e) {
            model.addAttribute("bannerImagesList", new java.util.ArrayList<>());
            model.addAttribute("bannerImagesJson", "[]");
        }
        
        return "admin/setting/form";
    }

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtEmailService nqtEmailService;

    @PostMapping("/admin/setting")
    public String nqtSettingUpdate(@RequestParam("nqtWebsiteName") String nqtWebsiteName,
            @RequestParam("nqtWebsiteColor") String nqtWebsiteColor,
            @RequestParam("nqtWebsiteFont") String nqtWebsiteFont,
            @RequestParam(value = "nqtCustomerFontBody", required = false) String nqtCustomerFontBody,
            @RequestParam(value = "nqtCustomerFontHeading", required = false) String nqtCustomerFontHeading,
            @RequestParam(value = "nqtCustomerFontSerif", required = false) String nqtCustomerFontSerif,
            @RequestParam("nqtTieuDe") String nqtTieuDe,
            @RequestParam(value = "nqtWebsiteLogoFile", required = false) MultipartFile nqtWebsiteLogoFile,
            @RequestParam(value = "nqtWebsiteAddress", required = false) String nqtWebsiteAddress,
            @RequestParam(value = "nqtWebsitePhone", required = false) String nqtWebsitePhone,
            @RequestParam(value = "nqtWebsiteEmail", required = false) String nqtWebsiteEmail,
            @RequestParam(value = "nqtWebsiteFacebook", required = false) String nqtWebsiteFacebook,
            @RequestParam(value = "nqtWebsiteZalo", required = false) String nqtWebsiteZalo,
            @RequestParam(value = "nqtWebsiteLink", required = false) String nqtWebsiteLink,
            @RequestParam(value = "nqtWebsiteFAQ", required = false) String nqtWebsiteFAQ,
            @RequestParam(value = "nqtWebsiteSupportLinks", required = false) String nqtWebsiteSupportLinks,
            @RequestParam(value = "nqtVipDiscountPercent", required = false) String nqtVipDiscountPercent,
            @RequestParam(value = "nqtSmtpHost", required = false) String nqtSmtpHost,
            @RequestParam(value = "nqtSmtpPort", required = false) String nqtSmtpPort,
            @RequestParam(value = "nqtSmtpUsername", required = false) String nqtSmtpUsername,
            @RequestParam(value = "nqtSmtpPassword", required = false) String nqtSmtpPassword,
            @RequestParam(value = "nqtSmtpFromEmail", required = false) String nqtSmtpFromEmail,
            @RequestParam(value = "nqtSmtpFromName", required = false) String nqtSmtpFromName,
            @RequestParam(value = "rate_limit_max_failed_attempts", required = false) String rate_limit_max_failed_attempts,
            @RequestParam(value = "rate_limit_lockout_duration_minutes", required = false) String rate_limit_lockout_duration_minutes,
            @RequestParam(value = "rate_limit_max_attempts", required = false) String rate_limit_max_attempts,
            @RequestParam(value = "rate_limit_window_minutes", required = false) String rate_limit_window_minutes,
            @RequestParam(value = "rate_limit_ip_max_attempts", required = false) String rate_limit_ip_max_attempts,
            @RequestParam(value = "rate_limit_ip_window_minutes", required = false) String rate_limit_ip_window_minutes,
            @RequestParam(value = "rate_limit_cleanup_days", required = false) String rate_limit_cleanup_days,
            @RequestParam(value = "admin_path", required = false) String admin_path,
            @RequestParam(value = "bannerImagesJson", required = false) String bannerImagesJson,
            RedirectAttributes redirectAttributes) {
        try {
            if (nqtWebsiteLogoFile != null && !nqtWebsiteLogoFile.isEmpty()) {
                String logoPath = saveFile(nqtWebsiteLogoFile);
                if (logoPath != null) {
                    nqtSettingService.saveNqtValue("nqtWebsiteLogo", logoPath);
                }
            }
            nqtSettingService.saveNqtValue("nqtWebsiteName", nqtWebsiteName);
            nqtSettingService.saveNqtValue("nqtWebsiteColor", nqtWebsiteColor);
            nqtSettingService.saveNqtValue("nqtWebsiteFont", nqtWebsiteFont);
            // Save customer font settings
            if (nqtCustomerFontBody != null && !nqtCustomerFontBody.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtCustomerFontBody", nqtCustomerFontBody.trim());
            }
            if (nqtCustomerFontHeading != null && !nqtCustomerFontHeading.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtCustomerFontHeading", nqtCustomerFontHeading.trim());
            }
            if (nqtCustomerFontSerif != null && !nqtCustomerFontSerif.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtCustomerFontSerif", nqtCustomerFontSerif.trim());
            }
            nqtSettingService.saveNqtValue("TieuDe", nqtTieuDe);
            if (nqtWebsiteAddress != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteAddress", nqtWebsiteAddress);
            }
            if (nqtWebsitePhone != null) {
                nqtSettingService.saveNqtValue("nqtWebsitePhone", nqtWebsitePhone);
            }
            if (nqtWebsiteEmail != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteEmail", nqtWebsiteEmail);
            }
            if (nqtWebsiteFacebook != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteFacebook", nqtWebsiteFacebook);
            }
            if (nqtWebsiteZalo != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteZalo", nqtWebsiteZalo);
            }
            if (nqtWebsiteLink != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteLink", nqtWebsiteLink);
            }
            if (nqtWebsiteFAQ != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteFAQ", nqtWebsiteFAQ);
            }
            if (nqtWebsiteSupportLinks != null) {
                nqtSettingService.saveNqtValue("nqtWebsiteSupportLinks", nqtWebsiteSupportLinks);
            }
            if (nqtVipDiscountPercent != null && !nqtVipDiscountPercent.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtVipDiscountPercent", nqtVipDiscountPercent);
            }
            
            // Save SMTP settings
            if (nqtSmtpHost != null) {
                nqtSettingService.saveNqtValue("nqtSmtpHost", nqtSmtpHost);
            }
            if (nqtSmtpPort != null) {
                nqtSettingService.saveNqtValue("nqtSmtpPort", nqtSmtpPort);
            }
            if (nqtSmtpUsername != null) {
                nqtSettingService.saveNqtValue("nqtSmtpUsername", nqtSmtpUsername);
            }
            // Only update password if provided (not empty)
            if (nqtSmtpPassword != null && !nqtSmtpPassword.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtSmtpPassword", nqtSmtpPassword);
            }
            if (nqtSmtpFromEmail != null) {
                nqtSettingService.saveNqtValue("nqtSmtpFromEmail", nqtSmtpFromEmail);
            }
            if (nqtSmtpFromName != null) {
                nqtSettingService.saveNqtValue("nqtSmtpFromName", nqtSmtpFromName);
            }
            
            // Update email service with new settings
            if (nqtEmailService != null) {
                nqtEmailService.updateMailSender();
            }
            
            // Save Rate Limiting Settings
            if (rate_limit_max_failed_attempts != null && !rate_limit_max_failed_attempts.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_max_failed_attempts", rate_limit_max_failed_attempts);
            }
            if (rate_limit_lockout_duration_minutes != null && !rate_limit_lockout_duration_minutes.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_lockout_duration_minutes", rate_limit_lockout_duration_minutes);
            }
            if (rate_limit_max_attempts != null && !rate_limit_max_attempts.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_max_attempts", rate_limit_max_attempts);
            }
            if (rate_limit_window_minutes != null && !rate_limit_window_minutes.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_window_minutes", rate_limit_window_minutes);
            }
            if (rate_limit_ip_max_attempts != null && !rate_limit_ip_max_attempts.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_ip_max_attempts", rate_limit_ip_max_attempts);
            }
            if (rate_limit_ip_window_minutes != null && !rate_limit_ip_window_minutes.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_ip_window_minutes", rate_limit_ip_window_minutes);
            }
            if (rate_limit_cleanup_days != null && !rate_limit_cleanup_days.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("rate_limit_cleanup_days", rate_limit_cleanup_days);
            }
            
            // Save Admin Path
            if (admin_path != null && !admin_path.trim().isEmpty()) {
                boolean saved = adminPathService.setAdminPath(admin_path.trim());
                if (!saved) {
                    redirectAttributes.addFlashAttribute("nqtError", "Đường dẫn admin không hợp lệ! Chỉ cho phép chữ cái, số, dấu gạch dưới (_) và dấu gạch ngang (-).");
                    // Use current admin path (before change) for redirect
                    String currentAdminPath = adminPathService.getAdminPathWithSlash();
                    return "redirect:" + currentAdminPath + "/setting";
                }
            }
            
            // Save Banner Images
            if (bannerImagesJson != null && !bannerImagesJson.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtBannerImages", bannerImagesJson);
            }
            
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật cấu hình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        // Use dynamic admin path for redirect
        String currentAdminPath = adminPathService.getAdminPathWithSlash();
        return "redirect:" + currentAdminPath + "/setting";
    }

    @PostMapping("/admin/setting/test-smtp")
    @ResponseBody
    public java.util.Map<String, Object> testSmtpConnection(
            @RequestParam("nqtSmtpHost") String nqtSmtpHost,
            @RequestParam("nqtSmtpPort") String nqtSmtpPort,
            @RequestParam("nqtSmtpUsername") String nqtSmtpUsername,
            @RequestParam(value = "nqtSmtpPassword", required = false) String nqtSmtpPassword,
            @RequestParam("nqtSmtpFromEmail") String nqtSmtpFromEmail) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // Temporarily save settings for test
            nqtSettingService.saveNqtValue("nqtSmtpHost", nqtSmtpHost);
            nqtSettingService.saveNqtValue("nqtSmtpPort", nqtSmtpPort);
            nqtSettingService.saveNqtValue("nqtSmtpUsername", nqtSmtpUsername);
            if (nqtSmtpPassword != null && !nqtSmtpPassword.trim().isEmpty()) {
                nqtSettingService.saveNqtValue("nqtSmtpPassword", nqtSmtpPassword);
            }
            nqtSettingService.saveNqtValue("nqtSmtpFromEmail", nqtSmtpFromEmail);
            
            // Update mail sender
            nqtEmailService.updateMailSender();
            
            // Try to send test email
            if (nqtSmtpFromEmail != null && !nqtSmtpFromEmail.trim().isEmpty()) {
                nqtEmailService.sendTextEmail(nqtSmtpFromEmail, "Test SMTP Connection - Hotel NQT", 
                    "Đây là email test để kiểm tra cấu hình SMTP. Nếu bạn nhận được email này, cấu hình SMTP đã hoạt động thành công!");
                result.put("success", true);
                result.put("message", "Đã gửi email test thành công! Vui lòng kiểm tra hộp thư đến.");
            } else {
                result.put("success", false);
                result.put("message", "Vui lòng nhập địa chỉ email gửi đi (From Email)");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @PostMapping(value = {
        "/admin/setting/upload-banner", 
        "/nqtAdmin/setting/upload-banner"
    }, produces = "application/json")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> uploadBannerImages(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam("bannerImages") List<MultipartFile> bannerImages) {
        String requestURI = request.getRequestURI();
        System.out.println("uploadBannerImages endpoint called, URI: " + requestURI);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.List<String> uploadedImages = new java.util.ArrayList<>();
        
        try {
            if (bannerImages == null || bannerImages.isEmpty()) {
                result.put("success", false);
                result.put("message", "Không có ảnh nào được chọn");
                return org.springframework.http.ResponseEntity.ok(result);
            }
            
            for (MultipartFile file : bannerImages) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String imagePath = saveFile(file);
                        if (imagePath != null && !imagePath.isEmpty()) {
                            uploadedImages.add(imagePath);
                        }
                    } catch (Exception e) {
                        System.err.println("Error saving file: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            if (uploadedImages.isEmpty()) {
                result.put("success", false);
                result.put("message", "Không thể upload ảnh. Vui lòng kiểm tra định dạng và kích thước file.");
            } else {
                result.put("success", true);
                result.put("images", uploadedImages);
                result.put("message", "Đã upload " + uploadedImages.size() + " ảnh thành công!");
            }
            
            return org.springframework.http.ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(result);
        } catch (Exception e) {
            System.err.println("Error in uploadBannerImages: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi khi upload ảnh: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            return org.springframework.http.ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(result);
        }
    }

    // ========== MÃ GIẢM GIÁ ==========
    @GetMapping("/admin/giam-gia")
    public String nqtGiamGiaList(Model model) {
        List<NqtGiamGiaResponse> nqtList = nqtGiamGiaService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/giam-gia/list";
    }

    @GetMapping("/admin/giam-gia/create")
    public String nqtGiamGiaCreateForm(Model model) {
        List<NqtNguoiDungResponse> nqtNguoiDungList = nqtNguoiDungService.nqtGetAll();
        model.addAttribute("nqtRequest", new NqtGiamGiaRequest());
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        return "admin/giam-gia/form";
    }

    @PostMapping("/admin/giam-gia/create")
    public String nqtGiamGiaCreate(@ModelAttribute NqtGiamGiaRequest nqtRequest, RedirectAttributes redirectAttributes) {
        try {
            nqtGiamGiaService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    @GetMapping("/admin/giam-gia/edit/{nqtId}")
    public String nqtGiamGiaEditForm(@PathVariable Integer nqtId, Model model) {
        NqtGiamGiaResponse nqtResponse = nqtGiamGiaService.nqtGetById(nqtId);
        NqtGiamGiaRequest nqtRequest = new NqtGiamGiaRequest();
        nqtRequest.setNqtMaGiamGia(nqtResponse.getNqtMaGiamGia());
        nqtRequest.setNqtMoTa(nqtResponse.getNqtMoTa());
        nqtRequest.setNqtLoaiGiam(nqtResponse.getNqtLoaiGiam());
        nqtRequest.setNqtGiaTriGiam(nqtResponse.getNqtGiaTriGiam());
        nqtRequest.setNqtGiaTriToiThieu(nqtResponse.getNqtGiaTriToiThieu());
        nqtRequest.setNqtGiaTriGiamToiDa(nqtResponse.getNqtGiaTriGiamToiDa());
        nqtRequest.setNqtNgayBatDau(nqtResponse.getNqtNgayBatDau());
        nqtRequest.setNqtNgayKetThuc(nqtResponse.getNqtNgayKetThuc());
        nqtRequest.setNqtSoLuongToiDa(nqtResponse.getNqtSoLuongToiDa());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtNguoiDungId(nqtResponse.getNqtNguoiDungId());
        nqtRequest.setNqtChiChoVip(nqtResponse.getNqtChiChoVip());

        List<NqtNguoiDungResponse> nqtNguoiDungList = nqtNguoiDungService.nqtGetAll();
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        model.addAttribute("nqtId", nqtId);
        return "admin/giam-gia/form";
    }

    @PostMapping("/admin/giam-gia/edit/{nqtId}")
    public String nqtGiamGiaUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtGiamGiaRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtGiamGiaService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    @GetMapping("/admin/giam-gia/delete/{nqtId}")
    public String nqtGiamGiaDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtGiamGiaService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    // Bulk Actions for Mã giảm giá
    @PostMapping("/admin/giam-gia/bulk-activate")
    public String nqtGiamGiaBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtGiamGiaResponse giamGia = nqtGiamGiaService.nqtGetById(id);
                    NqtGiamGiaRequest request = new NqtGiamGiaRequest();
                    request.setNqtMaGiamGia(giamGia.getNqtMaGiamGia());
                    request.setNqtMoTa(giamGia.getNqtMoTa());
                    request.setNqtLoaiGiam(giamGia.getNqtLoaiGiam());
                    request.setNqtGiaTriGiam(giamGia.getNqtGiaTriGiam());
                    request.setNqtGiaTriToiThieu(giamGia.getNqtGiaTriToiThieu());
                    request.setNqtGiaTriGiamToiDa(giamGia.getNqtGiaTriGiamToiDa());
                    request.setNqtNgayBatDau(giamGia.getNqtNgayBatDau());
                    request.setNqtNgayKetThuc(giamGia.getNqtNgayKetThuc());
                    request.setNqtSoLuongToiDa(giamGia.getNqtSoLuongToiDa());
                    request.setNqtStatus(true);
                    request.setNqtNguoiDungId(giamGia.getNqtNguoiDungId());
                    request.setNqtChiChoVip(giamGia.getNqtChiChoVip());
                    nqtGiamGiaService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã kích hoạt " + count + " mã giảm giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    @PostMapping("/admin/giam-gia/bulk-deactivate")
    public String nqtGiamGiaBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    NqtGiamGiaResponse giamGia = nqtGiamGiaService.nqtGetById(id);
                    NqtGiamGiaRequest request = new NqtGiamGiaRequest();
                    request.setNqtMaGiamGia(giamGia.getNqtMaGiamGia());
                    request.setNqtMoTa(giamGia.getNqtMoTa());
                    request.setNqtLoaiGiam(giamGia.getNqtLoaiGiam());
                    request.setNqtGiaTriGiam(giamGia.getNqtGiaTriGiam());
                    request.setNqtGiaTriToiThieu(giamGia.getNqtGiaTriToiThieu());
                    request.setNqtGiaTriGiamToiDa(giamGia.getNqtGiaTriGiamToiDa());
                    request.setNqtNgayBatDau(giamGia.getNqtNgayBatDau());
                    request.setNqtNgayKetThuc(giamGia.getNqtNgayKetThuc());
                    request.setNqtSoLuongToiDa(giamGia.getNqtSoLuongToiDa());
                    request.setNqtStatus(false);
                    request.setNqtNguoiDungId(giamGia.getNqtNguoiDungId());
                    request.setNqtChiChoVip(giamGia.getNqtChiChoVip());
                    nqtGiamGiaService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã vô hiệu hóa " + count + " mã giảm giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    @PostMapping("/admin/giam-gia/bulk-delete")
    public String nqtGiamGiaBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtGiamGiaService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " mã giảm giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/giam-gia";
    }

    // ========== CRON LOG ==========
    @GetMapping("/admin/cron-log")
    public String nqtCronLogList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        
        org.springframework.data.domain.Page<k23cnt1.nqt.project3.nqtEntity.NqtCronLog> logPage;
        
        long totalSuccess = 0;
        long totalError = 0;
        long totalWarning = 0;
        
        if (taskName != null && !taskName.isEmpty()) {
            List<k23cnt1.nqt.project3.nqtEntity.NqtCronLog> logs = nqtCronLogService.getLogsByTaskName(taskName);
            model.addAttribute("nqtList", logs);
            model.addAttribute("totalLogs", logs.size());
            totalSuccess = logs.stream().filter(l -> "SUCCESS".equals(l.getNqtStatus())).count();
            totalError = logs.stream().filter(l -> "ERROR".equals(l.getNqtStatus())).count();
            totalWarning = logs.stream().filter(l -> "WARNING".equals(l.getNqtStatus())).count();
        } else if (status != null && !status.isEmpty()) {
            List<k23cnt1.nqt.project3.nqtEntity.NqtCronLog> logs = nqtCronLogService.getLogsByStatus(status);
            model.addAttribute("nqtList", logs);
            model.addAttribute("totalLogs", logs.size());
            totalSuccess = logs.stream().filter(l -> "SUCCESS".equals(l.getNqtStatus())).count();
            totalError = logs.stream().filter(l -> "ERROR".equals(l.getNqtStatus())).count();
            totalWarning = logs.stream().filter(l -> "WARNING".equals(l.getNqtStatus())).count();
        } else {
            logPage = nqtCronLogService.getAllLogs(page, size);
            List<k23cnt1.nqt.project3.nqtEntity.NqtCronLog> allLogs = nqtCronLogService.getLatestLogs(1000);
            model.addAttribute("nqtList", logPage.getContent());
            model.addAttribute("totalPages", logPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalLogs", logPage.getTotalElements());
            totalSuccess = allLogs.stream().filter(l -> "SUCCESS".equals(l.getNqtStatus())).count();
            totalError = allLogs.stream().filter(l -> "ERROR".equals(l.getNqtStatus())).count();
            totalWarning = allLogs.stream().filter(l -> "WARNING".equals(l.getNqtStatus())).count();
        }
        
        model.addAttribute("totalSuccess", totalSuccess);
        model.addAttribute("totalError", totalError);
        model.addAttribute("totalWarning", totalWarning);
        model.addAttribute("selectedTaskName", taskName);
        model.addAttribute("selectedStatus", status);
        
        return "admin/cron-log/list";
    }

    @GetMapping("/admin/cron-log/delete/{id}")
    public String nqtCronLogDelete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            nqtCronLogService.deleteLog(id);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa log thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/cron-log";
    }

    @GetMapping("/admin/cron-log/clear-all")
    public String nqtCronLogClearAll(RedirectAttributes redirectAttributes) {
        try {
            nqtCronLogService.deleteAllLogs();
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa tất cả log!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/cron-log";
    }

    // ========== NGÂN HÀNG ==========
    @GetMapping("/admin/ngan-hang")
    public String nqtNganHangList(Model model) {
        List<k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse> nqtList = nqtNganHangService.nqtGetAll();
        model.addAttribute("nqtList", nqtList);
        return "admin/ngan-hang/list";
    }

    @GetMapping("/admin/ngan-hang/create")
    public String nqtNganHangCreateForm(Model model) {
        model.addAttribute("nqtRequest", new k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest());
        return "admin/ngan-hang/form";
    }

    @PostMapping("/admin/ngan-hang/create")
    public String nqtNganHangCreate(@ModelAttribute k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest nqtRequest, RedirectAttributes redirectAttributes) {
        try {
            nqtNganHangService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo ngân hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    @GetMapping("/admin/ngan-hang/edit/{nqtId}")
    public String nqtNganHangEditForm(@PathVariable Integer nqtId, Model model) {
        k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse nqtResponse = nqtNganHangService.nqtGetById(nqtId);
        k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest nqtRequest = new k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest();
        nqtRequest.setNqtTenNganHang(nqtResponse.getNqtTenNganHang());
        nqtRequest.setNqtMaNganHang(nqtResponse.getNqtMaNganHang());
        nqtRequest.setNqtSoTaiKhoan(nqtResponse.getNqtSoTaiKhoan());
        nqtRequest.setNqtTenChuTaiKhoan(nqtResponse.getNqtTenChuTaiKhoan());
        nqtRequest.setNqtChiNhanh(nqtResponse.getNqtChiNhanh());
        nqtRequest.setNqtGhiChu(nqtResponse.getNqtGhiChu());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        nqtRequest.setNqtThuTu(nqtResponse.getNqtThuTu());

        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/ngan-hang/form";
    }

    @PostMapping("/admin/ngan-hang/edit/{nqtId}")
    public String nqtNganHangUpdate(@PathVariable Integer nqtId, @ModelAttribute k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtNganHangService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    @GetMapping("/admin/ngan-hang/delete/{nqtId}")
    public String nqtNganHangDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtNganHangService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    // Bulk Actions for Ngân hàng
    @PostMapping("/admin/ngan-hang/bulk-activate")
    public String nqtNganHangBulkActivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse nganHang = nqtNganHangService.nqtGetById(id);
                    k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest request = new k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest();
                    request.setNqtTenNganHang(nganHang.getNqtTenNganHang());
                    request.setNqtMaNganHang(nganHang.getNqtMaNganHang());
                    request.setNqtSoTaiKhoan(nganHang.getNqtSoTaiKhoan());
                    request.setNqtTenChuTaiKhoan(nganHang.getNqtTenChuTaiKhoan());
                    request.setNqtChiNhanh(nganHang.getNqtChiNhanh());
                    request.setNqtGhiChu(nganHang.getNqtGhiChu());
                    request.setNqtStatus(true);
                    request.setNqtThuTu(nganHang.getNqtThuTu());
                    nqtNganHangService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã kích hoạt " + count + " ngân hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    @PostMapping("/admin/ngan-hang/bulk-deactivate")
    public String nqtNganHangBulkDeactivate(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse nganHang = nqtNganHangService.nqtGetById(id);
                    k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest request = new k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest();
                    request.setNqtTenNganHang(nganHang.getNqtTenNganHang());
                    request.setNqtMaNganHang(nganHang.getNqtMaNganHang());
                    request.setNqtSoTaiKhoan(nganHang.getNqtSoTaiKhoan());
                    request.setNqtTenChuTaiKhoan(nganHang.getNqtTenChuTaiKhoan());
                    request.setNqtChiNhanh(nganHang.getNqtChiNhanh());
                    request.setNqtGhiChu(nganHang.getNqtGhiChu());
                    request.setNqtStatus(false);
                    request.setNqtThuTu(nganHang.getNqtThuTu());
                    nqtNganHangService.nqtUpdate(id, request);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã vô hiệu hóa " + count + " ngân hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    @PostMapping("/admin/ngan-hang/bulk-delete")
    public String nqtNganHangBulkDelete(@RequestParam("ids") List<Integer> ids, RedirectAttributes redirectAttributes) {
        try {
            int count = 0;
            for (Integer id : ids) {
                try {
                    nqtNganHangService.nqtDelete(id);
                    count++;
                } catch (Exception e) {
                    // Skip if error
                }
            }
            redirectAttributes.addFlashAttribute("nqtSuccess", "Đã xóa " + count + " ngân hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + adminPathService.getAdminPathWithSlash() + "/ngan-hang";
    }

    // ========== REPORT EXPORT ==========
    @GetMapping("/admin/report/excel")
    public org.springframework.http.ResponseEntity<byte[]> exportExcelReport() {
        try {
            // Gather all data (same as dashboard)
            List<NqtDatPhongResponse> allBookings = nqtDatPhongService.nqtGetAll();

            // Calculate statistics
            Double nqtTongDoanhThu = 0.0;
            for (NqtDatPhongResponse dp : allBookings) {
                if (dp.getNqtStatus() == 1 && dp.getNqtTongTien() != null) {
                    nqtTongDoanhThu += dp.getNqtTongTien();
                }
            }

            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            int lastMonth = now.minusMonths(1).getMonthValue();
            int lastMonthYear = now.minusMonths(1).getYear();

            Double currentMonthRevenue = 0.0;
            Double lastMonthRevenue = 0.0;
            int newBookingsCount = 0;

            for (NqtDatPhongResponse dp : allBookings) {
                if (dp.getNqtNgayDen() != null) {
                    int bookingMonth = dp.getNqtNgayDen().getMonthValue();
                    int bookingYear = dp.getNqtNgayDen().getYear();

                    if (bookingMonth == currentMonth && bookingYear == currentYear && dp.getNqtStatus() == 1
                            && dp.getNqtTongTien() != null) {
                        currentMonthRevenue += dp.getNqtTongTien();
                    }

                    if (bookingMonth == lastMonth && bookingYear == lastMonthYear && dp.getNqtStatus() == 1
                            && dp.getNqtTongTien() != null) {
                        lastMonthRevenue += dp.getNqtTongTien();
                    }

                    if (bookingMonth == currentMonth && bookingYear == currentYear) {
                        newBookingsCount++;
                    }
                }
            }

            int growthPercentage = 0;
            if (lastMonthRevenue > 0) {
                growthPercentage = (int) (((currentMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100);
            } else if (currentMonthRevenue > 0) {
                growthPercentage = 100;
            }

            // Calculate monthly revenue
            Double[] nqtMonthlyRevenue = new Double[12];
            for (int i = 0; i < 12; i++)
                nqtMonthlyRevenue[i] = 0.0;

            for (NqtDatPhongResponse dp : allBookings) {
                if (dp.getNqtStatus() == 1 && dp.getNqtTongTien() != null && dp.getNqtNgayDen() != null) {
                    if (dp.getNqtNgayDen().getYear() == currentYear) {
                        int monthIndex = dp.getNqtNgayDen().getMonthValue() - 1;
                        nqtMonthlyRevenue[monthIndex] += dp.getNqtTongTien();
                    }
                }
            }

            // Calculate room occupancy
            List<k23cnt1.nqt.project3.nqtDto.NqtPhongResponse> rooms = nqtPhongService.nqtGetAll();
            int nqtOccupiedRooms = 0;

            for (NqtDatPhongResponse dp : allBookings) {
                if (dp.getNqtStatus() != 2 && dp.getNqtNgayDen() != null && dp.getNqtNgayDi() != null) {
                    if (!now.isBefore(dp.getNqtNgayDen()) && !now.isAfter(dp.getNqtNgayDi())) {
                        nqtOccupiedRooms++;
                    }
                }
            }
            int nqtVacantRooms = rooms.size() - nqtOccupiedRooms;
            if (nqtVacantRooms < 0)
                nqtVacantRooms = 0;

            // Generate Excel report
            byte[] excelBytes = nqtReportService.generateExcelReport(
                    nqtTongDoanhThu,
                    growthPercentage,
                    newBookingsCount,
                    nqtOccupiedRooms,
                    nqtVacantRooms,
                    nqtMonthlyRevenue,
                    allBookings);

            // Set response headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            String filename = "BaoCaoHoatDong_" + now.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                    + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);

            return new org.springframework.http.ResponseEntity<>(excelBytes, headers,
                    org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // Helper method to save file
    private String saveFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/uploads");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                return "/uploads/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
