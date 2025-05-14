package org.example.yhw.bookstorecrud.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.mapper.BookMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.queryCriteria.AuthorCriteria;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.repository.BookRepository;
import org.example.yhw.bookstorecrud.service.AuthorService;
import org.example.yhw.bookstorecrud.service.BookService;
import org.example.yhw.bookstorecrud.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<DataTableOutput<BookDTO>> listBooks(@RequestBody DataTableInput dataTableInput) {
        try {
            if (dataTableInput.getOrder() == null || dataTableInput.getOrder().isEmpty()
                    || dataTableInput.getColumns() == null || dataTableInput.getColumns().isEmpty()) {
                dataTableInput.getColumns().add(new Column("title", "", true, true, new Search("", false)));
                dataTableInput.getOrder().add(new Order(0, "asc"));
            }

            Pageable pageable = dataTableInput.getPageable();
            String searchValue = "";
            if (dataTableInput.getSearch() != null && dataTableInput.getSearch().getValue() != null) {
                searchValue = dataTableInput.getSearch().getValue().trim();
            }

            Page<BookDTO> bookPage;
            if (!searchValue.isEmpty()) {
                BookCriteria criteria = new BookCriteria();
                criteria.setBlurry(searchValue);
                bookPage = bookService.searchBooks(criteria, pageable);
            } else {
                bookPage = bookService.getAllBooks(pageable);
            }

            long totalRecords = bookService.countAllBooks();

            DataTableOutput<BookDTO> output = DataTableOutput.of(bookPage, totalRecords, dataTableInput);
            output.setDraw(dataTableInput.getDraw());

            return ResponseEntity.ok(output);

        } catch (Exception e) {
            log.error("Error processing DataTables request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DataTableOutput<>());
        }
    }

    @GetMapping("/api/debug")
    @ResponseBody
    public DataTableOutput<BookDTO> debugBooksApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BookDTO> bookPage;
        if (!search.isEmpty()) {
            bookPage = bookService.searchBooks(new BookCriteria(search), pageable);
        } else {
            bookPage = bookService.getAllBooks(pageable);
        }

        DataTableInput dummyInput = new DataTableInput();
        dummyInput.setDraw(1);
        return DataTableOutput.of(bookPage, bookService.countAllBooks(), dummyInput);
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDTO bookDTO,
                           BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors(PageRequest.of(0, 100)));
            return "book-form";
        }

        Book book = bookMapper.toEntity(bookDTO);
        Author author = authorService.getAuthorById(bookDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id: " + bookDTO.getAuthorId()));

        book.setAuthor(author);
        bookMapper.setDates(book);

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public String editBookForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/books?error=AccessDenied";
        }

//        if(id == null || id == 0){
//            model.addAttribute("book", new BookDTO());
//            model.addAttribute("authors", authorService.getAllAuthors(PageRequest.of(0,100)));
//            return "book-form";
//        }

        BookDTO bookDto = bookService.getBookById(id)
                .map(bookMapper::toDto)
                        .orElse(new BookDTO());

        model.addAttribute("book", bookDto);
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

