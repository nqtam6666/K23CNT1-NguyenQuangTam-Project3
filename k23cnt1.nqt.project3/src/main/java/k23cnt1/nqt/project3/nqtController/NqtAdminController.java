package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.*;
import k23cnt1.nqt.project3.nqtService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    // Dashboard
    @GetMapping({ "/admin", "/admin/dashboard" })
    public String nqtDashboard(Model model) {
        System.out.println("NqtAdminController: nqtDashboard called");
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
            RedirectAttributes redirectAttributes) {
        try {
            nqtNguoiDungService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/nguoi-dung";
    }

    @GetMapping("/admin/nguoi-dung/edit/{nqtId}")
    public String nqtNguoiDungEditForm(@PathVariable Integer nqtId, Model model) {
        NqtNguoiDungResponse nqtResponse = nqtNguoiDungService.nqtGetById(nqtId);
        NqtNguoiDungRequest nqtRequest = new NqtNguoiDungRequest();
        nqtRequest.setNqtHoVaTen(nqtResponse.getNqtHoVaTen());
        nqtRequest.setNqtTaiKhoan(nqtResponse.getNqtTaiKhoan());
        nqtRequest.setNqtSoDienThoai(nqtResponse.getNqtSoDienThoai());
        nqtRequest.setNqtEmail(nqtResponse.getNqtEmail());
        nqtRequest.setNqtDiaChi(nqtResponse.getNqtDiaChi());
        nqtRequest.setNqtVaiTro(nqtResponse.getNqtVaiTro());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtId", nqtId);
        return "admin/nguoi-dung/form";
    }

    @PostMapping("/admin/nguoi-dung/edit/{nqtId}")
    public String nqtNguoiDungUpdate(@PathVariable Integer nqtId, @ModelAttribute NqtNguoiDungRequest nqtRequest,
            RedirectAttributes redirectAttributes) {
        try {
            nqtNguoiDungService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/nguoi-dung";
    }

    @GetMapping("/admin/nguoi-dung/delete/{nqtId}")
    public String nqtNguoiDungDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtNguoiDungService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/nguoi-dung";
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
            RedirectAttributes redirectAttributes) {
        try {
            nqtLoaiPhongService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo loại phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loai-phong";
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
            RedirectAttributes redirectAttributes) {
        try {
            nqtLoaiPhongService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loai-phong";
    }

    @GetMapping("/admin/loai-phong/delete/{nqtId}")
    public String nqtLoaiPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtLoaiPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/loai-phong";
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
        return "redirect:/admin/phong";
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
        return "redirect:/admin/phong";
    }

    @GetMapping("/admin/phong/delete/{nqtId}")
    public String nqtPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/phong";
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
        model.addAttribute("nqtRequest", new NqtDatPhongRequest());
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        model.addAttribute("nqtPhongList", nqtPhongList);
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
        return "redirect:/admin/dat-phong";
    }

    @GetMapping("/admin/dat-phong/edit/{nqtId}")
    public String nqtDatPhongEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDatPhongResponse nqtResponse = nqtDatPhongService.nqtGetById(nqtId);
        List<NqtNguoiDungResponse> nqtNguoiDungList = nqtNguoiDungService.nqtGetAll();
        List<NqtPhongResponse> nqtPhongList = nqtPhongService.nqtGetAll();
        NqtDatPhongRequest nqtRequest = new NqtDatPhongRequest();
        nqtRequest.setNqtNguoiDungId(nqtResponse.getNqtNguoiDungId());
        nqtRequest.setNqtPhongId(nqtResponse.getNqtPhongId());
        nqtRequest.setNqtNgayDen(nqtResponse.getNqtNgayDen());
        nqtRequest.setNqtNgayDi(nqtResponse.getNqtNgayDi());
        nqtRequest.setNqtTongTien(nqtResponse.getNqtTongTien());
        nqtRequest.setNqtGhiChu(nqtResponse.getNqtGhiChu());
        nqtRequest.setNqtStatus(nqtResponse.getNqtStatus());
        model.addAttribute("nqtRequest", nqtRequest);
        model.addAttribute("nqtNguoiDungList", nqtNguoiDungList);
        model.addAttribute("nqtPhongList", nqtPhongList);
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
        return "redirect:/admin/dat-phong";
    }

    @GetMapping("/admin/dat-phong/delete/{nqtId}")
    public String nqtDatPhongDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDatPhongService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/dat-phong";
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
    public String nqtDichVuCreate(@ModelAttribute NqtDichVuRequest nqtRequest, RedirectAttributes redirectAttributes) {
        try {
            nqtDichVuService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/dich-vu";
    }

    @GetMapping("/admin/dich-vu/edit/{nqtId}")
    public String nqtDichVuEditForm(@PathVariable Integer nqtId, Model model) {
        NqtDichVuResponse nqtResponse = nqtDichVuService.nqtGetById(nqtId);
        NqtDichVuRequest nqtRequest = new NqtDichVuRequest();
        nqtRequest.setNqtTen(nqtResponse.getNqtTen());
        nqtRequest.setNqtDonGia(nqtResponse.getNqtDonGia());
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
            RedirectAttributes redirectAttributes) {
        try {
            nqtDichVuService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/dich-vu";
    }

    @GetMapping("/admin/dich-vu/delete/{nqtId}")
    public String nqtDichVuDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDichVuService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/dich-vu";
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
        return "redirect:/admin/don-gia-dich-vu";
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
        return "redirect:/admin/don-gia-dich-vu";
    }

    @GetMapping("/admin/don-gia-dich-vu/delete/{nqtId}")
    public String nqtDonGiaDichVuDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDonGiaDichVuService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/don-gia-dich-vu";
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
        return "redirect:/admin/danh-gia";
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
        return "redirect:/admin/danh-gia";
    }

    @GetMapping("/admin/danh-gia/delete/{nqtId}")
    public String nqtDanhGiaDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtDanhGiaService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/danh-gia";
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
    public String nqtBlogCreate(@ModelAttribute NqtBlogRequest nqtRequest, RedirectAttributes redirectAttributes) {
        try {
            nqtBlogService.nqtCreate(nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Tạo blog thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/blog";
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
            RedirectAttributes redirectAttributes) {
        try {
            nqtBlogService.nqtUpdate(nqtId, nqtRequest);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/blog";
    }

    @GetMapping("/admin/blog/delete/{nqtId}")
    public String nqtBlogDelete(@PathVariable Integer nqtId, RedirectAttributes redirectAttributes) {
        try {
            nqtBlogService.nqtDelete(nqtId);
            redirectAttributes.addFlashAttribute("nqtSuccess", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/blog";
    }
}
