package org.example.yhw.bookstorecrud.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.mapper.BookMapper;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.service.AuthorService;
import org.example.yhw.bookstorecrud.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final AuthorService authorService;

    public BookRestController(BookService bookService, BookMapper bookMapper, AuthorService authorService) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.authorService = authorService;
    }

    @GetMapping
    public Page<BookDTO> getBooks(@RequestParam(required = false) String blurry, Pageable pageable){
        BookCriteria criteria = new BookCriteria();
        criteria.setBlurry(blurry);
        return bookService.searchBooks(criteria, pageable);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id){
        return bookService.getBookById(id)
                .map(bookMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createOrUpdateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO){

        Author author = authorService.getAuthorById(bookDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id: " + bookDTO.getAuthorId()));

        Book book = bookMapper.toEntity(bookDTO);
        book.setAuthor(author);
        bookMapper.setDates(book);

        if (id == 0) {
            Book saved = bookService.saveBook(book);
            return ResponseEntity.status(201).body(bookMapper.toDto(saved));
        } else {
            return bookService.getBookById(id)
                    .map(existingBook -> {
                        book.setId(id);
                        Book updated = bookService.saveBook(book);
                        return ResponseEntity.ok(bookMapper.toDto(updated));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isPresent()) {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
