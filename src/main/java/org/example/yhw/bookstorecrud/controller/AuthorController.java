package org.example.yhw.bookstorecrud.controller;

import org.example.yhw.bookstorecrud.exception.ResourceNotFoundException;
import org.example.yhw.bookstorecrud.model.Author;

import org.example.yhw.bookstorecrud.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController( AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(required = false) String search,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authorPage = (search != null && !search.isEmpty())
                ? authorService.searchAuthors(search, pageable)
                : authorService.getAllAuthors(pageable);

        model.addAttribute("authorPage", authorPage);
        model.addAttribute("search", search);
        return "author-list";
    }

    @GetMapping("/new")
    @PreAuthorize( "hasRole('ROLE_ADMIN')")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author-form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize( "hasRole('ROLE_ADMIN')")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        Author author = authorService.getAuthorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        model.addAttribute("author", author);
        return "author-form";
    }

    @PostMapping("/save")
    @PreAuthorize( "hasRole('ROLE_ADMIN')")
    public String saveAuthor(@ModelAttribute Author author) {
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize( "hasRole('ROLE_ADMIN')")
    public String deleteAuthor(@PathVariable Long id) {
//        Author author = authorService.getAuthorById(id);
//        List<Book> books = author.getBooks();
        try {
            if (authorService.hasBooks(id)) {
                return "redirect:/authors?warning=Author has books, cannot be deleted";
            }
            authorService.deleteAuthor(id);
            return "redirect:/authors";
        } catch (ResourceNotFoundException e){
            return "redirect:/authors?error=" + e.getMessage();
        }
    }
}
