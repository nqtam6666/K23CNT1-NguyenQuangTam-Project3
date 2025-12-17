package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtNguoiDungRequest;
import k23cnt1.nqt.project3.nqtDto.NqtNguoiDungResponse;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NqtNguoiDungService {

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public List<NqtNguoiDungResponse> nqtGetAll() {
        return nqtNguoiDungRepository.findAll().stream()
                .map(this::nqtConvertToResponse)
                .collect(Collectors.toList());
    }

    public NqtNguoiDungResponse nqtGetById(Integer nqtId) {
        NqtNguoiDung nqtNguoiDung = nqtNguoiDungRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + nqtId));
        return nqtConvertToResponse(nqtNguoiDung);
    }

    public NqtNguoiDungResponse nqtCreate(NqtNguoiDungRequest nqtRequest) {
        if (nqtNguoiDungRepository.existsByNqtTaiKhoan(nqtRequest.getNqtTaiKhoan())) {
            throw new RuntimeException("Tài khoản đã tồn tại: " + nqtRequest.getNqtTaiKhoan());
        }

        NqtNguoiDung nqtNguoiDung = new NqtNguoiDung();
        nqtNguoiDung.setNqtHoVaTen(nqtRequest.getNqtHoVaTen());
        nqtNguoiDung.setNqtTaiKhoan(nqtRequest.getNqtTaiKhoan());
        nqtNguoiDung.setNqtMatKhau(passwordEncoder.encode(nqtRequest.getNqtMatKhau()));
        nqtNguoiDung.setNqtSoDienThoai(nqtRequest.getNqtSoDienThoai());
        nqtNguoiDung.setNqtEmail(nqtRequest.getNqtEmail());
        nqtNguoiDung.setNqtDiaChi(nqtRequest.getNqtDiaChi());
        nqtNguoiDung.setNqtVaiTro(nqtRequest.getNqtVaiTro() != null ? nqtRequest.getNqtVaiTro() : (byte) 0);
        nqtNguoiDung.setNqtStatus(nqtRequest.getNqtStatus() != null ? nqtRequest.getNqtStatus() : true);
        nqtNguoiDung.setNqtCapBac(nqtRequest.getNqtCapBac() != null && !nqtRequest.getNqtCapBac().isEmpty() 
                ? nqtRequest.getNqtCapBac() : "KhachThuong");
        nqtNguoiDung.setNqtAvatar(nqtRequest.getNqtAvatar());

        NqtNguoiDung nqtSaved = nqtNguoiDungRepository.save(nqtNguoiDung);
        return nqtConvertToResponse(nqtSaved);
    }

    public NqtNguoiDungResponse nqtUpdate(Integer nqtId, NqtNguoiDungRequest nqtRequest) {
        NqtNguoiDung nqtNguoiDung = nqtNguoiDungRepository.findById(nqtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + nqtId));

        // Kiểm tra tài khoản trùng (nếu thay đổi)
        if (!nqtNguoiDung.getNqtTaiKhoan().equals(nqtRequest.getNqtTaiKhoan()) &&
                nqtNguoiDungRepository.existsByNqtTaiKhoan(nqtRequest.getNqtTaiKhoan())) {
            throw new RuntimeException("Tài khoản đã tồn tại: " + nqtRequest.getNqtTaiKhoan());
        }

        nqtNguoiDung.setNqtHoVaTen(nqtRequest.getNqtHoVaTen());
        nqtNguoiDung.setNqtTaiKhoan(nqtRequest.getNqtTaiKhoan());
        if (nqtRequest.getNqtMatKhau() != null && !nqtRequest.getNqtMatKhau().isEmpty()) {
            nqtNguoiDung.setNqtMatKhau(passwordEncoder.encode(nqtRequest.getNqtMatKhau()));
        }
        nqtNguoiDung.setNqtSoDienThoai(nqtRequest.getNqtSoDienThoai());
        nqtNguoiDung.setNqtEmail(nqtRequest.getNqtEmail());
        nqtNguoiDung.setNqtDiaChi(nqtRequest.getNqtDiaChi());
        if (nqtRequest.getNqtVaiTro() != null) {
            nqtNguoiDung.setNqtVaiTro(nqtRequest.getNqtVaiTro());
        }
        if (nqtRequest.getNqtStatus() != null) {
            nqtNguoiDung.setNqtStatus(nqtRequest.getNqtStatus());
        }
        if (nqtRequest.getNqtCapBac() != null && !nqtRequest.getNqtCapBac().isEmpty()) {
            nqtNguoiDung.setNqtCapBac(nqtRequest.getNqtCapBac());
        }
        if (nqtRequest.getNqtAvatar() != null) {
            nqtNguoiDung.setNqtAvatar(nqtRequest.getNqtAvatar());
        }

        NqtNguoiDung nqtUpdated = nqtNguoiDungRepository.save(nqtNguoiDung);
        return nqtConvertToResponse(nqtUpdated);
    }

    public void nqtDelete(Integer nqtId) {
        if (!nqtNguoiDungRepository.existsById(nqtId)) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + nqtId);
        }
        nqtNguoiDungRepository.deleteById(nqtId);
    }

    private NqtNguoiDungResponse nqtConvertToResponse(NqtNguoiDung nqtNguoiDung) {
        NqtNguoiDungResponse nqtResponse = new NqtNguoiDungResponse();
        nqtResponse.setNqtId(nqtNguoiDung.getNqtId());
        nqtResponse.setNqtHoVaTen(nqtNguoiDung.getNqtHoVaTen());
        nqtResponse.setNqtTaiKhoan(nqtNguoiDung.getNqtTaiKhoan());
        nqtResponse.setNqtSoDienThoai(nqtNguoiDung.getNqtSoDienThoai());
        nqtResponse.setNqtEmail(nqtNguoiDung.getNqtEmail());
        nqtResponse.setNqtDiaChi(nqtNguoiDung.getNqtDiaChi());
        nqtResponse.setNqtVaiTro(nqtNguoiDung.getNqtVaiTro());
        nqtResponse.setNqtStatus(nqtNguoiDung.getNqtStatus());
        nqtResponse.setNqtCapBac(nqtNguoiDung.getNqtCapBac() != null ? nqtNguoiDung.getNqtCapBac() : "KhachThuong");
        nqtResponse.setNqtAvatar(nqtNguoiDung.getNqtAvatar());
        return nqtResponse;
    }
}
