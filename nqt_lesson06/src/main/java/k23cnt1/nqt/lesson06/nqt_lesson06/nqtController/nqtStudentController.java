package k23cnt1.nqt.lesson06.nqt_lesson06.nqtController;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtDto.nqtStudentDTO;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtService.nqtStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/students")
public class nqtStudentController {
    @Autowired
    private nqtStudentService nqtStudentService;
    public nqtStudentController(nqtStudentService nqtStudentService) {
        this.nqtStudentService = nqtStudentService;
    }
    @GetMapping
    public String getStudents(Model model) {
        model.addAttribute("students", nqtStudentService.findAll());
        return "students/student-list";
    }
    @GetMapping("/add-new")
    public String addNewStudent(Model model) {
        model.addAttribute("student", new nqtStudentDTO());
        return "students/student-add";
    }
    @GetMapping("/edit/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long
                                            id, Model model) {
        nqtStudentDTO student =
                nqtStudentService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        return "students/student-edit";
    }
    @PostMapping
    public String saveStudent(@ModelAttribute("student") nqtStudentDTO
                                      student) {
        nqtStudentService.save(student);
        return "redirect:/students";
    }
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable(value = "id") Long
                                        id,@ModelAttribute("student") nqtStudentDTO student) {
        nqtStudentService.updateStudentById(id,student);
        return "redirect:/students";
    }
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable(value = "id") Long id)
    {
        nqtStudentService.deleteStudent(id);
        return "redirect:/students";
    }
}