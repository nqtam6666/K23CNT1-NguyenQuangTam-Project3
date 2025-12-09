package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtBlog;
import k23cnt1.nqt.project3.nqtRepository.NqtBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class NqtKhachBlogController {

    @Autowired
    private NqtBlogRepository nqtBlogRepository;

    // Blog Listing
    @GetMapping("/nqtTinTuc")
    public String nqtTinTuc(@RequestParam(value = "search", required = false) String search,
            Model model) {
        List<NqtBlog> blogs;

        if (search != null && !search.isEmpty()) {
            // Search by title or content
            blogs = nqtBlogRepository.findByNqtTieuDeContainingOrNqtNoiDungContaining(search, search);
        } else {
            // Show all published blogs
            blogs = nqtBlogRepository.findByNqtStatusOrderByNqtNgayTaoDesc(true);
        }

        model.addAttribute("blogs", blogs);
        model.addAttribute("searchQuery", search);

        return "nqtCustomer/nqtTinTuc/nqtList";
    }

    // Blog Detail
    @GetMapping("/nqtTinTuc/{id}")
    public String nqtTinTucDetail(@PathVariable("id") Integer id, Model model) {
        Optional<NqtBlog> blogOptional = nqtBlogRepository.findById(id);

        if (blogOptional.isEmpty() || !blogOptional.get().getNqtStatus()) {
            return "redirect:/nqtTinTuc";
        }

        NqtBlog blog = blogOptional.get();

        // Get related blogs (latest 3, excluding current)
        List<NqtBlog> relatedBlogs = nqtBlogRepository.findByNqtStatusOrderByNqtNgayTaoDesc(true)
                .stream()
                .filter(b -> !b.getNqtId().equals(id))
                .limit(3)
                .toList();

        model.addAttribute("blog", blog);
        model.addAttribute("relatedBlogs", relatedBlogs);

        return "nqtCustomer/nqtTinTuc/nqtDetail";
    }
}
