package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtDonGiaDichVuRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDonGiaDichVuResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtDichVu;
import k23cnt1.nqt.project3.nqtEntity.NqtDonGiaDichVu;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtDichVuRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtDonGiaDichVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtDonGiaDichVuService {

    @Autowired
    private NqtDonGiaDichVuRepository nqtDonGiaDichVuRepository;

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @Autowired
    private NqtDichVuRepository nqtDichVuRepository;

    public List<NqtDonGiaDichVuResponse> nqtGetAll() {
        return nqtDonGiaDichVuRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public NqtDonGiaDichVuResponse nqtGetById(Integer nqtId) {
        NqtDonGiaDichVu nqtDonGiaDichVu = nqtDonGiaDichVuRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giá dịch vụ với ID: " + nqtId));
        return nqtConvertToResponse(nqtDonGiaDichVu);
    }

    public NqtDonGiaDichVuResponse nqtCreate(NqtDonGiaDichVuRequest nqtRequest) {
        NqtDonGiaDichVu nqtDonGiaDichVu = new NqtDonGiaDichVu();
        nqtDonGiaDichVu.setNqtSoLuong(nqtRequest.getNqtSoLuong() != null ? nqtRequest.getNqtSoLuong() : 1);
        nqtDonGiaDichVu.setNqtThanhTien(nqtRequest.getNqtThanhTien());

        if (nqtRequest.getNqtDatPhongId() != null) {
            NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtRequest.getNqtDatPhongId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy đặt phòng với ID: " + nqtRequest.getNqtDatPhongId()));
            nqtDonGiaDichVu.setNqtDatPhong(nqtDatPhong);
        }

        if (nqtRequest.getNqtDichVuId() != null) {
            NqtDichVu nqtDichVu = nqtDichVuRepository.findById(nqtRequest.getNqtDichVuId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy dịch vụ với ID: " + nqtRequest.getNqtDichVuId()));
            nqtDonGiaDichVu.setNqtDichVu(nqtDichVu);
            // Auto-calculate Total Amount
            nqtDonGiaDichVu.setNqtThanhTien(nqtDichVu.getNqtDonGia() * nqtDonGiaDichVu.getNqtSoLuong());
        }

        NqtDonGiaDichVu nqtSaved = nqtDonGiaDichVuRepository.save(nqtDonGiaDichVu);
        return nqtConvertToResponse(nqtSaved);
    }

    public NqtDonGiaDichVuResponse nqtUpdate(Integer nqtId, NqtDonGiaDichVuRequest nqtRequest) {
        NqtDonGiaDichVu nqtDonGiaDichVu = nqtDonGiaDichVuRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn giá dịch vụ với ID: " + nqtId));

        if (nqtRequest.getNqtSoLuong() != null) {
            nqtDonGiaDichVu.setNqtSoLuong(nqtRequest.getNqtSoLuong());
        }
        if (nqtRequest.getNqtThanhTien() != null) {
            nqtDonGiaDichVu.setNqtThanhTien(nqtRequest.getNqtThanhTien());
        }

        if (nqtRequest.getNqtDatPhongId() != null) {
            NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtRequest.getNqtDatPhongId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy đặt phòng với ID: " + nqtRequest.getNqtDatPhongId()));
            nqtDonGiaDichVu.setNqtDatPhong(nqtDatPhong);
        }

        if (nqtRequest.getNqtDichVuId() != null) {
            NqtDichVu nqtDichVu = nqtDichVuRepository.findById(nqtRequest.getNqtDichVuId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy dịch vụ với ID: " + nqtRequest.getNqtDichVuId()));
            nqtDonGiaDichVu.setNqtDichVu(nqtDichVu);
        }

        // Recalculate Total Amount if Quantity or Service changed
        if (nqtDonGiaDichVu.getNqtDichVu() != null && nqtDonGiaDichVu.getNqtSoLuong() != null) {
            nqtDonGiaDichVu
                    .setNqtThanhTien(nqtDonGiaDichVu.getNqtDichVu().getNqtDonGia() * nqtDonGiaDichVu.getNqtSoLuong());
        }

        NqtDonGiaDichVu nqtUpdated = nqtDonGiaDichVuRepository.save(nqtDonGiaDichVu);
        return nqtConvertToResponse(nqtUpdated);
    }

    public void nqtDelete(Integer nqtId) {
        if (!nqtDonGiaDichVuRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy đơn giá dịch vụ với ID: " + nqtId);
        }
        nqtDonGiaDichVuRepository.deleteById(nqtId);
    }

    private NqtDonGiaDichVuResponse nqtConvertToResponse(NqtDonGiaDichVu nqtDonGiaDichVu) {
        NqtDonGiaDichVuResponse nqtResponse = new NqtDonGiaDichVuResponse();
        nqtResponse.setNqtId(nqtDonGiaDichVu.getNqtId());
        nqtResponse.setNqtSoLuong(nqtDonGiaDichVu.getNqtSoLuong());
        nqtResponse.setNqtThanhTien(nqtDonGiaDichVu.getNqtThanhTien());
        if (nqtDonGiaDichVu.getNqtDatPhong() != null) {
            nqtResponse.setNqtDatPhongId(nqtDonGiaDichVu.getNqtDatPhong().getNqtId());
            if (nqtDonGiaDichVu.getNqtDatPhong().getNqtPhong() != null) {
                nqtResponse.setNqtSoPhong(nqtDonGiaDichVu.getNqtDatPhong().getNqtPhong().getNqtSoPhong());
            }
        }
        if (nqtDonGiaDichVu.getNqtDichVu() != null) {
            nqtResponse.setNqtDichVuId(nqtDonGiaDichVu.getNqtDichVu().getNqtId());
            nqtResponse.setNqtTenDichVu(nqtDonGiaDichVu.getNqtDichVu().getNqtTen());
        }
        return nqtResponse;
    }
}
