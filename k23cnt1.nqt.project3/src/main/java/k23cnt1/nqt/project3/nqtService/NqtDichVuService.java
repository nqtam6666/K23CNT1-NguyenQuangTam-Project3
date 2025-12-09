package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtDichVuRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDichVuResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtDichVu;
import k23cnt1.nqt.project3.nqtRepository.NqtDichVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtDichVuService {
    
    @Autowired
    private NqtDichVuRepository nqtDichVuRepository;
    
    public List<NqtDichVuResponse> nqtGetAll() {
        return nqtDichVuRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }
    
    public NqtDichVuResponse nqtGetById(Integer nqtId) {
        NqtDichVu nqtDichVu = nqtDichVuRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + nqtId));
        return nqtConvertToResponse(nqtDichVu);
    }
    
    public NqtDichVuResponse nqtCreate(NqtDichVuRequest nqtRequest) {
        NqtDichVu nqtDichVu = new NqtDichVu();
        nqtDichVu.setNqtTen(nqtRequest.getNqtTen());
        nqtDichVu.setNqtDonGia(nqtRequest.getNqtDonGia());
        nqtDichVu.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        nqtDichVu.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtDichVu.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        nqtDichVu.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        nqtDichVu.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        
        NqtDichVu nqtSaved = nqtDichVuRepository.save(nqtDichVu);
        return nqtConvertToResponse(nqtSaved);
    }
    
    public NqtDichVuResponse nqtUpdate(Integer nqtId, NqtDichVuRequest nqtRequest) {
        NqtDichVu nqtDichVu = nqtDichVuRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + nqtId));
        
        nqtDichVu.setNqtTen(nqtRequest.getNqtTen());
        nqtDichVu.setNqtDonGia(nqtRequest.getNqtDonGia());
        if (nqtRequest.getNqtHinhAnh() != null) {
            nqtDichVu.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        }
        if (nqtRequest.getNqtStatus() != null) {
            nqtDichVu.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtMetaTitle() != null) {
            nqtDichVu.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        }
        if (nqtRequest.getNqtMetaKeyword() != null) {
            nqtDichVu.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        }
        if (nqtRequest.getNqtMetaDescription() != null) {
            nqtDichVu.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        }
        
        NqtDichVu nqtUpdated = nqtDichVuRepository.save(nqtDichVu);
        return nqtConvertToResponse(nqtUpdated);
    }
    
    public void nqtDelete(Integer nqtId) {
        if (!nqtDichVuRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy dịch vụ với ID: " + nqtId);
        }
        nqtDichVuRepository.deleteById(nqtId);
    }
    
    private NqtDichVuResponse nqtConvertToResponse(NqtDichVu nqtDichVu) {
        NqtDichVuResponse nqtResponse = new NqtDichVuResponse();
        nqtResponse.setNqtId(nqtDichVu.getNqtId());
        nqtResponse.setNqtTen(nqtDichVu.getNqtTen());
        nqtResponse.setNqtDonGia(nqtDichVu.getNqtDonGia());
        nqtResponse.setNqtHinhAnh(nqtDichVu.getNqtHinhAnh());
        nqtResponse.setNqtStatus(nqtDichVu.getNqtStatus());
        nqtResponse.setNqtMetaTitle(nqtDichVu.getNqtMetaTitle());
        nqtResponse.setNqtMetaKeyword(nqtDichVu.getNqtMetaKeyword());
        nqtResponse.setNqtMetaDescription(nqtDichVu.getNqtMetaDescription());
        return nqtResponse;
    }
}

