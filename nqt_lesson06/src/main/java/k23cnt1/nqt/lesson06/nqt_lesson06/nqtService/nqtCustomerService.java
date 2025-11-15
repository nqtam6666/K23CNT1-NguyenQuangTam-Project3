package k23cnt1.nqt.lesson06.nqt_lesson06.nqtService;

import k23cnt1.nqt.lesson06.nqt_lesson06.nqtDto.nqtCustomerDTO;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtEntity.nqtCustomer;
import k23cnt1.nqt.lesson06.nqt_lesson06.nqtRepository.nqtCustomerRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class nqtCustomerService {
    private nqtCustomerRepository nqtCustomerRepository;

    @Autowired
    public nqtCustomerService(nqtCustomerRepository customerRepository) {
        this.nqtCustomerRepository = customerRepository;
    }

    public List<nqtCustomer> findAll() {
        return nqtCustomerRepository.findAll();
    }

    public Optional<nqtCustomerDTO> findById(Long id) {
        nqtCustomer customer = nqtCustomerRepository.findById(id).orElse(null);
        if (customer == null) {
            return Optional.empty();
        }
        nqtCustomerDTO customerDTO = new nqtCustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setUsername(customer.getUsername());
        customerDTO.setPassword(customer.getPassword());
        customerDTO.setFullName(customer.getFullName());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setBirthDay(customer.getBirthDay());
        customerDTO.setActive(customer.getActive());
        return Optional.of(customerDTO);
    }

    public Boolean save(nqtCustomerDTO customerDTO) {
        nqtCustomer customer = new nqtCustomer();
        customer.setUsername(customerDTO.getUsername());
        customer.setPassword(customerDTO.getPassword());
        customer.setFullName(customerDTO.getFullName());
        customer.setAddress(customerDTO.getAddress());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setBirthDay(customerDTO.getBirthDay());
        customer.setActive(customerDTO.getActive() != null ? customerDTO.getActive() : true);
        try {
            nqtCustomerRepository.save(customer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public nqtCustomer updateCustomerById(Long id, nqtCustomerDTO updatedCustomer) {
        return nqtCustomerRepository.findById(id)
                .map(customer -> {
                    customer.setUsername(updatedCustomer.getUsername());
                    customer.setPassword(updatedCustomer.getPassword());
                    customer.setFullName(updatedCustomer.getFullName());
                    customer.setAddress(updatedCustomer.getAddress());
                    customer.setPhone(updatedCustomer.getPhone());
                    customer.setEmail(updatedCustomer.getEmail());
                    customer.setBirthDay(updatedCustomer.getBirthDay());
                    customer.setActive(updatedCustomer.getActive());
                    return nqtCustomerRepository.save(customer);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID: " + id));
    }

    public void deleteCustomer(Long id) {
        nqtCustomerRepository.deleteById(id);
    }
}

