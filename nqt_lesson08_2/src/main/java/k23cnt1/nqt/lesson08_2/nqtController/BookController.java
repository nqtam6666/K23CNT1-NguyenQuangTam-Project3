package k23cnt1.nqt.lesson08_2.nqtController;

import k23cnt1.nqt.lesson08_2.nqtDto.AuthorDto;
import k23cnt1.nqt.lesson08_2.nqtDto.BookDto;
import k23cnt1.nqt.lesson08_2.nqtService.AuthorService;
import k23cnt1.nqt.lesson08_2.nqtService.BookService;
import k23cnt1.nqt.lesson08_2.nqtService.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public String listBooks(Model model) {
        List<BookDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/new")
    public String showBookForm(Model model) {
        BookDto book = new BookDto();
        List<AuthorDto> authors = authorService.getActiveAuthors();
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        return "books/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookDto book = bookService.getBookById(id);
        List<AuthorDto> authors = authorService.getActiveAuthors();
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        return "books/form";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute BookDto book,
                          @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                          @RequestParam(value = "editorId", required = false) Long editorId,
                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (authorIds != null) {
                book.setAuthorIds(authorIds);
            }
            // Chỉ set editorId nếu editorId nằm trong danh sách authorIds đã chọn
            if (editorId != null && authorIds != null && authorIds.contains(editorId)) {
                book.setEditorId(editorId);
            }
            
            // Xử lý upload file ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = fileUploadService.uploadFile(imageFile);
                    if (imageUrl != null) {
                        // Xóa ảnh cũ nếu có
                        if (book.getId() != null) {
                            try {
                                BookDto oldBook = bookService.getBookById(book.getId());
                                if (oldBook.getImgUrl() != null && oldBook.getImgUrl().startsWith("/uploads/")) {
                                    fileUploadService.deleteFile(oldBook.getImgUrl());
                                }
                            } catch (Exception e) {
                                // Ignore error khi lấy old book
                            }
                        }
                        book.setImgUrl(imageUrl);
                    }
                } catch (Exception e) {
                    // Log error nhưng vẫn tiếp tục lưu book
                    System.err.println("Error uploading file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            bookService.saveBook(book);
            return "redirect:/books";
        } catch (Exception e) {
            e.printStackTrace();
            // Redirect về form với error message
            return "redirect:/books/new?error=" + e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}

