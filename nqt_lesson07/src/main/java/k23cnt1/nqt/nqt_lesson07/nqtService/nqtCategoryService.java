package k23cnt1.nqt.nqt_lesson07.nqtService;

import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtCategory;
import k23cnt1.nqt.nqt_lesson07.nqtRepository.nqtCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class nqtCategoryService {

    private final nqtCategoryRepository nqtCategoryRepository;

    @Autowired
    public nqtCategoryService(nqtCategoryRepository nqtCategoryRepository) {
        this.nqtCategoryRepository = nqtCategoryRepository;
    }

    // Lấy danh sách
    public List<nqtCategory> getAllCategories() {
        System.out.println(nqtCategoryRepository.findAll());
        return nqtCategoryRepository.findAll();
    }

    // Lấy category theo id
    public Optional<nqtCategory> getCategoryById(Long id) {
        return nqtCategoryRepository.findById(id);
    }

    // Tạo hoặc cập nhật
    public nqtCategory saveCategory(nqtCategory category) {
        return nqtCategoryRepository.save(category);
    }

    // Xóa theo id
    public void deleteCategory(Long id) {
        nqtCategoryRepository.deleteById(id);
    }
}
