package org.example.yhw.bookstorecrud.repository;

import org.example.yhw.bookstorecrud.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthorRepository extends JpaRepository<Author,Long>, JpaSpecificationExecutor<Author> {

}
