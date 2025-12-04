package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtDatPhongRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDatPhongResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtDatPhongService {

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    public List<NqtDatPhongResponse> nqtGetAll() {
        return nqtDatPhongRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public NqtDatPhongResponse nqtGetById(Integer nqtId) {
        NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + nqtId));
        return nqtConvertToResponse(nqtDatPhong);
    }

    public NqtDatPhongResponse nqtCreate(NqtDatPhongRequest nqtRequest) {
        NqtDatPhong nqtDatPhong = new NqtDatPhong();

        if (nqtRequest.getNqtNguoiDungId() != null) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungRepository.findById(nqtRequest.getNqtNguoiDungId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy người dùng với ID: " + nqtRequest.getNqtNguoiDungId()));
            nqtDatPhong.setNqtNguoiDung(nqtNguoiDung);
        }

        if (nqtRequest.getNqtPhongId() != null) {
            NqtPhong nqtPhong = nqtPhongRepository.findById(nqtRequest.getNqtPhongId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy phòng với ID: " + nqtRequest.getNqtPhongId()));
            nqtDatPhong.setNqtPhong(nqtPhong);
        }

        nqtDatPhong.setNqtNgayDen(nqtRequest.getNqtNgayDen());
        nqtDatPhong.setNqtNgayDi(nqtRequest.getNqtNgayDi());
        nqtDatPhong.setNqtTongTien(nqtRequest.getNqtTongTien());
        nqtDatPhong.setNqtGhiChu(nqtRequest.getNqtGhiChu());
        nqtDatPhong.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : (byte) 0);

        NqtDatPhong nqtSaved = nqtDatPhongRepository.save(nqtDatPhong);
        updateRoomStatus(nqtSaved);
        return nqtConvertToResponse(nqtSaved);
    }

    public NqtDatPhongResponse nqtUpdate(Integer nqtId, NqtDatPhongRequest nqtRequest) {
        NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + nqtId));

        if (nqtRequest.getNqtNguoiDungId() != null) {
            NqtNguoiDung nqtNguoiDung = nqtNguoiDungRepository.findById(nqtRequest.getNqtNguoiDungId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy người dùng với ID: " + nqtRequest.getNqtNguoiDungId()));
            nqtDatPhong.setNqtNguoiDung(nqtNguoiDung);
        }

        if (nqtRequest.getNqtPhongId() != null) {
            NqtPhong nqtPhong = nqtPhongRepository.findById(nqtRequest.getNqtPhongId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy phòng với ID: " + nqtRequest.getNqtPhongId()));
            nqtDatPhong.setNqtPhong(nqtPhong);
        }

        if (nqtRequest.getNqtNgayDen() != null) {
            nqtDatPhong.setNqtNgayDen(nqtRequest.getNqtNgayDen());
        }
        if (nqtRequest.getNqtNgayDi() != null) {
            nqtDatPhong.setNqtNgayDi(nqtRequest.getNqtNgayDi());
        }
        if (nqtRequest.getNqtTongTien() != null) {
            nqtDatPhong.setNqtTongTien(nqtRequest.getNqtTongTien());
        }
        if (nqtRequest.getNqtGhiChu() != null) {
            nqtDatPhong.setNqtGhiChu(nqtRequest.getNqtGhiChu());
        }
        if (nqtRequest.getNqtStatus() != null) {
            nqtDatPhong.setNqtStatus(nqtRequest.getNqtStatus());
        }

        NqtDatPhong nqtUpdated = nqtDatPhongRepository.save(nqtDatPhong);
        updateRoomStatus(nqtUpdated);
        return nqtConvertToResponse(nqtUpdated);
    }

    public void nqtDelete(Integer nqtId) {
        if (!nqtDatPhongRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + nqtId);
        }
        nqtDatPhongRepository.deleteById(nqtId);
    }

    private NqtDatPhongResponse nqtConvertToResponse(NqtDatPhong nqtDatPhong) {
        NqtDatPhongResponse nqtResponse = new NqtDatPhongResponse();
        nqtResponse.setNqtId(nqtDatPhong.getNqtId());
        if (nqtDatPhong.getNqtNguoiDung() != null) {
            nqtResponse.setNqtNguoiDungId(nqtDatPhong.getNqtNguoiDung().getNqtId());
            nqtResponse.setNqtTenNguoiDung(nqtDatPhong.getNqtNguoiDung().getNqtHoVaTen());
        }
        if (nqtDatPhong.getNqtPhong() != null) {
            nqtResponse.setNqtPhongId(nqtDatPhong.getNqtPhong().getNqtId());
            nqtResponse.setNqtSoPhong(nqtDatPhong.getNqtPhong().getNqtSoPhong());
        }
        nqtResponse.setNqtNgayDen(nqtDatPhong.getNqtNgayDen());
        nqtResponse.setNqtNgayDi(nqtDatPhong.getNqtNgayDi());
        nqtResponse.setNqtTongTien(nqtDatPhong.getNqtTongTien());
        nqtResponse.setNqtGhiChu(nqtDatPhong.getNqtGhiChu());
        nqtResponse.setNqtStatus(nqtDatPhong.getNqtStatus());
        return nqtResponse;
    }

    private void updateRoomStatus(NqtDatPhong nqtDatPhong) {
        if (nqtDatPhong.getNqtPhong() != null) {
            NqtPhong nqtPhong = nqtDatPhong.getNqtPhong();
            // Nếu trạng thái là Đã thanh toán (1)
            if (nqtDatPhong.getNqtStatus() == 1) {
                java.time.LocalDate today = java.time.LocalDate.now();
                // Và ngày hiện tại nằm trong khoảng ngày đến - ngày đi
                if (!today.isBefore(nqtDatPhong.getNqtNgayDen()) && !today.isAfter(nqtDatPhong.getNqtNgayDi())) {
                    nqtPhong.setNqtStatus(false); // 0-Đã được book
                    nqtPhongRepository.save(nqtPhong);
                }
            }
        }
    }
}
