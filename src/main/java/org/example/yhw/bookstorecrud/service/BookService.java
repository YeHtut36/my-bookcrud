package org.example.yhw.bookstorecrud.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.mapper.BookMapper;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.query.QueryHelper;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.repository.BookRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Cacheable(value = "bookSearchCache", key = "#criteria.searchValue + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BookDTO> searchBooks(BookCriteria criteria, Pageable pageable) {
        Specification<Book> specification = (root, query, cb) -> QueryHelper.getPredicate(root, criteria, query, cb);
        return bookRepository.findAll(specification, pageable)
                .map(bookMapper::toDto);
    }

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    public long countAllBooks() {
        return bookRepository.count();
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
