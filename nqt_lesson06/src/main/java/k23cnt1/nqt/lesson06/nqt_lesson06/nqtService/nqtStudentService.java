package k23cnt1.nqt.lesson06.nqt_lesson06.nqtService;

import k23cnt1.nqt.lesson06.nqt_lesson06.nqtDto.nqtStudentDTO;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtEntity.nqtStudent;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtRepository.nqtStudentRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
@NoArgsConstructor
public class nqtStudentService {
    private nqtStudentRepository nqtStudentRepository;
    @Autowired
    public nqtStudentService(nqtStudentRepository studentRepository) {
        this.nqtStudentRepository = studentRepository;
    }
    public List<nqtStudent> findAll() {
        return nqtStudentRepository.findAll();
    }
    public Optional<nqtStudentDTO> findById(Long id) {
        nqtStudent student =
                nqtStudentRepository.findById(id).orElse(null);
        nqtStudentDTO studentDTO = new nqtStudentDTO();
        studentDTO.setId(id);
        studentDTO.setName(student.getName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setAge(student.getAge());
        return Optional.of(studentDTO);
    }
    public Boolean save(nqtStudentDTO studentDTO) {
        nqtStudent student = new nqtStudent();
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setAge(studentDTO.getAge());
        try {
            nqtStudentRepository.save(student);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    public nqtStudent updateStudentById(Long id, nqtStudentDTO
            updatedStudent) {
        return nqtStudentRepository.findById(id)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setEmail(updatedStudent.getEmail());
                    student.setAge(updatedStudent.getAge());
                    return nqtStudentRepository.save(student);

                })
                .orElseThrow(() -> new
                        IllegalArgumentException("Invalid student ID: " + id));
    }
    public void deleteStudent(Long id) {
        nqtStudentRepository.deleteById(id);
    }
}