package k23cnt1.nqt.nqt_lesson07.nqtController;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtBook;
import k23cnt1.nqt.nqt_lesson07.nqtService.nqtBookService;
import k23cnt1.nqt.nqt_lesson07.nqtService.nqtCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/book")
public class nqtBookController {
    @Autowired
    private nqtBookService nqtbookService;
    
    @Autowired
    private nqtCategoryService nqtcategoryService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", nqtbookService.getAllBooks());
        return "nqtBook/book-list";
    }

    @GetMapping("/book-list")
    public String listBooksAlt(Model model) {
        model.addAttribute("books", nqtbookService.getAllBooks());
        return "nqtBook/book-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new nqtBook());
        model.addAttribute("categories", nqtcategoryService.getAllCategories());
        return "nqtBook/book-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", nqtbookService.findById(id).orElse(null));
        model.addAttribute("categories", nqtcategoryService.getAllCategories());
        return "nqtBook/book-form";
    }

    @PostMapping("/create")
    public String saveBook(@ModelAttribute("book") nqtBook book,
                           @RequestParam("categoryId") Long categoryId,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        book.setCategory(nqtcategoryService.getCategoryById(categoryId).orElse(null));
        
        // Handle file upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            if (fileName != null) {
                book.setImgUrl("/" + uploadDir + "/" + fileName);
            }
        }
        
        nqtbookService.saveBook(book);
        return "redirect:/book";
    }

    @PostMapping("/create/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute nqtBook book,
                             @RequestParam("categoryId") Long categoryId,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        book.setId(id);
        book.setCategory(nqtcategoryService.getCategoryById(categoryId).orElse(null));
        
        // Handle file upload - only update if new file is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            if (fileName != null) {
                book.setImgUrl("/" + uploadDir + "/" + fileName);
            }
        } else {
            // Keep existing image URL if no new file is uploaded
            nqtBook existingBook = nqtbookService.findById(id).orElse(null);
            if (existingBook != null && existingBook.getImgUrl() != null) {
                book.setImgUrl(existingBook.getImgUrl());
            }
        }
        
        nqtbookService.saveBook(book);
        return "redirect:/book";
    }
    
    private String saveImage(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get("src/main/resources/static/" + uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        nqtbookService.deleteBook(id);
        return "redirect:/book";
    }
}

