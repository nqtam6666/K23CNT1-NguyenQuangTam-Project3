package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtGiamGiaRequest;
import k23cnt1.nqt.project3.nqtDto.NqtGiamGiaResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtGiamGia;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtGiamGiaRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtGiamGiaService {

    @Autowired
    private NqtGiamGiaRepository nqtGiamGiaRepository;

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    public List<NqtGiamGiaResponse> nqtGetAll() {
        return nqtGiamGiaRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public NqtGiamGiaResponse nqtGetById(Integer nqtId) {
        NqtGiamGia nqtGiamGia = nqtGiamGiaRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + nqtId));
        return nqtConvertToResponse(nqtGiamGia);
    }

    public NqtGiamGiaResponse nqtCreate(NqtGiamGiaRequest nqtRequest) {
        // Kiểm tra mã giảm giá đã tồn tại chưa
        if (nqtGiamGiaRepository.findByNqtMaGiamGia(nqtRequest.getNqtMaGiamGia()).isPresent()) {
            throw new RuntimeException("Mã giảm giá đã tồn tại: " + nqtRequest.getNqtMaGiamGia());
        }

        NqtGiamGia nqtGiamGia = new NqtGiamGia();
        nqtGiamGia.setNqtMaGiamGia(nqtRequest.getNqtMaGiamGia());
        nqtGiamGia.setNqtMoTa(nqtRequest.getNqtMoTa());
        nqtGiamGia.setNqtLoaiGiam(nqtRequest.getNqtLoaiGiam() != null ? nqtRequest.getNqtLoaiGiam() : (byte) 0);
        nqtGiamGia.setNqtGiaTriGiam(nqtRequest.getNqtGiaTriGiam());
        nqtGiamGia.setNqtGiaTriToiThieu(nqtRequest.getNqtGiaTriToiThieu());
        nqtGiamGia.setNqtGiaTriGiamToiDa(nqtRequest.getNqtGiaTriGiamToiDa());
        nqtGiamGia.setNqtNgayBatDau(nqtRequest.getNqtNgayBatDau());
        nqtGiamGia.setNqtNgayKetThuc(nqtRequest.getNqtNgayKetThuc());
        nqtGiamGia.setNqtSoLuongToiDa(nqtRequest.getNqtSoLuongToiDa());
        nqtGiamGia.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtGiamGia.setNqtSoLuongDaDung(0);
        nqtGiamGia.setNqtChiChoVip(nqtRequest.getNqtChiChoVip() != null ? nqtRequest.getNqtChiChoVip() : false);

        // Set người dùng nếu có
        if (nqtRequest.getNqtNguoiDungId() != null) {
            NqtNguoiDung nguoiDung = nqtNguoiDungRepository.findById(nqtRequest.getNqtNguoiDungId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + nqtRequest.getNqtNguoiDungId()));
            nqtGiamGia.setNqtNguoiDung(nguoiDung);
        } else {
            nqtGiamGia.setNqtNguoiDung(null);
        }

        NqtGiamGia nqtSaved = nqtGiamGiaRepository.save(nqtGiamGia);
        return nqtConvertToResponse(nqtSaved);
    }

    public NqtGiamGiaResponse nqtUpdate(Integer nqtId, NqtGiamGiaRequest nqtRequest) {
        NqtGiamGia nqtGiamGia = nqtGiamGiaRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + nqtId));

        // Kiểm tra mã giảm giá đã tồn tại chưa (trừ chính nó)
        nqtGiamGiaRepository.findByNqtMaGiamGia(nqtRequest.getNqtMaGiamGia())
                .ifPresent(existing -> {
                    if (!existing.getNqtId().equals(nqtId)) {
                        throw new RuntimeException("Mã giảm giá đã tồn tại: " + nqtRequest.getNqtMaGiamGia());
                    }
                });

        nqtGiamGia.setNqtMaGiamGia(nqtRequest.getNqtMaGiamGia());
        nqtGiamGia.setNqtMoTa(nqtRequest.getNqtMoTa());
        nqtGiamGia.setNqtLoaiGiam(nqtRequest.getNqtLoaiGiam() != null ? nqtRequest.getNqtLoaiGiam() : (byte) 0);
        nqtGiamGia.setNqtGiaTriGiam(nqtRequest.getNqtGiaTriGiam());
        nqtGiamGia.setNqtGiaTriToiThieu(nqtRequest.getNqtGiaTriToiThieu());
        nqtGiamGia.setNqtGiaTriGiamToiDa(nqtRequest.getNqtGiaTriGiamToiDa());
        nqtGiamGia.setNqtNgayBatDau(nqtRequest.getNqtNgayBatDau());
        nqtGiamGia.setNqtNgayKetThuc(nqtRequest.getNqtNgayKetThuc());
        nqtGiamGia.setNqtSoLuongToiDa(nqtRequest.getNqtSoLuongToiDa());
        if (nqtRequest.getNqtStatus() != null) {
            nqtGiamGia.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtChiChoVip() != null) {
            nqtGiamGia.setNqtChiChoVip(nqtRequest.getNqtChiChoVip());
        }

        // Set người dùng nếu có
        if (nqtRequest.getNqtNguoiDungId() != null) {
            NqtNguoiDung nguoiDung = nqtNguoiDungRepository.findById(nqtRequest.getNqtNguoiDungId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + nqtRequest.getNqtNguoiDungId()));
            nqtGiamGia.setNqtNguoiDung(nguoiDung);
        } else {
            nqtGiamGia.setNqtNguoiDung(null);
        }

        NqtGiamGia nqtUpdated = nqtGiamGiaRepository.save(nqtGiamGia);
        return nqtConvertToResponse(nqtUpdated);
    }

    public void nqtDelete(Integer nqtId) {
        if (!nqtGiamGiaRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy mã giảm giá với ID: " + nqtId);
        }
        nqtGiamGiaRepository.deleteById(nqtId);
    }

    /**
     * Lấy danh sách voucher đang active và hợp lệ (trong thời gian áp dụng, chưa hết số lượng)
     * Chỉ lấy voucher chung (không gắn với người dùng cụ thể)
     */
    public List<NqtGiamGiaResponse> nqtGetActiveVouchers() {
        LocalDate today = LocalDate.now();
        return nqtGiamGiaRepository.findByNqtStatusTrue().stream()
                .filter(voucher -> {
                    // Chỉ lấy voucher chung (không gắn với người dùng cụ thể)
                    if (voucher.getNqtNguoiDung() != null) {
                        return false;
                    }
                    // Kiểm tra thời gian áp dụng
                    if (voucher.getNqtNgayBatDau() != null && voucher.getNqtNgayBatDau().isAfter(today)) {
                        return false;
                    }
                    if (voucher.getNqtNgayKetThuc() != null && voucher.getNqtNgayKetThuc().isBefore(today)) {
                        return false;
                    }
                    // Kiểm tra số lượng còn lại
                    if (voucher.getNqtSoLuongToiDa() != null) {
                        if (voucher.getNqtSoLuongDaDung() == null || 
                            voucher.getNqtSoLuongDaDung() >= voucher.getNqtSoLuongToiDa()) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    private NqtGiamGiaResponse nqtConvertToResponse(NqtGiamGia nqtGiamGia) {
        NqtGiamGiaResponse nqtResponse = new NqtGiamGiaResponse();
        nqtResponse.setNqtId(nqtGiamGia.getNqtId());
        nqtResponse.setNqtMaGiamGia(nqtGiamGia.getNqtMaGiamGia());
        nqtResponse.setNqtMoTa(nqtGiamGia.getNqtMoTa());
        nqtResponse.setNqtLoaiGiam(nqtGiamGia.getNqtLoaiGiam());
        nqtResponse.setNqtGiaTriGiam(nqtGiamGia.getNqtGiaTriGiam());
        nqtResponse.setNqtGiaTriToiThieu(nqtGiamGia.getNqtGiaTriToiThieu());
        nqtResponse.setNqtGiaTriGiamToiDa(nqtGiamGia.getNqtGiaTriGiamToiDa());
        nqtResponse.setNqtNgayBatDau(nqtGiamGia.getNqtNgayBatDau());
        nqtResponse.setNqtNgayKetThuc(nqtGiamGia.getNqtNgayKetThuc());
        nqtResponse.setNqtSoLuongToiDa(nqtGiamGia.getNqtSoLuongToiDa());
        nqtResponse.setNqtSoLuongDaDung(nqtGiamGia.getNqtSoLuongDaDung());
        nqtResponse.setNqtStatus(nqtGiamGia.getNqtStatus());
        nqtResponse.setNqtNgayTao(nqtGiamGia.getNqtNgayTao());

        if (nqtGiamGia.getNqtNguoiDung() != null) {
            nqtResponse.setNqtNguoiDungId(nqtGiamGia.getNqtNguoiDung().getNqtId());
            nqtResponse.setNqtNguoiDungTen(nqtGiamGia.getNqtNguoiDung().getNqtHoVaTen());
        } else {
            nqtResponse.setNqtNguoiDungId(null);
            nqtResponse.setNqtNguoiDungTen(null);
        }
        nqtResponse.setNqtChiChoVip(nqtGiamGia.getNqtChiChoVip() != null ? nqtGiamGia.getNqtChiChoVip() : false);

        return nqtResponse;
    }
}

