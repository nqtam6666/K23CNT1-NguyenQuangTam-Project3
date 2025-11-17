package k23cnt1.nqt.nqt_lesson07.nqtController;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtProduct;
import k23cnt1.nqt.nqt_lesson07.nqtService.nqtCategoryService;
import k23cnt1.nqt.nqt_lesson07.nqtService.nqtProductService;
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
@RequestMapping("/product")
public class nqtProductController {
    @Autowired
    private nqtProductService nqtproductService;
    
    @Autowired
    private nqtCategoryService nqtcategoryService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products",
                nqtproductService.getAllProducts());
        return "nqtProduct/product-list";
    }

    @GetMapping("/product-list")
    public String listProductsAlt(Model model) {
        model.addAttribute("products",
                nqtproductService.getAllProducts());
        return "nqtProduct/product-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new nqtProduct());
        model.addAttribute("categories", nqtcategoryService.getAllCategories());
        return "nqtProduct/product-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model) {
        model.addAttribute("product",
                nqtproductService.findById(id).orElse(null));
        model.addAttribute("categories", nqtcategoryService.getAllCategories());
        return "nqtProduct/product-form";
    }

    @PostMapping("/create")
    public String saveProduct(@ModelAttribute("product") nqtProduct product,
                              @RequestParam("categoryId") Long categoryId,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        product.setNqtCategory(nqtcategoryService.getCategoryById(categoryId).orElse(null));
        
        // Handle file upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            if (fileName != null) {
                product.setImageUrl("/" + uploadDir + "/" + fileName);
            }
        }
        
        nqtproductService.saveProduct(product);
        return "redirect:/product";
    }

    @PostMapping("/create/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute nqtProduct product,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        product.setId(id);
        product.setNqtCategory(nqtcategoryService.getCategoryById(categoryId).orElse(null));
        
        // Handle file upload - only update if new file is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            if (fileName != null) {
                product.setImageUrl("/" + uploadDir + "/" + fileName);
            }
        } else {
            // Keep existing image URL if no new file is uploaded
            nqtProduct existingProduct = nqtproductService.findById(id).orElse(null);
            if (existingProduct != null && existingProduct.getImageUrl() != null) {
                product.setImageUrl(existingProduct.getImageUrl());
            }
        }
        
        nqtproductService.saveProduct(product);
        return "redirect:/product";
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
    public String deleteProduct(@PathVariable("id") Long id) {
        nqtproductService.deleteProduct(id);
        return "redirect:/product";
    }
}

