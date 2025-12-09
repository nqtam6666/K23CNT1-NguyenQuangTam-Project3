package k23cnt1.nqt.lesson08_2.nqtService;

import k23cnt1.nqt.lesson08_2.nqtDto.AuthorDto;
import k23cnt1.nqt.lesson08_2.nqtEntity.Author;
import k23cnt1.nqt.lesson08_2.nqtRepository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public List<AuthorDto> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<AuthorDto> getActiveAuthors() {
        List<Author> authors = authorRepository.findByIsActiveTrue();
        return authors.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        return convertToDto(author);
    }

    public AuthorDto saveAuthor(AuthorDto authorDto) {
        Author author;
        if (authorDto.getId() != null) {
            author = authorRepository.findById(authorDto.getId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
        } else {
            author = new Author();
        }
        author.setCode(authorDto.getCode());
        author.setName(authorDto.getName());
        author.setDescription(authorDto.getDescription());
        author.setImgUrl(authorDto.getImgUrl());
        author.setEmail(authorDto.getEmail());
        author.setPhone(authorDto.getPhone());
        author.setAddress(authorDto.getAddress());
        author.setIsActive(authorDto.getIsActive() != null ? authorDto.getIsActive() : true);

        author = authorRepository.save(author);
        return convertToDto(author);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDto convertToDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setCode(author.getCode());
        dto.setName(author.getName());
        dto.setDescription(author.getDescription());
        dto.setImgUrl(author.getImgUrl());
        dto.setEmail(author.getEmail());
        dto.setPhone(author.getPhone());
        dto.setAddress(author.getAddress());
        dto.setIsActive(author.getIsActive());
        return dto;
    }
}

