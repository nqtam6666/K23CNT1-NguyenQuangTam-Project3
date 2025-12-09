package com.nqtam_lab03.nqtam_lab03.nqtcontroller;
import com.nqtam_lab03.nqtam_lab03.nqtentity.nqtStudent;
import com.nqtam_lab03.nqtam_lab03.nqtservice.nqtStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
public class nqtStudentController {
    @Autowired
    private nqtStudentService nqtstudentService;
        @GetMapping("/student-list")
    public List<nqtStudent> getAllStudents() {
        return nqtstudentService.getStudents();
    }
    @GetMapping("/student/{id}")
    public nqtStudent getAllStudents(@PathVariable String id)
    {
        Long param = Long.parseLong(id);
        return nqtstudentService.getStudent(param);
    }
    @PostMapping("/student-add")
    public nqtStudent addStudent(@RequestBody nqtStudent student)
    {
        return nqtstudentService.addStudent(student);
    }
    @PutMapping("/student/{id}")
    public nqtStudent updateStudent(@PathVariable String id,
                                 @RequestBody nqtStudent student) {
        Long param = Long.parseLong(id);
        return nqtstudentService.updateStudent(param,
                student);
    }
    @DeleteMapping("/student/{id}")
    public boolean deleteStudent(@PathVariable String id) {
        Long param = Long.parseLong(id);
        return nqtstudentService.deleteStudent(param);
    }
}