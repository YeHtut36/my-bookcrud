package org.example.yhw.bookstorecrud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.exception.ResourceNotFoundException;
import org.example.yhw.bookstorecrud.mapper.AuthorMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.queryCriteria.AuthorCriteria;
import org.example.yhw.bookstorecrud.service.AuthorService;
import org.example.yhw.bookstorecrud.vo.DataTableInput;
import org.example.yhw.bookstorecrud.vo.DataTableOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }
    @GetMapping
    public String showAuthorList() {
        return "author-list";
    }

    @PostMapping(path = "/api",consumes = "application/json", produces = "application/json")
    public ResponseEntity<DataTableOutput<AuthorDTO>> listAuthors(@RequestBody DataTableInput dataTableInput) {
        String searchValue = "";
        if (dataTableInput.getSearch() != null && dataTableInput.getSearch().getValue() != null) {
            searchValue = dataTableInput.getSearch().getValue().trim();
        }

        AuthorCriteria criteria = new AuthorCriteria();
        criteria.setName(searchValue);

        Pageable pageable = dataTableInput.getPageable();

        Page<AuthorDTO> authorPage = authorService.searchAuthors(criteria, pageable);

        long totalRecords = authorService.getAllAuthors(Pageable.unpaged()).getTotalElements();

        DataTableOutput<AuthorDTO> output = DataTableOutput.of(authorPage, totalRecords, dataTableInput);
        output.setDraw(dataTableInput.getDraw());

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        
        AuthorDTO authorDto = authorService.getAuthorById(id)
                .map(authorMapper::toDto)
                                .orElse(new AuthorDTO());

        model.addAttribute("author", authorDto);
        return "author-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveAuthor(@ModelAttribute AuthorDTO authorDto) {

        Author author = authorMapper.toEntity(authorDto);
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteAuthor(@PathVariable Long id) {
        try {
            if (authorService.hasBooks(id)) {
                return "redirect:/authors?warning=Author has books, cannot be deleted";
            }
            authorService.deleteAuthor(id);
            return "redirect:/authors";
        } catch (ResourceNotFoundException e) {
            return "redirect:/authors?error=" + e.getMessage();
        }
    }
}
