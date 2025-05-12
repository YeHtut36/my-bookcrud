package org.example.yhw.bookstorecrud.repository;

import org.example.yhw.bookstorecrud.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {

}
