package k23cnt1.nqt.nqt_lesson07.nqtController;
import k23cnt1.nqt.nqt_lesson07.nqtEntity.nqtCategory;
import k23cnt1.nqt.nqt_lesson07.nqtService.nqtCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/category")
public class nqtCategoryController {
    @Autowired
    private nqtCategoryService nqtcategoryService;
    @GetMapping
    public String listCategories(Model model) {
        try {
            System.out.println("Getting categories...");
            var categories = nqtcategoryService.getAllCategories();
            System.out.println("Categories found: " + categories.size());
            model.addAttribute("categories", categories);
        } catch (Exception e) {
            System.err.println("ERROR in listCategories: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("categories", java.util.Collections.emptyList());
            model.addAttribute("error", "Error loading categories: " + e.getMessage());
        }
        return "nqtCategory/category-list";
    }
    @GetMapping("/category-list")
    public String listCategoriesAlt(Model model) {
        model.addAttribute("categories",
                nqtcategoryService.getAllCategories());
        return "nqtCategory/category-list";
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new nqtCategory());
        return "nqtCategory/category-form";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model) {
        model.addAttribute("category",
                nqtcategoryService.getCategoryById(id).orElse(null));
        return "nqtCategory/category-form";
    }
    @PostMapping("/create")
    public String saveCategory(@ModelAttribute("category")
                                   nqtCategory category) {
        nqtcategoryService.saveCategory(category);
        return "redirect:/category";
    }
    @PostMapping("/create/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute nqtCategory category) {
        category.setId(id);
        nqtcategoryService.saveCategory(category);
        return "redirect:/category";
    }
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        nqtcategoryService.deleteCategory(id);
        return "redirect:/category";
    }
}