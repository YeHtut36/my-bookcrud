package org.example.yhw.bookstorecrud.service;

import lombok.AllArgsConstructor;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.mapper.UserMapper;
import org.example.yhw.bookstorecrud.model.Book;
import org.example.yhw.bookstorecrud.model.User;
import org.example.yhw.bookstorecrud.query.QueryHelper;
import org.example.yhw.bookstorecrud.queryCriteria.BookCriteria;
import org.example.yhw.bookstorecrud.queryCriteria.UserCriteria;
import org.example.yhw.bookstorecrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("User password cannot be null or empty");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new IllegalArgumentException("User role cannot be null or empty");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        );
    }

    public Page<UserDTO> searchUsers(UserCriteria criteria, Pageable pageable) {
        Specification<User> specification = (root, query, cb) -> QueryHelper.getPredicate(root, criteria, query, cb);
        return userRepository.findAll(specification, pageable)
                .map(userMapper::toUserDto);
    }

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String role = user.getRole();
        user.setRole(role == null || role.isEmpty() ? "ROLE_USER"
                : role.startsWith("ROLE_") ? role : "ROLE_" + role);

        if (user.getId() != null) {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users != null ? users : Page.empty(pageable);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

//    public Optional<User> getUserByName(String username) {
//        return userRepository.findByUsername(username);
//    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}