package org.example.yhw.bookstorecrud.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.mapper.BookMapper;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.query.QueryHelper;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Page<BookDTO> searchBooks(BookCriteria criteria, Pageable pageable) {
        Specification<Book> specification = (root, query, cb) -> QueryHelper.getPredicate(root, criteria, query, cb);
        return bookRepository.findAll(specification, pageable)
                .map(bookMapper::toBookDto);
    }

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        log.info("Total books found: {}", books.getTotalElements());
        return books.map(bookMapper::toBookDto);
    }


    public Optional<Book> getBookById(Long id){
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
       return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

}
