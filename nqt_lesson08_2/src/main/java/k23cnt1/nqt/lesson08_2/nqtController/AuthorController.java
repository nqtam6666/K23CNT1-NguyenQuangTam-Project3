package k23cnt1.nqt.lesson08_2.nqtController;

import k23cnt1.nqt.lesson08_2.nqtDto.AuthorDto;
import k23cnt1.nqt.lesson08_2.nqtService.AuthorService;
import k23cnt1.nqt.lesson08_2.nqtService.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public String listAuthors(Model model) {
        List<AuthorDto> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    @GetMapping("/new")
    public String showAuthorForm(Model model) {
        AuthorDto author = new AuthorDto();
        model.addAttribute("author", author);
        return "authors/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AuthorDto author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        return "authors/form";
    }

    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute AuthorDto author,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // Xử lý upload file ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = fileUploadService.uploadFile(imageFile);
                    if (imageUrl != null) {
                        // Xóa ảnh cũ nếu có
                        if (author.getId() != null) {
                            try {
                                AuthorDto oldAuthor = authorService.getAuthorById(author.getId());
                                if (oldAuthor.getImgUrl() != null && oldAuthor.getImgUrl().startsWith("/uploads/")) {
                                    fileUploadService.deleteFile(oldAuthor.getImgUrl());
                                }
                            } catch (Exception e) {
                                // Ignore error khi lấy old author
                                System.err.println("Error getting old author: " + e.getMessage());
                            }
                        }
                        author.setImgUrl(imageUrl);
                    }
                } catch (Exception e) {
                    // Log error nhưng vẫn tiếp tục lưu author
                    System.err.println("Error uploading file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            authorService.saveAuthor(author);
            return "redirect:/authors";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving author: " + e.getMessage());
            // Redirect về form với error message
            return "redirect:/authors/new?error=" + e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }
}

