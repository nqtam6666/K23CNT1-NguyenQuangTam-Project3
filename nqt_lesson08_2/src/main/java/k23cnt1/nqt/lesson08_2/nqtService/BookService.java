package k23cnt1.nqt.lesson08_2.nqtService;

import k23cnt1.nqt.lesson08_2.nqtDto.BookDto;
import k23cnt1.nqt.lesson08_2.nqtEntity.Author;
import k23cnt1.nqt.lesson08_2.nqtEntity.Book;
import k23cnt1.nqt.lesson08_2.nqtEntity.BookAuthor;
import k23cnt1.nqt.lesson08_2.nqtRepository.AuthorRepository;
import k23cnt1.nqt.lesson08_2.nqtRepository.BookAuthorRepository;
import k23cnt1.nqt.lesson08_2.nqtRepository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAllWithAuthors();
        return books.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        // Force load bookAuthors to avoid LazyInitializationException
        if (book.getBookAuthors() != null) {
            book.getBookAuthors().size();
        }
        return convertToDto(book);
    }

    @Transactional
    public BookDto saveBook(BookDto bookDto) {
        Book book;
        if (bookDto.getId() != null) {
            book = bookRepository.findById(bookDto.getId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            
            // Xóa các mối quan hệ cũ TRƯỚC KHI update các field khác
            // Clear collection trước để tránh cascade issues
            book.getBookAuthors().clear();
            bookRepository.saveAndFlush(book); // Flush để đảm bảo changes được persist
            
            // Xóa từ database
            bookAuthorRepository.deleteByBookId(book.getId());
            
            // Update các field khác
            book.setCode(bookDto.getCode());
            book.setName(bookDto.getName());
            book.setDescription(bookDto.getDescription());
            book.setImgUrl(bookDto.getImgUrl());
            book.setQuantity(bookDto.getQuantity() != null ? bookDto.getQuantity() : 0);
            book.setPrice(bookDto.getPrice() != null ? bookDto.getPrice() : 0.0);
            book.setIsActive(bookDto.getIsActive() != null ? bookDto.getIsActive() : true);
        } else {
            book = new Book();
            book.setCode(bookDto.getCode());
            book.setName(bookDto.getName());
            book.setDescription(bookDto.getDescription());
            book.setImgUrl(bookDto.getImgUrl());
            book.setQuantity(bookDto.getQuantity() != null ? bookDto.getQuantity() : 0);
            book.setPrice(bookDto.getPrice() != null ? bookDto.getPrice() : 0.0);
            book.setIsActive(bookDto.getIsActive() != null ? bookDto.getIsActive() : true);
        }
        
        // Xử lý null cho quantity và price khi edit
        if (book.getQuantity() == null) {
            book.setQuantity(0);
        }
        if (book.getPrice() == null) {
            book.setPrice(0.0);
        }

        book = bookRepository.save(book);

        // Thêm các tác giả mới
        if (bookDto.getAuthorIds() != null && !bookDto.getAuthorIds().isEmpty()) {
            for (Long authorId : bookDto.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
                // Kiểm tra xem tác giả này có phải là chủ biên không
                Boolean isEditor = (bookDto.getEditorId() != null && bookDto.getEditorId().equals(authorId));
                BookAuthor bookAuthor = new BookAuthor(book, author, isEditor != null ? isEditor : false);
                book.getBookAuthors().add(bookAuthor);
            }
        }

        book = bookRepository.save(book);
        // Force load bookAuthors
        if (book.getBookAuthors() != null) {
            book.getBookAuthors().size();
        }
        return convertToDto(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setCode(book.getCode());
        dto.setName(book.getName());
        dto.setDescription(book.getDescription());
        dto.setImgUrl(book.getImgUrl());
        dto.setQuantity(book.getQuantity());
        dto.setPrice(book.getPrice());
        dto.setIsActive(book.getIsActive());

        List<String> authorNames = new ArrayList<>();
        List<Long> authorIds = new ArrayList<>();
        Long editorId = null;
        if (book.getBookAuthors() != null && !book.getBookAuthors().isEmpty()) {
            for (BookAuthor bookAuthor : book.getBookAuthors()) {
                if (bookAuthor.getAuthor() != null) {
                    String authorName = bookAuthor.getAuthor().getName();
                    Long authorId = bookAuthor.getAuthor().getId();
                    // Đánh dấu chủ biên
                    if (bookAuthor.getIsEditor() != null && bookAuthor.getIsEditor()) {
                        editorId = authorId;
                        authorName = authorName + " (Chủ biên)";
                    }
                    authorNames.add(authorName);
                    authorIds.add(authorId);
                }
            }
        }
        dto.setAuthorNames(authorNames);
        dto.setAuthorIds(authorIds);
        dto.setEditorId(editorId);

        return dto;
    }
}

