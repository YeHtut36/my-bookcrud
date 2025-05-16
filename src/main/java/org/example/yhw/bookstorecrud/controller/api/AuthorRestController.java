package org.example.yhw.bookstorecrud.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.mapper.AuthorMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.queryCriteria.AuthorCriteria;
import org.example.yhw.bookstorecrud.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public AuthorRestController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @GetMapping
    public Page<AuthorDTO> getAuthors(@RequestParam(required = false) String name, Pageable pageable){
        AuthorCriteria criteria = new AuthorCriteria();
        criteria.setName(name);
        return authorService.searchAuthors(criteria, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id){
        return authorService.getAuthorById(id)
                .map(authorMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createOrUpdateAuthor(
            @PathVariable Long id,
            @RequestBody AuthorDTO authorDTO){

        Author author = authorMapper.toEntity(authorDTO);

        if (id == 0) {
            Author saved = authorService.saveAuthor(author);
            return ResponseEntity.status(201).body(authorMapper.toDto(saved));
        } else {
            return authorService.getAuthorById(id)
                    .map(existingAuthor -> {
                        author.setId(id);
                        Author updated = authorService.saveAuthor(author);
                        return ResponseEntity.ok(authorMapper.toDto(existingAuthor));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id){
        Optional<Author> authorOpt = authorService.getAuthorById(id);
        if (authorOpt.isPresent()) {
            if (authorService.hasBooks(id)) {
                return ResponseEntity.status(409).build();
            }
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
