package k23cnt1.nqt.lesson08.nqtService;
import k23cnt1.nqt.lesson08.nqtEntity.nqtBook;
import k23cnt1.nqt.lesson08.nqtRepositoty.nqtBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class nqtBookService {
    @Autowired
    private nqtBookRepository bookRepository;
    public List<nqtBook> getAllBooks() {
        return bookRepository.findAll();
    }
    public nqtBook saveBook(nqtBook book) {
        return bookRepository.save(book);
    }
    public nqtBook getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}