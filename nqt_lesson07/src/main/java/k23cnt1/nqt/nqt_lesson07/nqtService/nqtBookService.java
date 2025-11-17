package k23cnt1.nqt.nqt_lesson07.nqtService;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtBook;
import k23cnt1.nqt.nqt_lesson07.nqtRepository.nqtBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class nqtBookService {
    @Autowired
    private nqtBookRepository nqtbookRepository;

    // Đọc toàn bộ dữ liệu bảng Book
    public List<nqtBook> getAllBooks() {
        return nqtbookRepository.findAll();
    }

    // Đọc dữ liệu bảng Book theo id
    public Optional<nqtBook> findById(Long id) {
        return nqtbookRepository.findById(id);
    }

    // Cập nhật: create / update
    public nqtBook saveBook(nqtBook book) {
        return nqtbookRepository.save(book);
    }

    // Xóa book theo id
    public void deleteBook(Long id) {
        nqtbookRepository.deleteById(id);
    }
}

