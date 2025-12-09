package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtNganHangRequest;
import k23cnt1.nqt.project3.nqtDto.NqtNganHangResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtNganHang;
import k23cnt1.nqt.project3.nqtRepository.NqtNganHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtNganHangService {

    @Autowired
    private NqtNganHangRepository nqtNganHangRepository;

    public List<NqtNganHangResponse> nqtGetAll() {
        return nqtNganHangRepository.findAllByOrderByNqtThuTuAsc().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public List<NqtNganHangResponse> nqtGetActive() {
        return nqtNganHangRepository.findByNqtStatusTrueOrderByNqtThuTuAsc().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public NqtNganHangResponse nqtGetById(Integer nqtId) {
        NqtNganHang nqtNganHang = nqtNganHangRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngân hàng với ID: " + nqtId));
        return nqtConvertToResponse(nqtNganHang);
    }

    public NqtNganHangResponse nqtCreate(NqtNganHangRequest nqtRequest) {
        // Kiểm tra mã ngân hàng đã tồn tại chưa
        if (nqtNganHangRepository.existsByNqtMaNganHang(nqtRequest.getNqtMaNganHang())) {
            throw new RuntimeException("Mã ngân hàng đã tồn tại: " + nqtRequest.getNqtMaNganHang());
        }

        NqtNganHang nqtNganHang = new NqtNganHang();
        nqtNganHang.setNqtTenNganHang(nqtRequest.getNqtTenNganHang());
        nqtNganHang.setNqtMaNganHang(nqtRequest.getNqtMaNganHang().toUpperCase());
        nqtNganHang.setNqtSoTaiKhoan(nqtRequest.getNqtSoTaiKhoan());
        nqtNganHang.setNqtTenChuTaiKhoan(nqtRequest.getNqtTenChuTaiKhoan());
        nqtNganHang.setNqtChiNhanh(nqtRequest.getNqtChiNhanh());
        nqtNganHang.setNqtGhiChu(nqtRequest.getNqtGhiChu());
        nqtNganHang.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtNganHang.setNqtThuTu(nqtRequest.getNqtThuTu() != null ? nqtRequest.getNqtThuTu() : 0);

        NqtNganHang nqtSaved = nqtNganHangRepository.save(nqtNganHang);
        return nqtConvertToResponse(nqtSaved);
    }

    public NqtNganHangResponse nqtUpdate(Integer nqtId, NqtNganHangRequest nqtRequest) {
        NqtNganHang nqtNganHang = nqtNganHangRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngân hàng với ID: " + nqtId));

        // Kiểm tra mã ngân hàng đã tồn tại chưa (trừ chính nó)
        nqtNganHangRepository.findByNqtMaNganHang(nqtRequest.getNqtMaNganHang())
                .ifPresent(existing -> {
                    if (!existing.getNqtId().equals(nqtId)) {
                        throw new RuntimeException("Mã ngân hàng đã tồn tại: " + nqtRequest.getNqtMaNganHang());
                    }
                });

        nqtNganHang.setNqtTenNganHang(nqtRequest.getNqtTenNganHang());
        nqtNganHang.setNqtMaNganHang(nqtRequest.getNqtMaNganHang().toUpperCase());
        nqtNganHang.setNqtSoTaiKhoan(nqtRequest.getNqtSoTaiKhoan());
        nqtNganHang.setNqtTenChuTaiKhoan(nqtRequest.getNqtTenChuTaiKhoan());
        nqtNganHang.setNqtChiNhanh(nqtRequest.getNqtChiNhanh());
        nqtNganHang.setNqtGhiChu(nqtRequest.getNqtGhiChu());
        if (nqtRequest.getNqtStatus() != null) {
            nqtNganHang.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtThuTu() != null) {
            nqtNganHang.setNqtThuTu(nqtRequest.getNqtThuTu());
        }

        NqtNganHang nqtUpdated = nqtNganHangRepository.save(nqtNganHang);
        return nqtConvertToResponse(nqtUpdated);
    }

    public void nqtDelete(Integer nqtId) {
        if (!nqtNganHangRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy ngân hàng với ID: " + nqtId);
        }
        nqtNganHangRepository.deleteById(nqtId);
    }

    /**
     * Tạo URL QR code từ VietQR API
     * Format: https://api.vietqr.io/{MÃ NGÂN HÀNG}/{STK}/{SỐ TIỀN}/{NỘI DUNG}/vietqr_net_2.jpg
     */
    public String generateVietQrUrl(String maNganHang, String soTaiKhoan, Float soTien, String noiDung) {
        // URL encode các tham số
        String encodedMaNganHang = java.net.URLEncoder.encode(maNganHang, java.nio.charset.StandardCharsets.UTF_8);
        String encodedSoTaiKhoan = java.net.URLEncoder.encode(soTaiKhoan, java.nio.charset.StandardCharsets.UTF_8);
        String encodedSoTien = java.net.URLEncoder.encode(String.format("%.0f", soTien), java.nio.charset.StandardCharsets.UTF_8);
        String encodedNoiDung = java.net.URLEncoder.encode(noiDung, java.nio.charset.StandardCharsets.UTF_8);
        
        return String.format("https://api.vietqr.io/%s/%s/%s/%s/vietqr_net_2.jpg",
                encodedMaNganHang, encodedSoTaiKhoan, encodedSoTien, encodedNoiDung);
    }

    private NqtNganHangResponse nqtConvertToResponse(NqtNganHang nqtNganHang) {
        NqtNganHangResponse nqtResponse = new NqtNganHangResponse();
        nqtResponse.setNqtId(nqtNganHang.getNqtId());
        nqtResponse.setNqtTenNganHang(nqtNganHang.getNqtTenNganHang());
        nqtResponse.setNqtMaNganHang(nqtNganHang.getNqtMaNganHang());
        nqtResponse.setNqtSoTaiKhoan(nqtNganHang.getNqtSoTaiKhoan());
        nqtResponse.setNqtTenChuTaiKhoan(nqtNganHang.getNqtTenChuTaiKhoan());
        nqtResponse.setNqtChiNhanh(nqtNganHang.getNqtChiNhanh());
        nqtResponse.setNqtGhiChu(nqtNganHang.getNqtGhiChu());
        nqtResponse.setNqtStatus(nqtNganHang.getNqtStatus());
        nqtResponse.setNqtThuTu(nqtNganHang.getNqtThuTu());
        nqtResponse.setNqtNgayTao(nqtNganHang.getNqtNgayTao());
        return nqtResponse;
    }
}

