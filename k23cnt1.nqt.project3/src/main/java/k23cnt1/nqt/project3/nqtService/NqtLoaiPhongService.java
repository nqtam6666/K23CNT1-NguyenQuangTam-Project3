package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtLoaiPhongRequest;
import k23cnt1.nqt.project3.nqtDto.NqtLoaiPhongResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtLoaiPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtLoaiPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtLoaiPhongService {
    
    @Autowired
    private NqtLoaiPhongRepository nqtLoaiPhongRepository;
    
    public List<NqtLoaiPhongResponse> nqtGetAll() {
        return nqtLoaiPhongRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }
    
    public NqtLoaiPhongResponse nqtGetById(Integer nqtId) {
        NqtLoaiPhong nqtLoaiPhong = nqtLoaiPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + nqtId));
        return nqtConvertToResponse(nqtLoaiPhong);
    }
    
    public NqtLoaiPhongResponse nqtCreate(NqtLoaiPhongRequest nqtRequest) {
        NqtLoaiPhong nqtLoaiPhong = new NqtLoaiPhong();
        nqtLoaiPhong.setNqtTenLoaiPhong(nqtRequest.getNqtTenLoaiPhong());
        nqtLoaiPhong.setNqtGia(nqtRequest.getNqtGia());
        nqtLoaiPhong.setNqtSoNguoi(nqtRequest.getNqtSoNguoi());
        nqtLoaiPhong.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        nqtLoaiPhong.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtLoaiPhong.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        nqtLoaiPhong.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        nqtLoaiPhong.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        
        NqtLoaiPhong nqtSaved = nqtLoaiPhongRepository.save(nqtLoaiPhong);
        return nqtConvertToResponse(nqtSaved);
    }
    
    public NqtLoaiPhongResponse nqtUpdate(Integer nqtId, NqtLoaiPhongRequest nqtRequest) {
        NqtLoaiPhong nqtLoaiPhong = nqtLoaiPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + nqtId));
        
        nqtLoaiPhong.setNqtTenLoaiPhong(nqtRequest.getNqtTenLoaiPhong());
        nqtLoaiPhong.setNqtGia(nqtRequest.getNqtGia());
        nqtLoaiPhong.setNqtSoNguoi(nqtRequest.getNqtSoNguoi());
        nqtLoaiPhong.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        if (nqtRequest.getNqtStatus() != null) {
            nqtLoaiPhong.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtMetaTitle() != null) {
            nqtLoaiPhong.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        }
        if (nqtRequest.getNqtMetaKeyword() != null) {
            nqtLoaiPhong.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        }
        if (nqtRequest.getNqtMetaDescription() != null) {
            nqtLoaiPhong.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        }
        
        NqtLoaiPhong nqtUpdated = nqtLoaiPhongRepository.save(nqtLoaiPhong);
        return nqtConvertToResponse(nqtUpdated);
    }
    
    public void nqtDelete(Integer nqtId) {
        if (!nqtLoaiPhongRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy loại phòng với ID: " + nqtId);
        }
        nqtLoaiPhongRepository.deleteById(nqtId);
    }
    
    private NqtLoaiPhongResponse nqtConvertToResponse(NqtLoaiPhong nqtLoaiPhong) {
        NqtLoaiPhongResponse nqtResponse = new NqtLoaiPhongResponse();
        nqtResponse.setNqtId(nqtLoaiPhong.getNqtId());
        nqtResponse.setNqtTenLoaiPhong(nqtLoaiPhong.getNqtTenLoaiPhong());
        nqtResponse.setNqtGia(nqtLoaiPhong.getNqtGia());
        nqtResponse.setNqtSoNguoi(nqtLoaiPhong.getNqtSoNguoi());
        nqtResponse.setNqtHinhAnh(nqtLoaiPhong.getNqtHinhAnh());
        nqtResponse.setNqtStatus(nqtLoaiPhong.getNqtStatus());
        nqtResponse.setNqtMetaTitle(nqtLoaiPhong.getNqtMetaTitle());
        nqtResponse.setNqtMetaKeyword(nqtLoaiPhong.getNqtMetaKeyword());
        nqtResponse.setNqtMetaDescription(nqtLoaiPhong.getNqtMetaDescription());
        return nqtResponse;
    }
}

