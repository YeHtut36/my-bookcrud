package org.example.yhw.bookstorecrud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.mapper.BookMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.repository.BookRepository;
import org.example.yhw.bookstorecrud.service.AuthorService;
import org.example.yhw.bookstorecrud.service.BookService;
import org.example.yhw.bookstorecrud.vo.DataTableInput;
import org.example.yhw.bookstorecrud.vo.DataTableOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookService bookService , AuthorService authorService, BookMapper bookMapper, BookRepository bookRepository) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String showBookList(Model model, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("books", bookService.getAllBooks(PageRequest.of(0, 10)));
        return "book-list";
    }

    @GetMapping("/api")
    @ResponseBody
    public DataTableOutput<BookDTO> listBooks(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int length,
            @RequestParam(defaultValue = "1") int draw,
            @RequestParam(defaultValue = "", required = false) String searchValue,
            Authentication authentication
                ) {

        log.info("Received DataTable Request: start={}, length={}, search={}", start, length, searchValue);

        Pageable pageable = PageRequest.of(start / length, length, Sort.by("title").ascending());
        Page<BookDTO> bookPage;

        if (searchValue != null && !searchValue.trim().isEmpty()) {
            log.info("Searching for books with keyword: {}", searchValue);
            BookCriteria bookCriteria = new BookCriteria(searchValue);
            bookPage = bookService.searchBooks(bookCriteria, pageable);
        } else {
            log.info("Searching for all books");
            bookPage = bookService.getAllBooks(pageable);
        }

        DataTableOutput<BookDTO> output = new DataTableOutput<>();
        output.setDraw(draw);
        output.setRecordsTotal(bookRepository.count());
        output.setRecordsFiltered(bookPage.getTotalElements());
        output.setData(bookPage.getContent());

        log.info("Book found: {}", bookPage.getTotalElements());

        return output;
    }

    @GetMapping("/new")
    public String createBookForm(Model model, Authentication authentication) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "book-list";
        }
        model.addAttribute("book", new BookDTO());
        model.addAttribute("authors", authorService.getAllAuthors(PageRequest.of(0, 100)));
        return "book-form";
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDTO bookDTO,
                              BindingResult bindingResult, Model model) {
//        Author author = authorRepository.findById(authorId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id: " + authorId));
//        Author author = new Author();
//        author.setId(authorId);
//        book.setAuthor(author);
//
//        if (book.getPublicationDate() == null) {
//            book.setPublicationDate(LocalDate.now());
//        }
//
//        book.setLastUpdated(LocalDateTime.now());
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors(PageRequest.of(0, 100)));
            return "book-form";
        }

        Book book = bookMapper.toBook(bookDTO);
        Author author = authorService.getAuthorById(bookDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id: " + bookDTO.getAuthorId()));

        book.setAuthor(author);
        bookMapper.setDates(book);

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/books?error=You cannot edit books.";
        }

        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
        model.addAttribute("book", bookMapper.toBookDto(book));
        model.addAttribute("authors", authorService.getAllAuthors(PageRequest.of(0, 100)));
        return "book-form";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, Authentication authentication) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/books?error=You cannot delete books.";
        }
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}

