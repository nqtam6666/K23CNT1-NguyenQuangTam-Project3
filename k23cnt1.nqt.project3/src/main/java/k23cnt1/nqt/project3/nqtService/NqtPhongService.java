package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtPhongRequest;
import k23cnt1.nqt.project3.nqtDto.NqtPhongResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtLoaiPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtLoaiPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtPhongService {
    
    @Autowired
    private NqtPhongRepository nqtPhongRepository;
    
    @Autowired
    private NqtLoaiPhongRepository nqtLoaiPhongRepository;
    
    public List<NqtPhongResponse> nqtGetAll() {
        return nqtPhongRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }
    
    public NqtPhongResponse nqtGetById(Integer nqtId) {
        NqtPhong nqtPhong = nqtPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + nqtId));
        return nqtConvertToResponse(nqtPhong);
    }
    
    public NqtPhongResponse nqtCreate(NqtPhongRequest nqtRequest) {
        if (nqtPhongRepository.existsByNqtSoPhong(nqtRequest.getNqtSoPhong())) {
            throw new RuntimeException("Số phòng đã tồn tại: " + nqtRequest.getNqtSoPhong());
        }
        
        NqtPhong nqtPhong = new NqtPhong();
        nqtPhong.setNqtSoPhong(nqtRequest.getNqtSoPhong());
        nqtPhong.setNqtTenPhong(nqtRequest.getNqtTenPhong());
        
        if (nqtRequest.getNqtLoaiPhongId() != null) {
            NqtLoaiPhong nqtLoaiPhong = nqtLoaiPhongRepository.findById(nqtRequest.getNqtLoaiPhongId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + nqtRequest.getNqtLoaiPhongId()));
            nqtPhong.setNqtLoaiPhong(nqtLoaiPhong);
        }
        
        nqtPhong.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        
        NqtPhong nqtSaved = nqtPhongRepository.save(nqtPhong);
        return nqtConvertToResponse(nqtSaved);
    }
    
    public NqtPhongResponse nqtUpdate(Integer nqtId, NqtPhongRequest nqtRequest) {
        NqtPhong nqtPhong = nqtPhongRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + nqtId));
        
        // Kiểm tra số phòng trùng (nếu thay đổi)
        if (!nqtPhong.getNqtSoPhong().equals(nqtRequest.getNqtSoPhong()) &&
            nqtPhongRepository.existsByNqtSoPhong(nqtRequest.getNqtSoPhong())) {
            throw new RuntimeException("Số phòng đã tồn tại: " + nqtRequest.getNqtSoPhong());
        }
        
        nqtPhong.setNqtSoPhong(nqtRequest.getNqtSoPhong());
        nqtPhong.setNqtTenPhong(nqtRequest.getNqtTenPhong());
        
        if (nqtRequest.getNqtLoaiPhongId() != null) {
            NqtLoaiPhong nqtLoaiPhong = nqtLoaiPhongRepository.findById(nqtRequest.getNqtLoaiPhongId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + nqtRequest.getNqtLoaiPhongId()));
            nqtPhong.setNqtLoaiPhong(nqtLoaiPhong);
        }
        
        if (nqtRequest.getNqtStatus() != null) {
            nqtPhong.setNqtStatus(nqtRequest.getNqtStatus());
        }
        
        NqtPhong nqtUpdated = nqtPhongRepository.save(nqtPhong);
        return nqtConvertToResponse(nqtUpdated);
    }
    
    public void nqtDelete(Integer nqtId) {
        if (!nqtPhongRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy phòng với ID: " + nqtId);
        }
        nqtPhongRepository.deleteById(nqtId);
    }
    
    private NqtPhongResponse nqtConvertToResponse(NqtPhong nqtPhong) {
        NqtPhongResponse nqtResponse = new NqtPhongResponse();
        nqtResponse.setNqtId(nqtPhong.getNqtId());
        nqtResponse.setNqtSoPhong(nqtPhong.getNqtSoPhong());
        nqtResponse.setNqtTenPhong(nqtPhong.getNqtTenPhong());
        if (nqtPhong.getNqtLoaiPhong() != null) {
            nqtResponse.setNqtLoaiPhongId(nqtPhong.getNqtLoaiPhong().getNqtId());
            nqtResponse.setNqtTenLoaiPhong(nqtPhong.getNqtLoaiPhong().getNqtTenLoaiPhong());
        }
        nqtResponse.setNqtStatus(nqtPhong.getNqtStatus());
        return nqtResponse;
    }
}

