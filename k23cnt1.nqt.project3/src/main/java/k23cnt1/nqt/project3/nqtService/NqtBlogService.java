package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtBlogRequest;
import k23cnt1.nqt.project3.nqtDto.NqtBlogResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtBlog;
import k23cnt1.nqt.project3.nqtRepository.NqtBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtBlogService {
    
    @Autowired
    private NqtBlogRepository nqtBlogRepository;
    
    public List<NqtBlogResponse> nqtGetAll() {
        return nqtBlogRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }
    
    public NqtBlogResponse nqtGetById(Integer nqtId) {
        NqtBlog nqtBlog = nqtBlogRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog với ID: " + nqtId));
        return nqtConvertToResponse(nqtBlog);
    }
    
    public NqtBlogResponse nqtCreate(NqtBlogRequest nqtRequest) {
        NqtBlog nqtBlog = new NqtBlog();
        nqtBlog.setNqtTieuDe(nqtRequest.getNqtTieuDe());
        nqtBlog.setNqtNoiDung(nqtRequest.getNqtNoiDung());
        nqtBlog.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        nqtBlog.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtBlog.setNqtNgayTao(LocalDateTime.now());
        nqtBlog.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        nqtBlog.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        nqtBlog.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        
        NqtBlog nqtSaved = nqtBlogRepository.save(nqtBlog);
        return nqtConvertToResponse(nqtSaved);
    }
    
    public NqtBlogResponse nqtUpdate(Integer nqtId, NqtBlogRequest nqtRequest) {
        NqtBlog nqtBlog = nqtBlogRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog với ID: " + nqtId));
        
        if (nqtRequest.getNqtTieuDe() != null) {
            nqtBlog.setNqtTieuDe(nqtRequest.getNqtTieuDe());
        }
        if (nqtRequest.getNqtNoiDung() != null) {
            nqtBlog.setNqtNoiDung(nqtRequest.getNqtNoiDung());
        }
        if (nqtRequest.getNqtHinhAnh() != null) {
            nqtBlog.setNqtHinhAnh(nqtRequest.getNqtHinhAnh());
        }
        if (nqtRequest.getNqtStatus() != null) {
            nqtBlog.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtMetaTitle() != null) {
            nqtBlog.setNqtMetaTitle(nqtRequest.getNqtMetaTitle());
        }
        if (nqtRequest.getNqtMetaKeyword() != null) {
            nqtBlog.setNqtMetaKeyword(nqtRequest.getNqtMetaKeyword());
        }
        if (nqtRequest.getNqtMetaDescription() != null) {
            nqtBlog.setNqtMetaDescription(nqtRequest.getNqtMetaDescription());
        }
        
        NqtBlog nqtUpdated = nqtBlogRepository.save(nqtBlog);
        return nqtConvertToResponse(nqtUpdated);
    }
    
    public void nqtDelete(Integer nqtId) {
        if (!nqtBlogRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy blog với ID: " + nqtId);
        }
        nqtBlogRepository.deleteById(nqtId);
    }
    
    private NqtBlogResponse nqtConvertToResponse(NqtBlog nqtBlog) {
        NqtBlogResponse nqtResponse = new NqtBlogResponse();
        nqtResponse.setNqtId(nqtBlog.getNqtId());
        nqtResponse.setNqtTieuDe(nqtBlog.getNqtTieuDe());
        nqtResponse.setNqtNoiDung(nqtBlog.getNqtNoiDung());
        nqtResponse.setNqtHinhAnh(nqtBlog.getNqtHinhAnh());
        nqtResponse.setNqtNgayTao(nqtBlog.getNqtNgayTao());
        nqtResponse.setNqtStatus(nqtBlog.getNqtStatus());
        nqtResponse.setNqtMetaTitle(nqtBlog.getNqtMetaTitle());
        nqtResponse.setNqtMetaKeyword(nqtBlog.getNqtMetaKeyword());
        nqtResponse.setNqtMetaDescription(nqtBlog.getNqtMetaDescription());
        return nqtResponse;
    }
}

