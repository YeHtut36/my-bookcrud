package org.example.yhw.bookstorecrud.service;


import lombok.AllArgsConstructor;
import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.exception.ResourceNotFoundException;
import org.example.yhw.bookstorecrud.mapper.AuthorMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.query.QueryHelper;
import org.example.yhw.bookstorecrud.queryCriteria.AuthorCriteria;
import org.example.yhw.bookstorecrud.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public Page<AuthorDTO> searchAuthors(AuthorCriteria authorCriteria, Pageable pageable) {
        Specification<Author> specification = (root, query, cb) ->
                QueryHelper.getPredicate(root, authorCriteria, query, cb);
        return authorRepository.findAll(specification, pageable).map(authorMapper::toDto);
    }

    public Page<AuthorDTO> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(authorMapper::toDto);
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    public boolean hasBooks(Long authorId) {
        Author author = getAuthorById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        return author.getBooks() != null && !author.getBooks().isEmpty();
    }
}
