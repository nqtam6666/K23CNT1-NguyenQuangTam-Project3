package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtDichVu;
import k23cnt1.nqt.project3.nqtRepository.NqtDichVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class NqtKhachDichVuController {

    @Autowired
    private NqtDichVuRepository nqtDichVuRepository;

    // Services Listing
    @GetMapping("/nqtDichVu")
    public String nqtDichVu(Model model) {
        List<NqtDichVu> services = nqtDichVuRepository.findByNqtStatus(true);
        model.addAttribute("services", services);
        return "nqtCustomer/nqtDichVu/nqtList";
    }

    // Service Detail
    @GetMapping("/nqtDichVu/{id}")
    public String nqtDichVuDetail(@PathVariable("id") Integer id, Model model) {
        Optional<NqtDichVu> serviceOptional = nqtDichVuRepository.findById(id);

        if (serviceOptional.isEmpty() || !serviceOptional.get().getNqtStatus()) {
            return "redirect:/nqtDichVu";
        }

        model.addAttribute("service", serviceOptional.get());
        return "nqtCustomer/nqtDichVu/nqtDetail";
    }
}
