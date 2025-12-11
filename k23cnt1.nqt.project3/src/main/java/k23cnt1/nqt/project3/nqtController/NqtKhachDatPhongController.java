package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.*;
import k23cnt1.nqt.project3.nqtRepository.*;
import k23cnt1.nqt.project3.nqtService.NqtSettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
public class NqtKhachDatPhongController {

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    @Autowired
    private NqtDichVuRepository nqtDichVuRepository;

    @Autowired
    private NqtDonGiaDichVuRepository nqtDonGiaDichVuRepository;

    @Autowired
    private NqtDanhGiaRepository nqtDanhGiaRepository;

    @Autowired
    private NqtGiamGiaRepository nqtGiamGiaRepository;

    @Autowired
    private NqtSettingService nqtSettingService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtNganHangService nqtNganHangService;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtPaymentCheckService nqtPaymentCheckService;

    // Booking Form
    @GetMapping("/nqtDatPhong")
    public String nqtDatPhong(@RequestParam(value = "phongId", required = false) Integer phongId,
            @RequestParam(value = "ngayDen", required = false) String ngayDen,
            @RequestParam(value = "ngayDi", required = false) String ngayDi,
            @RequestParam(value = "soKhach", required = false) Integer soKhach,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            String redirectUrl = "/nqtDatPhong";
            if (phongId != null || ngayDen != null || ngayDi != null || soKhach != null) {
                redirectUrl += "?";
                if (phongId != null) redirectUrl += "phongId=" + phongId;
                if (ngayDen != null) redirectUrl += (redirectUrl.contains("?") && !redirectUrl.endsWith("?") ? "&" : "") + "ngayDen=" + ngayDen;
                if (ngayDi != null) redirectUrl += (redirectUrl.contains("?") && !redirectUrl.endsWith("?") ? "&" : "") + "ngayDi=" + ngayDi;
                if (soKhach != null) redirectUrl += (redirectUrl.contains("?") && !redirectUrl.endsWith("?") ? "&" : "") + "soKhach=" + soKhach;
            }
            session.setAttribute("nqtRedirectAfterLogin", redirectUrl);
            return "redirect:/nqtDangNhap";
        }

        // Get available rooms
        List<NqtPhong> availableRooms = nqtPhongRepository.findByNqtStatus(true);

        // Get available services
        List<NqtDichVu> services = nqtDichVuRepository.findByNqtStatus(true);

        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("services", services);
        model.addAttribute("selectedPhongId", phongId);
        model.addAttribute("selectedNgayDen", ngayDen);
        model.addAttribute("selectedNgayDi", ngayDi);
        model.addAttribute("selectedSoKhach", soKhach);

