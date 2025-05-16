package org.example.yhw.bookstorecrud.service;

import lombok.AllArgsConstructor;
import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.dto.UserRegistrationDTO;
import org.example.yhw.bookstorecrud.mapper.UserMapper;
import org.example.yhw.bookstorecrud.model.User;
import org.example.yhw.bookstorecrud.query.QueryHelper;
import org.example.yhw.bookstorecrud.queryCriteria.UserCriteria;
import org.example.yhw.bookstorecrud.repository.UserRepository;
import org.example.yhw.bookstorecrud.security.JwtTokenProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        validateUser(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        );
    }
    private void validateUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("User password cannot be null or empty");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new IllegalArgumentException("User role cannot be null or empty");
        }
    }

    public Page<UserDTO> searchUsers(UserCriteria criteria, Pageable pageable) {
        Specification<User> specification = (root, query, cb) -> QueryHelper.getPredicate(root, criteria, query, cb);
        return userRepository.findAll(specification, pageable)
                .map(userMapper::toDto);
    }

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        normalizeRole(user);

        if (user.getId() != null) {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            if ((user.getPassword() == null ||user.getPassword().isEmpty()) ) {
                throw new IllegalArgumentException("Password must be provided for new user");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    private void normalizeRole(User user) {
        String role = user.getRole();
        if (role == null || role.isEmpty()) {
            user.setRole("ROLE_USER");
        } else if (!role.startsWith("ROLE_")) {
            user.setRole("ROLE_" + role);
        }
    }

    public Page<User> getAllUsers(Pageable pageable) {
       return userRepository.findAll(pageable);
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

    public void registerNewUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(registrationDTO.getRole().startsWith("ROLE_") ? registrationDTO.getRole() : "ROLE_" + registrationDTO.getRole());

        userRepository.save(user);
    }
    public long countUsers() {
        return userRepository.count();
    }

    public String authenticateAndGenerateToken(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return jwtTokenProvider.generateToken(username);

        } catch (AuthenticationException ex) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

}