package k23cnt1.nqt.nqt_lesson07.nqtService;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtCategory;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtProduct;
import k23cnt1.nqt.nqt_lesson07.nqtRepository.nqtProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class nqtProductService {
    @Autowired
    private nqtProductRepository nqtproductRepository;
    // Đọc toàn bộ dữ liệu bảng Product
    public List<nqtProduct> getAllProducts() {
        return nqtproductRepository.findAll();
    }
    // Đọc dữ liệu bảng Product theo id
    public Optional<nqtProduct> findById(Long id) {
        return nqtproductRepository.findById(id);
    }
    // Cập nhật: create / update
    public nqtProduct saveProduct(nqtProduct product) {
        System.out.println(product);
        return nqtproductRepository.save(product);
    }
    // Xóa product theo id
    public void deleteProduct(Long id) {
        nqtproductRepository.deleteById(id);
    }
}