        return "nqtCustomer/nqtDatPhong/nqtForm";
    }

    // Create Booking
    @PostMapping("/nqtDatPhong")
    public String nqtDatPhongSubmit(@RequestParam("nqtPhongId") Integer nqtPhongId,
            @RequestParam("nqtNgayDen") String nqtNgayDenStr,
            @RequestParam("nqtNgayDi") String nqtNgayDiStr,
            @RequestParam(value = "nqtDichVuIds", required = false) List<Integer> nqtDichVuIds,
            @RequestParam(value = "nqtGhiChu", required = false) String nqtGhiChu,
            @RequestParam(value = "nqtMaGiamGia", required = false) String nqtMaGiamGia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        // Validate room
        Optional<NqtPhong> roomOptional = nqtPhongRepository.findById(nqtPhongId);
        if (roomOptional.isEmpty() || !roomOptional.get().getNqtStatus()) {
            redirectAttributes.addFlashAttribute("nqtError", "Phòng không khả dụng!");
            return "redirect:/nqtPhong";
        }

        NqtPhong room = roomOptional.get();

        // Parse dates
        LocalDate nqtNgayDen;
        LocalDate nqtNgayDi;
        try {
            nqtNgayDen = LocalDate.parse(nqtNgayDenStr);
            nqtNgayDi = LocalDate.parse(nqtNgayDiStr);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nqtError", "Ngày tháng không hợp lệ!");
            redirectAttributes.addAttribute("phongId", nqtPhongId);
            redirectAttributes.addAttribute("ngayDen", nqtNgayDenStr);
            redirectAttributes.addAttribute("ngayDi", nqtNgayDiStr);
            return "redirect:/nqtDatPhong";
        }

        // Validate dates
        if (nqtNgayDen.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("nqtError", "Ngày đến không hợp lệ! Vui lòng chọn ngày từ hôm nay trở đi.");
            redirectAttributes.addAttribute("phongId", nqtPhongId);
            redirectAttributes.addAttribute("ngayDen", nqtNgayDenStr);
            redirectAttributes.addAttribute("ngayDi", nqtNgayDiStr);
            return "redirect:/nqtDatPhong";
        }

        if (nqtNgayDi.isBefore(nqtNgayDen) || nqtNgayDi.isEqual(nqtNgayDen)) {
            redirectAttributes.addFlashAttribute("nqtError", "Ngày đi phải sau ngày đến!");
            redirectAttributes.addAttribute("phongId", nqtPhongId);
            redirectAttributes.addAttribute("ngayDen", nqtNgayDenStr);
            redirectAttributes.addAttribute("ngayDi", nqtNgayDiStr);
            return "redirect:/nqtDatPhong";
        }

        // Calculate total price
        long numberOfDays = ChronoUnit.DAYS.between(nqtNgayDen, nqtNgayDi);
        float roomPrice = room.getNqtLoaiPhong().getNqtGia() * numberOfDays;
        float totalPrice = roomPrice;

        // Tạo mã nội dung chuyển khoản ngẫu nhiên 10 ký tự (chữ in hoa + số)
        String noiDungChuyenKhoan = generateRandomCode(10);

        // Create booking
        NqtDatPhong booking = new NqtDatPhong();
        booking.setNqtNguoiDung(nqtCustomerUser);
        booking.setNqtPhong(room);
        booking.setNqtNgayDen(nqtNgayDen);
        booking.setNqtNgayDi(nqtNgayDi);
        booking.setNqtGhiChu(nqtGhiChu);
        booking.setNqtNoiDungChuyenKhoan(noiDungChuyenKhoan); // Lưu mã nội dung chuyển khoản
        booking.setNqtStatus((byte) 0); // Pending payment
        booking.setNqtNgayTao(java.time.LocalDateTime.now()); // Lưu thời gian tạo đơn

        // Save booking first to get ID
        NqtDatPhong savedBooking = nqtDatPhongRepository.save(booking);

        // Add services if selected
        if (nqtDichVuIds != null && !nqtDichVuIds.isEmpty()) {
            for (Integer dichVuId : nqtDichVuIds) {
                Optional<NqtDichVu> serviceOptional = nqtDichVuRepository.findById(dichVuId);
                if (serviceOptional.isPresent()) {
                    NqtDichVu service = serviceOptional.get();

                    NqtDonGiaDichVu servicePrice = new NqtDonGiaDichVu();
                    servicePrice.setNqtDatPhong(savedBooking);
                    servicePrice.setNqtDichVu(service);
                    servicePrice.setNqtSoLuong(1);
                    servicePrice.setNqtThanhTien(service.getNqtDonGia());

                    nqtDonGiaDichVuRepository.save(servicePrice);

                    totalPrice += service.getNqtDonGia();
                }
            }
        }

        // Apply discount: Mã giảm giá hoặc chiết khấu VIP
        float discountAmount = 0;
        NqtGiamGia appliedDiscount = null;

        // 1. Kiểm tra mã giảm giá nếu có
        if (nqtMaGiamGia != null && !nqtMaGiamGia.trim().isEmpty()) {
            Optional<NqtGiamGia> discountOptional = nqtGiamGiaRepository.findValidCodeForUser(
                    nqtMaGiamGia.trim(), nqtCustomerUser, LocalDate.now());
            
            if (discountOptional.isPresent()) {
                NqtGiamGia discount = discountOptional.get();
                
                // Kiểm tra giá trị tối thiểu
                if (discount.getNqtGiaTriToiThieu() == null || totalPrice >= discount.getNqtGiaTriToiThieu()) {
                    // Tính giảm giá
                    if (discount.getNqtLoaiGiam() == 0) { // Phần trăm
                        discountAmount = totalPrice * discount.getNqtGiaTriGiam() / 100;
                        // Áp dụng giới hạn tối đa nếu có
                        if (discount.getNqtGiaTriGiamToiDa() != null && discountAmount > discount.getNqtGiaTriGiamToiDa()) {
                            discountAmount = discount.getNqtGiaTriGiamToiDa();
                        }
                    } else { // Số tiền cố định
                        discountAmount = discount.getNqtGiaTriGiam();
                    }
                    
                    appliedDiscount = discount;
                }
            }
        }
        
        // 2. Nếu không có mã giảm giá, áp dụng chiết khấu VIP tự động
        if (appliedDiscount == null && "KhachVip".equals(nqtCustomerUser.getNqtCapBac())) {
            String vipDiscountPercentStr = nqtSettingService.getNqtValue("nqtVipDiscountPercent", "10");
            try {
                float vipDiscountPercent = Float.parseFloat(vipDiscountPercentStr);
                discountAmount = totalPrice * vipDiscountPercent / 100;
            } catch (NumberFormatException e) {
                // Ignore if setting is invalid
            }
        }

        // Cập nhật tổng tiền sau giảm giá
        float finalPrice = totalPrice - discountAmount;
        if (finalPrice < 0) {
            finalPrice = 0;
        }

        // Lưu thông tin giảm giá vào booking
        savedBooking.setNqtTongTien(finalPrice);
        savedBooking.setNqtGiamGia(discountAmount);
        if (appliedDiscount != null) {
            savedBooking.setNqtGiamGiaEntity(appliedDiscount);
            // Tăng số lần đã sử dụng
            appliedDiscount.setNqtSoLuongDaDung(appliedDiscount.getNqtSoLuongDaDung() + 1);
            nqtGiamGiaRepository.save(appliedDiscount);
        }
        
        nqtDatPhongRepository.save(savedBooking);

        // Update room status to booked
        room.setNqtStatus(false);
        nqtPhongRepository.save(room);

        return "redirect:/nqtDatPhong/xac-nhan/" + savedBooking.getNqtId();
    }

    // Booking Confirmation
    @GetMapping("/nqtDatPhong/xac-nhan/{id}")
    public String nqtDatPhongXacNhan(@PathVariable("id") Integer id,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(id);

        if (bookingOptional.isEmpty()
                || !bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
            return "redirect:/nqtDatPhongCuaToi";
        }

        NqtDatPhong booking = bookingOptional.get();
        
        // Lấy danh sách ngân hàng đang hoạt động
        List<k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse> nganHangList = nqtNganHangService.nqtGetActive();
        
        // Tạo Map chứa QR URL cho mỗi ngân hàng
        java.util.Map<Integer, String> qrUrlMap = new java.util.HashMap<>();
        
        // Lấy mã nội dung chuyển khoản từ booking đã lưu trong database
        String noiDung = booking.getNqtNoiDungChuyenKhoan();
        if (noiDung == null || noiDung.isEmpty()) {
            // Nếu chưa có, tạo mới và cập nhật lại (trường hợp cũ chưa có field này)
            noiDung = generateRandomCode(10);
            booking.setNqtNoiDungChuyenKhoan(noiDung);
            nqtDatPhongRepository.save(booking);
        }
        
        for (k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse nganHang : nganHangList) {
            String qrUrl = nqtNganHangService.generateVietQrUrl(
                nganHang.getNqtMaNganHang(),
                nganHang.getNqtSoTaiKhoan(),
                booking.getNqtTongTien() != null ? booking.getNqtTongTien() : 0f,
                noiDung
            );
            qrUrlMap.put(nganHang.getNqtId(), qrUrl);
        }

        model.addAttribute("booking", booking);
        model.addAttribute("nganHangList", nganHangList);
        model.addAttribute("qrUrlMap", qrUrlMap);
        model.addAttribute("noiDung", noiDung);
        model.addAttribute("isPaid", booking.getNqtStatus() != null && booking.getNqtStatus() == 1);
        return "nqtCustomer/nqtDatPhong/nqtConfirmation";
    }

    /**
     * API endpoint để kiểm tra trạng thái thanh toán
     */
    @GetMapping("/nqtDatPhong/check-payment/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPaymentStatus(@PathVariable("id") Integer id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
            
            if (nqtCustomerUser == null) {
                response.put("success", false);
                response.put("message", "Chưa đăng nhập");
                return ResponseEntity.ok(response);
            }

            Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(id);
            
            if (bookingOptional.isEmpty() || 
                !bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đơn đặt phòng");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra thanh toán
            boolean isPaid = nqtPaymentCheckService.checkPaymentStatus(id);
            
            response.put("success", true);
            response.put("isPaid", isPaid);
            response.put("message", isPaid ? "Đã thanh toán thành công" : "Chưa thanh toán");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // My Bookings
    @GetMapping("/nqtDatPhongCuaToi")
    public String nqtDatPhongCuaToi(HttpSession session, Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        List<NqtDatPhong> bookings = nqtDatPhongRepository.findByNqtNguoiDungOrderByNqtIdDesc(nqtCustomerUser);
        model.addAttribute("bookings", bookings);

        return "nqtCustomer/nqtDatPhong/nqtMyBookings";
    }

    // Booking Detail
    @GetMapping("/nqtDatPhongCuaToi/{id}")
    public String nqtDatPhongCuaToiDetail(@PathVariable("id") Integer id,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(id);

        if (bookingOptional.isEmpty()
                || !bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
            return "redirect:/nqtDatPhongCuaToi";
        }

        NqtDatPhong booking = bookingOptional.get();
        
        // Check if review exists
        List<NqtDanhGia> existingReviews = nqtDanhGiaRepository.findByNqtDatPhong(booking);
        boolean hasReview = !existingReviews.isEmpty();
        
        // Lấy thông tin ngân hàng và QR code nếu chưa thanh toán
        boolean isPaid = booking.getNqtStatus() != null && booking.getNqtStatus() == 1;
        if (!isPaid) {
            List<k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse> nganHangList = nqtNganHangService.nqtGetActive();
            java.util.Map<Integer, String> qrUrlMap = new java.util.HashMap<>();
            
            // Lấy mã nội dung chuyển khoản
            String noiDung = booking.getNqtNoiDungChuyenKhoan();
            if (noiDung == null || noiDung.isEmpty()) {
                noiDung = generateRandomCode(10);
                booking.setNqtNoiDungChuyenKhoan(noiDung);
                nqtDatPhongRepository.save(booking);
            }
            
            // Tạo QR URL cho mỗi ngân hàng
            for (k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse nganHang : nganHangList) {
                String qrUrl = nqtNganHangService.generateVietQrUrl(
                    nganHang.getNqtMaNganHang(),
                    nganHang.getNqtSoTaiKhoan(),
                    booking.getNqtTongTien() != null ? booking.getNqtTongTien() : 0f,
                    noiDung
                );
                qrUrlMap.put(nganHang.getNqtId(), qrUrl);
            }
            
            model.addAttribute("nganHangList", nganHangList);
            model.addAttribute("qrUrlMap", qrUrlMap);
            model.addAttribute("noiDung", noiDung);
        }
        
        // Kiểm tra xem có thể hủy đặt phòng không
        // Chỉ cho phép hủy nếu: chưa thanh toán (status = 0) và ngày đến chưa đến
        boolean canCancel = false;
        if (booking.getNqtStatus() != null && booking.getNqtStatus() == 0) {
            canCancel = booking.getNqtNgayDen().isAfter(LocalDate.now());
        }
        
        model.addAttribute("booking", booking);
        model.addAttribute("hasReview", hasReview);
        model.addAttribute("isPaid", isPaid);
        model.addAttribute("canCancel", canCancel);
        if (hasReview) {
            model.addAttribute("review", existingReviews.get(0));
        }
        return "nqtCustomer/nqtDatPhong/nqtDetail";
    }

    // Cancel Booking
    @PostMapping("/nqtDatPhong/{id}/huy")
    public String nqtHuyDatPhong(@PathVariable("id") Integer id,
            HttpSession session,
            Model model) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");

        if (nqtCustomerUser == null) {
            return "redirect:/nqtDangNhap";
        }

        Optional<NqtDatPhong> bookingOptional = nqtDatPhongRepository.findById(id);

        if (bookingOptional.isPresent()
                && bookingOptional.get().getNqtNguoiDung().getNqtId().equals(nqtCustomerUser.getNqtId())) {
            NqtDatPhong booking = bookingOptional.get();

            // Only allow cancellation if not yet checked in
            if (booking.getNqtNgayDen().isAfter(LocalDate.now())) {
                booking.setNqtStatus((byte) 2); // Đã hủy
                nqtDatPhongRepository.save(booking);

                // Make room available again
                NqtPhong room = booking.getNqtPhong();
                room.setNqtStatus(true);
                nqtPhongRepository.save(room);

                model.addAttribute("nqtSuccess", "Hủy đặt phòng thành công!");
            } else {
                model.addAttribute("nqtError", "Không thể hủy đặt phòng đã quá hạn!");
            }
        }

        return "redirect:/nqtDatPhongCuaToi";
    }

    // API: Check discount code
    @GetMapping("/api/check-discount-code")
    @ResponseBody
    public ResponseEntity<?> checkDiscountCode(
            @RequestParam("code") String code,
            @RequestParam(value = "totalPrice", required = false) Float totalPrice,
            HttpSession session) {
        NqtNguoiDung nqtCustomerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
        
        if (nqtCustomerUser == null) {
            return ResponseEntity.ok().body("{\"valid\": false, \"message\": \"Vui lòng đăng nhập\"}");
        }

        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.ok().body("{\"valid\": false, \"message\": \"Vui lòng nhập mã giảm giá\"}");
        }

        Optional<NqtGiamGia> discountOptional = nqtGiamGiaRepository.findValidCodeForUser(
                code.trim(), nqtCustomerUser, LocalDate.now());

        if (discountOptional.isEmpty()) {
            return ResponseEntity.ok().body("{\"valid\": false, \"message\": \"Mã giảm giá không hợp lệ hoặc đã hết hạn\"}");
        }

        NqtGiamGia discount = discountOptional.get();
        
        // Kiểm tra giá trị tối thiểu nếu có
        if (totalPrice != null && discount.getNqtGiaTriToiThieu() != null 
                && totalPrice < discount.getNqtGiaTriToiThieu()) {
            return ResponseEntity.ok().body("{\"valid\": false, \"message\": \"Đơn hàng phải đạt tối thiểu " 
                    + String.format("%.0f", discount.getNqtGiaTriToiThieu()) + " VNĐ\"}");
        }

        // Tính toán giảm giá
        float discountAmount = 0;
        if (totalPrice != null) {
            if (discount.getNqtLoaiGiam() == 0) { // Phần trăm
                discountAmount = totalPrice * discount.getNqtGiaTriGiam() / 100;
                if (discount.getNqtGiaTriGiamToiDa() != null && discountAmount > discount.getNqtGiaTriGiamToiDa()) {
                    discountAmount = discount.getNqtGiaTriGiamToiDa();
                }
            } else { // Số tiền cố định
                discountAmount = discount.getNqtGiaTriGiam();
            }
        }

        String discountText = "";
        if (discount.getNqtLoaiGiam() == 0) {
            discountText = "Giảm " + String.format("%.0f", discount.getNqtGiaTriGiam()) + "%";
            if (discount.getNqtGiaTriGiamToiDa() != null) {
                discountText += " (tối đa " + String.format("%.0f", discount.getNqtGiaTriGiamToiDa()) + " VNĐ)";
            }
        } else {
            discountText = "Giảm " + String.format("%.0f", discount.getNqtGiaTriGiam()) + " VNĐ";
        }

        if (totalPrice != null) {
            discountText += " - Tiết kiệm: " + String.format("%.0f", discountAmount) + " VNĐ";
        }

        return ResponseEntity.ok().body("{\"valid\": true, \"message\": \"" + discountText + "\", \"discountAmount\": " + discountAmount + "}");
    }

    /**
     * Tạo mã ngẫu nhiên gồm chữ in hoa và số
     * @param length Độ dài mã cần tạo
     * @return Chuỗi ngẫu nhiên
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }
}
