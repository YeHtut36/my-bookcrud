package org.example.yhw.bookstorecrud.repository;

import org.example.yhw.bookstorecrud.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {
   Optional<User> findByUsername(String username);

   boolean existsByUsername(String username);

}

