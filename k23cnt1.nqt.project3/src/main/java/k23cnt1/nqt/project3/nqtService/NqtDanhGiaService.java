package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtDanhGiaRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDanhGiaResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtDanhGia;
import k23cnt1.nqt.project3.nqtEntity.NqtDatPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtDanhGiaRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtDanhGiaService {
    
    @Autowired
    private NqtDanhGiaRepository nqtDanhGiaRepository;
    
    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;
    
    public List<NqtDanhGiaResponse> nqtGetAll() {
        return nqtDanhGiaRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }
    
    public NqtDanhGiaResponse nqtGetById(Integer nqtId) {
        NqtDanhGia nqtDanhGia = nqtDanhGiaRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + nqtId));
        return nqtConvertToResponse(nqtDanhGia);
    }
    
    public NqtDanhGiaResponse nqtCreate(NqtDanhGiaRequest nqtRequest) {
        NqtDanhGia nqtDanhGia = new NqtDanhGia();
        
        if (nqtRequest.getNqtDatPhongId() != null) {
            NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtRequest.getNqtDatPhongId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + nqtRequest.getNqtDatPhongId()));
            nqtDanhGia.setNqtDatPhong(nqtDatPhong);
        }
        
        nqtDanhGia.setNqtNoiDungDanhGia(nqtRequest.getNqtNoiDungDanhGia());
        nqtDanhGia.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtDanhGia.setNqtNgayDanhGia(LocalDateTime.now());
        
        NqtDanhGia nqtSaved = nqtDanhGiaRepository.save(nqtDanhGia);
        return nqtConvertToResponse(nqtSaved);
    }
    
    public NqtDanhGiaResponse nqtUpdate(Integer nqtId, NqtDanhGiaRequest nqtRequest) {
        NqtDanhGia nqtDanhGia = nqtDanhGiaRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + nqtId));
        
        if (nqtRequest.getNqtDatPhongId() != null) {
            NqtDatPhong nqtDatPhong = nqtDatPhongRepository.findById(nqtRequest.getNqtDatPhongId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + nqtRequest.getNqtDatPhongId()));
            nqtDanhGia.setNqtDatPhong(nqtDatPhong);
        }
        
        if (nqtRequest.getNqtNoiDungDanhGia() != null) {
            nqtDanhGia.setNqtNoiDungDanhGia(nqtRequest.getNqtNoiDungDanhGia());
        }
        if (nqtRequest.getNqtStatus() != null) {
            nqtDanhGia.setNqtStatus(nqtRequest.getNqtStatus());
        }
        
        NqtDanhGia nqtUpdated = nqtDanhGiaRepository.save(nqtDanhGia);
        return nqtConvertToResponse(nqtUpdated);
    }
    
    public void nqtDelete(Integer nqtId) {
        if (!nqtDanhGiaRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy đánh giá với ID: " + nqtId);
        }
        nqtDanhGiaRepository.deleteById(nqtId);
    }
    
    private NqtDanhGiaResponse nqtConvertToResponse(NqtDanhGia nqtDanhGia) {
        NqtDanhGiaResponse nqtResponse = new NqtDanhGiaResponse();
        nqtResponse.setNqtId(nqtDanhGia.getNqtId());
        if (nqtDanhGia.getNqtDatPhong() != null) {
            nqtResponse.setNqtDatPhongId(nqtDanhGia.getNqtDatPhong().getNqtId());
            if (nqtDanhGia.getNqtDatPhong().getNqtPhong() != null) {
                nqtResponse.setNqtSoPhong(nqtDanhGia.getNqtDatPhong().getNqtPhong().getNqtSoPhong());
            }
            if (nqtDanhGia.getNqtDatPhong().getNqtNguoiDung() != null) {
                nqtResponse.setNqtTenNguoiDung(nqtDanhGia.getNqtDatPhong().getNqtNguoiDung().getNqtHoVaTen());
            }
        }
        nqtResponse.setNqtNoiDungDanhGia(nqtDanhGia.getNqtNoiDungDanhGia());
        nqtResponse.setNqtStatus(nqtDanhGia.getNqtStatus());
        nqtResponse.setNqtNgayDanhGia(nqtDanhGia.getNqtNgayDanhGia());
        return nqtResponse;
    }
}

