package k23cnt1.nqt.lesson08.nqtService;
import k23cnt1.nqt.lesson08.nqtEntity.nqtBook;
import k23cnt1.nqt.lesson08.nqtEntity.nqtAuthor;
import k23cnt1.nqt.lesson08.nqtRepositoty.nqtAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class nqtAuthorService {
    @Autowired
    private nqtAuthorRepository authorRepository;
    public List<nqtAuthor> getAllAuthors() {
        return authorRepository.findAll();
    }
    public nqtAuthor saveAuthor(nqtAuthor author) {
        return authorRepository.save(author);
    }
    public nqtAuthor getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
    public List<nqtAuthor> findAllById(List<Long> ids) {
        return authorRepository.findAllById(ids);
    }
}