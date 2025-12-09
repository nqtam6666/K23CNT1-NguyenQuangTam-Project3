package k23cnt1.nqt.lesson06.nqt_lesson06.nqtController;

import k23cnt1.nqt.lesson06.nqt_lesson06.nqtDto.nqtCustomerDTO;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtService.nqtCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class nqtCustomerController {
    @Autowired
    private nqtCustomerService nqtCustomerService;

    public nqtCustomerController(nqtCustomerService nqtCustomerService) {
        this.nqtCustomerService = nqtCustomerService;
    }

    @GetMapping
    public String getCustomers(Model model) {
        model.addAttribute("customers", nqtCustomerService.findAll());
        return "customers/customer-list";
    }

    @GetMapping("/add-new")
    public String addNewCustomer(Model model) {
        model.addAttribute("customer", new nqtCustomerDTO());
        return "customers/customer-add";
    }

    @GetMapping("/edit/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        nqtCustomerDTO customer = nqtCustomerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        model.addAttribute("customer", customer);
        return "customers/customer-edit";
    }

    @PostMapping
    public String saveCustomer(@ModelAttribute("customer") nqtCustomerDTO customer) {
        nqtCustomerService.save(customer);
        return "redirect:/customers";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable(value = "id") Long id,
                                 @ModelAttribute("customer") nqtCustomerDTO customer) {
        nqtCustomerService.updateCustomerById(id, customer);
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") Long id) {
        nqtCustomerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}

